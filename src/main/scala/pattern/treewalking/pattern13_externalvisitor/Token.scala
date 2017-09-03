package pattern.treewalking.pattern13_externalvisitor

case class Token(string: String, tokenType: TokenType) {
  override def toString: String = s"<'$string', $tokenType>"
}

sealed trait TokenType
case object Name extends TokenType
case object Int extends TokenType
case object Plus extends TokenType
case object Assign extends TokenType
