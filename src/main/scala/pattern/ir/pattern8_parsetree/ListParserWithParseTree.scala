package pattern.ir.pattern8_parsetree

import pattern.ir.pattern8_parsetree.tree.{ParseTree, RuleNode}
import pattern.parsing.pattern2_ll1lexer.{ListLexer, Token, TokenType}
import pattern.parsing.pattern3_ll1parser.LL1Parser

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
class ListParserWithParseTree private(
  val lexer: ListLexer,
  val lookAhead: Token,
  private val currentNode: ParseTree,
) extends LL1Parser[ListParserWithParseTree] {

  lazy val tree: ParseTree = currentNode

  private def copy(
    lexer: ListLexer = this.lexer,
    lookAhead: Token = this.lookAhead,
    currentNode: ParseTree = this.currentNode,
  ): ListParserWithParseTree = new ListParserWithParseTree(
    lexer = lexer,
    lookAhead = lookAhead,
    currentNode = currentNode,
  )

  override protected def consume: ListParserWithParseTree = {
    val (token, lex) = lexer.nextToken
    copy(lexer = lex, lookAhead = token)
  }

  private def check(token: Token): Try[ListParserWithParseTree] = {
    val addedTokenNode = copy(currentNode = currentNode.addChild(token).parent)
    if(this.lookAhead.tokenType == token.tokenType) Success(addedTokenNode)
    else Failure(new Exception(s"expecting ${token.tokenType}, but found: ${addedTokenNode.lookAhead.tokenType}"))
  }

  private def checkAndConsume(token: Token): Try[ListParserWithParseTree] =
    check(token).map(_.consume)

  // list: '[' elements ']' - ブラケットで囲ったリストと一致する
  def parseList: Try[ListParserWithParseTree] = {
    val descent = copy(currentNode = RuleNode("list", currentNode.level + 1, None))
    val parseResult = for {
      parsedLBracket    <- descent.checkAndConsume(Token.LBracket)
      parsedAllElements <- parsedLBracket.parseElements
      parsedRBracket    <- parsedAllElements.checkAndConsume(Token.RBracket)
    } yield parsedRBracket
    parseResult.map(p => p.copy(currentNode = currentNode.addChild(p.currentNode).parent))
  }

  // elements: element(',' element)* - カンマ区切りの要素（1つ以上）と一致する
  private def parseElements: Try[ListParserWithParseTree] = {
    @tailrec
    def loop(parser: ListParserWithParseTree): Try[ListParserWithParseTree] = {
      parser.checkAndConsume(Token.Comma) match {
        case Success(parsedComma) =>
          val parsedElem = parsedComma.parseElement
          parsedElem match {
            case Success(p) => loop(p)
            case Failure(_) => parsedElem
          }
        case Failure(_)           => Success(parser)
      }
    }

    val descent = copy(currentNode = RuleNode("elements", currentNode.level + 1, None))
    val parseResult = for {
      parsedFirstElement  <- descent.parseElement
      parsedOtherElements <- loop(parsedFirstElement)
    } yield parsedOtherElements
    parseResult.map(p => p.copy(currentNode = currentNode.addChild(p.currentNode).parent))
  }

  // element: name | list - 識別子名（トークン）or 入れ子のlistと一致する
  private  def parseElement: Try[ListParserWithParseTree] = {
    import TokenType._
    this.lookAhead.tokenType match {
      case Name     => checkAndConsume(lookAhead)
      case LBracket => this.parseList
      case _        => Failure(new Exception(
        s"expecting ${TokenType.Name} or List[...], but found: ${this.lookAhead.tokenType}"
      ))
    }
  }

}

object ListParserWithParseTree {
  def apply(lexer: ListLexer): ListParserWithParseTree = {
    val (token, lex) = lexer.nextToken
    val root = RuleNode("root", 0, None)
    new ListParserWithParseTree(lex, token, root)
  }
}
