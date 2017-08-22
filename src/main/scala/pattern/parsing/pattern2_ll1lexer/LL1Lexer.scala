package pattern.parsing.pattern2_ll1lexer

import scala.annotation.tailrec

abstract class LL1Lexer[Lexer <: LL1Lexer[Lexer]](input: String) {

  def nextToken: (Token, Lexer)
  def nextTokens(count: Int): (Seq[Token], ListLexer)

  lazy val lookAhead: Option[Char] = input.headOption

  protected def nameTokenAndRest(str: String): (Token, String) = {
    val (name, rest) = str.span(TokenCharacter.isLetter)
    (Token.name(name), rest)
  }

  def print(): Unit = {
    @tailrec
    def loop(token: Token, lexer: Lexer): Unit = {
      println(token)
      if(token == Token.EOF) ()
      else {
        val (nextToken, nextLexer) = lexer.nextToken
        loop(nextToken, nextLexer)
      }
    }
    val (token, lexer) = this.nextToken
    loop(token, lexer)
  }

}