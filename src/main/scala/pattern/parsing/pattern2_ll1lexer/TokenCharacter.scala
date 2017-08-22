package pattern.parsing.pattern2_ll1lexer

object TokenCharacter {

  lazy val EOF      = (-1).toChar
  lazy val Comma    = ','
  lazy val LBracket = '['
  lazy val RBracket = ']'
  lazy val Equal    = '='

  def isLetter(c: Char): Boolean = c.toString.matches("""[a-zA-Z]""")
  def isWhiteSpace(c: Char): Boolean = c.toString.matches("""\s""")

}
