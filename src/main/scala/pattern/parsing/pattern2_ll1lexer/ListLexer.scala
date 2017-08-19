package pattern.parsing.pattern2_ll1lexer

import scala.annotation.tailrec

case class ListLexer(input: String) extends LL1Lexer[ListLexer](input) {

  override def nextToken: (Token, ListLexer) = {

    import TokenCharacter._

    @tailrec
    def loop(str: String): (Token, String) = {
      val char = str.headOption.getOrElse(EOF)
      char match {
        case _ if isWhiteSpace(char) => loop(str.tail)
        case _ if isLetter(char)     => nameTokenAndRest(str)
        case EOF                     => (Token.EOF, str.tail)
        case Comma                   => (Token.Comma, str.tail)
        case LBracket                => (Token.LBracket, str.tail)
        case RBracket                => (Token.RBracket, str.tail)
        case invalidChar             => throw new Exception(s"Invalid character found: $invalidChar")
      }
    }

    lookAhead match {
      case None    => (Token.EOF, this)
      case Some(_) =>
        val (token, str) = loop(input)
        (token, ListLexer(str))
    }
  }

}
