package pattern.parsing.pattern4_llkparser

import pattern.parsing.pattern2_ll1lexer.ListLexer

object LLkParserApp extends App {
  val lexer  = ListLexer("[a, b = c, [d, e=d]]")
  val parser = ListParser(lexer, k = 2)
  println(parser.parseList) // 問題なくパースできる
}
