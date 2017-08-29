package pattern.ir.pattern8_parsetree

import pattern.parsing.pattern2_ll1lexer.ListLexer

object ParseTreeApp extends App {
  val lexer  = ListLexer("[apple, [banana, cherry], dorian]")
  val parser = ListParserWithParseTree(lexer)
  val result = parser.parseList  // 問題なくパースできる
  println(result.map(_.tree.toString)
    .getOrElse("There is no node because the parse failed")
  )
  /********** 出力 **********
  RuleNode(root, children:
    RuleNode(list, children:
        TokenNode(<'[',LBRACK>)
        RuleNode(elements, children:
            TokenNode(<'apple',NAME>)
            TokenNode(<',',COMMA>)
            RuleNode(list, children:
                TokenNode(<'[',LBRACK>)
                RuleNode(elements, children:
                    TokenNode(<'banana',NAME>)
                    TokenNode(<',',COMMA>)
                    TokenNode(<'cherry',NAME>))
                TokenNode(<']',RBRACK>))
            TokenNode(<',',COMMA>)
            TokenNode(<'dorian',NAME>))
        TokenNode(<']',RBRACK>)))
  *************************/
}
