package pattern.parsing.pattern3_ll1parser

import pattern.parsing.pattern2_ll1lexer.ListLexer

object LL1ParserApp extends App {
  val lexer  = ListLexer("[apple, [banana, cherry], ]")
  val parser = ListParser(lexer)
  println(parser.parseList)
  // エラー: Failure(java.lang.Exception: expecting NAME or List[...], but found: RBRACK)
}
