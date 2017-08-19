package pattern.parsing.pattern2_ll1lexer

class Token private(val string: String, val tokenType: TokenType) {
  override def toString: String = s"<'$string',$tokenType>"
}

object Token {

  lazy val Comma    = new Token(TokenCharacter.Comma.toString, TokenType.Comma)
  lazy val LBracket = new Token(TokenCharacter.LBracket.toString, TokenType.LBracket)
  lazy val RBracket = new Token(TokenCharacter.RBracket.toString, TokenType.RBracket)
  lazy val EOF      = new Token("<EOF>", TokenType.EOF)

  def name(str: String): Token = new Token(str, TokenType.Name)

}
