package pattern.parsing.pattern5_backtrackingparser

import pattern.parsing.pattern2_ll1lexer.{LL1Lexer, Token}

trait BacktrackingParser[Parser <: BacktrackingParser[Parser]] {
  def lexer: LL1Lexer[_]
  def lookAheadTokens: Seq[Token]
  def lookAhead(index: Int): (Token, Parser)
  protected def consume: Parser
}
