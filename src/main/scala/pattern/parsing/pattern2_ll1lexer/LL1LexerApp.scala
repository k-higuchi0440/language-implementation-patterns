package pattern.parsing.pattern2_ll1lexer

object Main extends App {
  val lexer = ListLexer("[ apple, banana  ]")
  lexer.print()
  /********** 出力 **********
    <'[',LBRACK>
    <'apple',NAME>
    <',',COMMA>
    <'banana',NAME>
    <']',RBRACK>
    <'<EOF>',<EOF>>
   *************************/
}
