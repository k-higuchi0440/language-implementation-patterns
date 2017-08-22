package pattern.parsing.pattern4_llkparser

import pattern.parsing.pattern2_ll1lexer.{LL1Lexer, Token}

trait LLkParser[Parser <: LLkParser[Parser]] {
  def lexer: LL1Lexer[_]
  def k: Int
  def lookAheadTokens: Seq[Token]
  def lookAhead(index: Int): Token = lookAheadTokens(index)
  protected def consume: Parser
}