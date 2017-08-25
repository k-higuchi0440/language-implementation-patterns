package pattern.parsing.pattern5_backtrackingparser

import pattern.parsing.pattern2_ll1lexer.TokenType._
import pattern.parsing.pattern2_ll1lexer.{ListLexer, Token, TokenType}

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

/**
  * 後戻り リストパーサー: 以下の文法と同じ構文を解析できる
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
class ListAndParallelAssignParser protected(
  val lexer: ListLexer,
  val lookAheadTokens: Vector[Token],
  ) extends BacktrackingParser[ListAndParallelAssignParser] {

  override def lookAhead(index: Int): (Token, ListAndParallelAssignParser) = {
    val scarceTokenCount = (index + 1) - lookAheadTokens.size
    if(scarceTokenCount <= 0) (lookAheadTokens(index), this)
    else {
      val (tokens, lex) = lexer.nextTokens(scarceTokenCount)
      val parser = new ListAndParallelAssignParser(lex, lookAheadTokens ++ tokens)
      parser.lookAhead(index)
    }
  }

  override protected def consume: ListAndParallelAssignParser = {
    val (nextToken, nextLexer) = lexer.nextToken
    val nextLookAhead = lookAheadTokens.tail :+ nextToken
    new ListAndParallelAssignParser(nextLexer, nextLookAhead)
  }

  protected def check(tokenType: TokenType): Try[ListAndParallelAssignParser] = {
    val (token, parser) = lookAhead(0)
    if(token.tokenType == tokenType) Success(parser)
    else Failure(new Exception(s"expecting $tokenType, but found: ${token.tokenType}"))
  }

  protected def checkAndConsume(tokenType: TokenType): Try[ListAndParallelAssignParser] =
    check(tokenType).map(_.consume)

  // statement: list EOF | parallel_assignment EOF - リスト or 並行代入と一致する
  // 最初のトークンがどちらも list なので投機的構文解析が必要
  def parseStatement: Try[ListAndParallelAssignParser] = {
    // 投機的構文解析の候補
    lazy val speculating1 = parseListThenEOF
    lazy val speculating2 = parseParallelAssignmentThenEOF

    if(speculating1.isSuccess) parseListThenEOF
    else if(speculating2.isSuccess) parseParallelAssignmentThenEOF
    else Failure(new Exception(s"expecting statement, but found: ${lookAhead(0)._1}"))
  }

  // list EOF
  def parseListThenEOF: Try[ListAndParallelAssignParser] =
    for {
      parsedList <- parseList
      parsedEOF  <- parsedList.checkAndConsume(EOF)
    } yield parsedEOF

  // parallel_assignment EOF
  def parseParallelAssignmentThenEOF: Try[ListAndParallelAssignParser] =
    for {
      parsedParallel <- parseParallelAssignment
      parsedEOF      <- parsedParallel.checkAndConsume(EOF)
    } yield parsedEOF

  // parallel_assignment:  list '=' list
  def parseParallelAssignment: Try[ListAndParallelAssignParser] =
    for {
      parsedList1 <- parseList
      parsedEqual <- parsedList1.checkAndConsume(Equal)
      parsedList2 <- parsedEqual.parseList
    } yield parsedList2

  // list: '[' elements ']' - ブラケットで囲ったリストと一致する
  def parseList: Try[ListAndParallelAssignParser] =
    for {
      parsedLBracket    <- checkAndConsume(LBracket)
      parsedAllElements <- parsedLBracket.parseElements
      parsedRBracket    <- parsedAllElements.checkAndConsume(RBracket)
    } yield parsedRBracket

  // elements: element(',' element)* - カンマ区切りの要素（1つ以上）と一致する
  protected def parseElements: Try[ListAndParallelAssignParser] = {
    @tailrec
    def loop(parser: ListAndParallelAssignParser): Try[ListAndParallelAssignParser] = {
      parser.checkAndConsume(Comma) match {
        case Success(parsedComma) =>
          val parsedElem = parsedComma.parseElement
          parsedElem match {
            case Success(p) => loop(p)
            case Failure(_) => parsedElem
          }
        case Failure(_)           => Success(parser)
      }
    }
    for {
      parsedFirstElement  <- parseElement
      parsedOtherElements <- loop(parsedFirstElement)
    } yield parsedOtherElements
  }

  // element: assignment | name | list - 代入 or 識別子名（トークン）or 入れ子のlistと一致する
  protected def parseElement: Try[ListAndParallelAssignParser] = {
    lookAhead(0)._1.tokenType match {
      case Name     =>
        lookAhead(1)._1.tokenType match {
          case Equal => parseAssignment
          case _     => Success(consume)
        }
      case LBracket => parseList
      case _        => Failure(new Exception(
        s"expecting $Name or List[...], but found: ${lookAhead(0)._1.tokenType}"
      ))
    }
  }

  // assignment: list '=' name - 代入
  protected def parseAssignment: Try[ListAndParallelAssignParser] =
    for {
      parsedName1  <- checkAndConsume(Name)
      parsedEqual  <- parsedName1.checkAndConsume(Equal)
      parsedName2  <- parsedEqual.checkAndConsume(Name)
    } yield parsedName2

}

object ListAndParallelAssignParser {
  def apply(lexer: ListLexer): ListAndParallelAssignParser = {
    val (tokens, lex) = lexer.nextTokens(10) // アクセスする度リスト生成するとパフォーマンス悪そうなので読んでおく
    new ListAndParallelAssignParser(lex, tokens.toVector)
  }
}