package pattern.parsing.pattern6_memoizingparser

import pattern.parsing.pattern2_ll1lexer.ListLexer

object MemoizingParserApp extends App {
  val lexer  = ListLexer("[a, b] = [c , d]")
  val parser = ListAndParaAssignMemoizingParser(lexer)
  println(parser.parseStatement) // 問題なくパースできる
  /********** 出力 **********
    ---------- speculate 1 ----------
    read memo @ position 0: None
    write memo @ position 0: Success(ListAndParaAssignMemoizingParser(position: 5))
    ---------- speculate 2 ----------
    read memo @ position 0: Some(Success(ListAndParaAssignMemoizingParser(position: 5)))
    >> skip parse and jump to position 5
    ---------- speculate end --------
    Success(ListAndParaAssignMemoizingParser(position: 12))
  *************************/
}
