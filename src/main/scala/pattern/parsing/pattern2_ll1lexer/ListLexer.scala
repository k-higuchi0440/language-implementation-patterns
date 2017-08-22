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
        case EOF                     => (Token.EOF, str)
        case Comma                   => (Token.Comma, str.tail)
        case LBracket                => (Token.LBracket, str.tail)
        case RBracket                => (Token.RBracket, str.tail)
        case Equal                   => (Token.Equal, str.tail)
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

  def nextTokens(count: Int): (Seq[Token], ListLexer) = {
    @tailrec
    def loop(lexer: ListLexer, tokens: Vector[Token], cnt: Int): (Seq[Token], ListLexer) = {
      val (nextToken, nextLexer) = lexer.nextToken
      val nextTokens = tokens :+ nextToken
      if (cnt <= 0 || nextToken.tokenType == TokenType.EOF)
        (nextTokens, nextLexer)
      else {
        loop(nextLexer, nextTokens, cnt - 1)
      }
    }
    loop(this, Vector.empty, count)
  }

}
