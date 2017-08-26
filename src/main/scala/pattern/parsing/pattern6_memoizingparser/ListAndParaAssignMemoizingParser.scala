package pattern.parsing.pattern6_memoizingparser

import pattern.parsing.pattern2_ll1lexer.TokenType.{EOF, Equal}
import pattern.parsing.pattern2_ll1lexer.{ListLexer, Token}
import pattern.parsing.pattern5_backtrackingparser.ListAndParallelAssignParser
import pattern.parsing.pattern6_memoizingparser.Memo.ParsePosition

import scala.util.{Failure, Success, Try}
/**
  * メモ化 リストパーサー: 以下の文法と同じ構文を解析できる
  *
  * statement : list EOF                 // リスト EOFと一致する
  *           | parallel_assignment EOF  // 並行代入 EOFと一致する
  *           ;
  * list      : '[' elements ']' ;       // ブラケットで囲ったリストと一致する
  * elements  : element(',' element)* ;  // カンマ区切りの要素（1つ以上）と一致する
  * element   : assignment               // 代入と一致する
  *           | name                     // 識別子名（トークン）と一致する
  *           | list                     // 入れ子のlistと一致する
  *           ;
  * assignment: name '=' name ;          // elementのnameと最初の構文候補が同じため、2つ先読みする必要がある（LL(2)）
  * parallel_assignment: list '=' list ; // 並行代入と一致する
  *
  */
class ListAndParaAssignMemoizingParser private(
  val position: ParsePosition,
  val listMemo: Memo[ListAndParaAssignMemoizingParser#MemoResult],
  lexer: ListLexer,
  lookAheadTokens: Vector[Token],
) extends ListAndParallelAssignParser(lexer, lookAheadTokens) {

  type MemoResult = Try[ListAndParaAssignMemoizingParser]

  def copy(
    position: ParsePosition = this.position,
    listMemo: Memo[MemoResult] = this.listMemo,
    lexer: ListLexer = this.lexer,
    lookAheadTokens: Vector[Token] = this.lookAheadTokens,
  ): ListAndParaAssignMemoizingParser =
    new ListAndParaAssignMemoizingParser(position, listMemo, lexer, lookAheadTokens)

  override protected def consume: ListAndParaAssignMemoizingParser = {
    val (nextToken, nextLexer) = lexer.nextToken
    val nextLookAhead = lookAheadTokens.lastOption.fold(Vector(nextToken)){ last =>
      if(last.tokenType == EOF && 1 < lookAheadTokens.size) lookAheadTokens.tail
      else lookAheadTokens.tail :+ nextToken
    }
    copy(lexer = nextLexer, lookAheadTokens = nextLookAhead, position = position + 1)
  }

  // statement: list EOF | parallel_assignment EOF - リスト or 並行代入と一致する
  // 最初のトークンがどちらも list なので投機的構文解析が必要
  override def parseStatement: Try[ListAndParaAssignMemoizingParser] = {
    lazy val (speculate1, memo1) = {
      println("---------- speculate 1 ----------")
      speculateListThenEOF
    }
    lazy val (speculate2, _) = {
      println("---------- speculate 2 ----------")
      this.copy(listMemo = memo1).speculateParallelAssignment
    }
    if(speculate1.isSuccess) {
      println("---------- speculate end --------")
      parseListThenEOF.asInstanceOf[Try[ListAndParaAssignMemoizingParser]]
    } else if(speculate2.isSuccess) {
      println("---------- speculate end --------")
      parseParallelAssignmentThenEOF.asInstanceOf[Try[ListAndParaAssignMemoizingParser]]
    } else {
      println("---------- speculate end --------")
      Failure(new Exception(s"expecting statement, but found: ${lookAhead(0)._1}"))
    }
  }

  // メモ化を利用した投機的構文解析
  private def speculate(
    parse: => Try[ListAndParallelAssignParser]
  ): (Try[ListAndParaAssignMemoizingParser], Memo[MemoResult]) = {
    // find memoized result
    listMemo.get(position) match {
      case Some(memoized) =>
        println(s">> skip parse and jump to position ${memoized.map(_.position).getOrElse("error")}")
        (memoized, listMemo)
      case None           =>
        // parse and memoize its result
        parse match {
          case Success(p) =>
            val parser = p.asInstanceOf[ListAndParaAssignMemoizingParser]
            val memo   = parser.listMemo.memoize(this.position, Success(parser))
            (Success(parser), memo)
          case Failure(ex) =>
            val memo = this.listMemo.memoize(this.position, Failure(ex))
            (Failure(ex), memo)
      }
    }
  }

  private def speculateListThenEOF: (Try[ListAndParallelAssignParser], Memo[MemoResult]) = {
    val (parsedList, memo1) = speculate(parseList)
    val result = for {
      parsedList1 <- parsedList
      parsedEOF   <- parsedList1.checkAndConsume(EOF)
    } yield parsedEOF
    (result, memo1)
  }

  private def speculateParallelAssignment: (Try[ListAndParallelAssignParser], Memo[MemoResult]) = {
    val (parsedList, memo1) = speculate(parseList)
    val result = for {
      parsedList1 <- parsedList
      parsedEqual <- parsedList1.checkAndConsume(Equal)
      parsedList2 <- parsedEqual.parseList
    } yield parsedList2
    (result, memo1)
  }

  override def toString: String = s"${getClass.getSimpleName}(position: $position)"

}

object ListAndParaAssignMemoizingParser {
  def apply(lexer: ListLexer): ListAndParaAssignMemoizingParser = {
    val (tokens, lex) = lexer.nextTokens(10) // アクセスする度リスト生成するとパフォーマンス悪そうなので読んでおく
    new ListAndParaAssignMemoizingParser(0, Memo.apply, lex, tokens.toVector)
  }
}