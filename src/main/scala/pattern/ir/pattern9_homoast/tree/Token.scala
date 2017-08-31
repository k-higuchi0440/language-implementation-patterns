package pattern.ir.pattern9_homoast.tree

case class Token (string: String, tokenType: TokenType) {
  override def toString: String = s"<'$string', $tokenType>"
}

sealed trait TokenType
case object Plus extends TokenType
case object One extends TokenType
case object Two extends TokenType
