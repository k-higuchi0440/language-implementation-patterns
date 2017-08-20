package pattern.parsing.pattern3_ll1parser

import pattern.parsing.pattern2_ll1lexer.TokenType.{Comma, LBracket, RBracket}
import pattern.parsing.pattern2_ll1lexer.{ListLexer, Token, TokenType}

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

/**
  * LL(1) リストパーサー: 以下の文法と同じ構文を解析できる
  *
  * list    : '[' elements ']' ;      // ブラケットで囲ったリストと一致する
  * elements: element(',' element)* ; // カンマ区切りの要素（1つ以上）と一致する
  * element : name                    // 識別子名（トークン）と一致する
  *         | list                    // 入れ子のlistと一致する
  *         ;
  *
  */
class ListParser protected(
  val lexer: ListLexer,
  val lookAhead: Token,
) extends LL1Parser[ListParser] {

  override protected def consume: ListParser = ListParser.apply(lexer)

  protected def check(tokenType: TokenType): Try[ListParser] =
    if(this.lookAhead.tokenType == tokenType) Success(this)
    else Failure(new Exception(s"expecting $tokenType, but found: ${this.lookAhead.tokenType}"))

  protected def checkAndConsume(tokenType: TokenType): Try[ListParser] = check(tokenType).map(_.consume)

  // list: '[' elements ']' - ブラケットで囲ったリストと一致する
  def parseList: Try[ListParser] =
    for {
      parsedLBracket    <- this.checkAndConsume(LBracket)
      parsedAllElements <- parsedLBracket.parseElements
      parsedRBracket    <- parsedAllElements.checkAndConsume(RBracket)
    } yield parsedRBracket

  // elements: element(',' element)* - カンマ区切りの要素（1つ以上）と一致する
  protected def parseElements: Try[ListParser] = {
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
      parsedFirstElement  <- this.parseElement
      parsedOtherElements <- loop(parsedFirstElement)
    } yield parsedOtherElements
  }

  // element: name | list - 識別子名（トークン）or 入れ子のlistと一致する
  protected def parseElement: Try[ListParser] = {
    import TokenType._
    this.lookAhead.tokenType match {
      case Name     => Success(this.consume)
      case LBracket => this.parseList
      case _        => Failure(new Exception(
        s"expecting ${TokenType.Name} or List[...], but found: ${this.lookAhead.tokenType}"
      ))
    }
  }

}

object ListParser {
  def apply(lexer: ListLexer): ListParser = {
    val (token, lex) = lexer.nextToken
    new ListParser(lex, token)
  }
}