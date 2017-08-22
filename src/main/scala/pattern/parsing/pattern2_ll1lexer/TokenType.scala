package pattern.parsing.pattern2_ll1lexer

sealed trait TokenType

object TokenType {

  case object EOF extends TokenType {
    override def toString: String = "<EOF>"
  }

  case object Name extends TokenType {
    override def toString: String = "NAME"
  }

  case object Comma extends TokenType {
    override def toString: String = "COMMA"
  }

  case object LBracket extends TokenType {
    override def toString: String = "LBRACK"
  }

  case object RBracket extends TokenType {
    override def toString: String = "RBRACK"
  }

  case object Equal extends TokenType {
    override def toString: String = "EQUAL"
  }

}