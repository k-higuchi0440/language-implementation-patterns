package pattern.parsing.pattern3_ll1parser

import pattern.parsing.pattern2_ll1lexer.{LL1Lexer, Token}

trait LL1Parser[Parser <: LL1Parser[Parser]] {
  def lexer: LL1Lexer[_]
  def lookAhead: Token
  protected def consume: Parser
}
