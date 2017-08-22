package pattern.parsing.pattern4_llkparser

import pattern.parsing.pattern2_ll1lexer.TokenType._
import pattern.parsing.pattern2_ll1lexer.{ListLexer, Token, TokenType}

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

/**
  * LL(k) リストパーサー: 以下の文法と同じ構文を解析できる
  *
  * list      : '[' elements ']' ;      // ブラケットで囲ったリストと一致する
  * elements  : element(',' element)* ; // カンマ区切りの要素（1つ以上）と一致する
  * element   : assignment              // 代入と一致する
  *           | name                    // 識別子名（トークン）と一致する
  *           | list                    // 入れ子のlistと一致する
  *           ;
  * assignment: name '=' name ;         // elementのnameと最初の構文候補が同じため、2つ先読みする必要がある（LL(2)）
  *
  */
class ListParser private(
  val lexer: ListLexer,
  val k: Int,
  val lookAheadTokens: Vector[Token]) extends LLkParser[ListParser] {

  override protected def consume: ListParser = {
    val (nextToken, nextLexer) = lexer.nextToken
    val nextLookAhead = lookAheadTokens.tail :+ nextToken
    new ListParser(nextLexer, k, nextLookAhead)
  }

  private def check(tokenType: TokenType): Try[ListParser] =
    if(lookAhead(0).tokenType == tokenType) Success(this)
    else Failure(new Exception(s"expecting $tokenType, but found: ${lookAhead(0).tokenType}"))

  private def checkAndConsume(tokenType: TokenType): Try[ListParser] = check(tokenType).map(_.consume)

  // list: '[' elements ']' - ブラケットで囲ったリストと一致する
  def parseList: Try[ListParser] =
    for {
      parsedLBracket    <- checkAndConsume(LBracket)
      parsedAllElements <- parsedLBracket.parseElements
      parsedRBracket    <- parsedAllElements.checkAndConsume(RBracket)
    } yield parsedRBracket

  // elements: element(',' element)* - カンマ区切りの要素（1つ以上）と一致する
  private def parseElements: Try[ListParser] = {
    @tailrec
    def loop(parser: ListParser): Try[ListParser] = {
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
  // elementのnameと最初の構文候補が同じため、2つ先読みする必要がある（LL(2)）
  private def parseElement: Try[ListParser] =
    lookAhead(0).tokenType match {
      case Name     =>
        lookAhead(1).tokenType match {
          case Equal => parseAssignment
          case _     => Success(consume)
        }
      case LBracket => parseList
      case _        => Failure(new Exception(
        s"expecting $Name or List[...], but found: ${lookAhead(0).tokenType}"
      ))
    }

  // assignment: name '=' name - 代入と一致する
  private def parseAssignment: Try[ListParser] =
    for {
      parsedName1  <- checkAndConsume(Name)
      parsedEqual  <- parsedName1.checkAndConsume(Equal)
      parsedName2  <- parsedEqual.checkAndConsume(Name)
    } yield parsedName2

}

object ListParser {
  def apply(lexer: ListLexer, k: Int): ListParser = {
    val (tokens, lex) = lexer.nextTokens(k)
    new ListParser(lex, k, tokens.toVector)
  }
}