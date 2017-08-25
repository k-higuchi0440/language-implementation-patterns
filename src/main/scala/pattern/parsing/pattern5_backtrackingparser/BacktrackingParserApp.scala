package pattern.parsing.pattern5_backtrackingparser

import pattern.parsing.pattern2_ll1lexer.ListLexer

object BacktrackingParserApp extends App {
  val lexer  = ListLexer("[a, b] = [c, d]")
  val parser = ListAndParallelAssignParser(lexer)
  println(parser.parseStatement) // 問題なくパースできる
}