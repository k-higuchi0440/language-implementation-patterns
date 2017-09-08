package pattern.symboltable.pattern16_monolithicscope

import antlr3.pattern16_monolithicscope.cymbl.{CymbolLexer, CymbolParser}
import org.antlr.runtime.{ANTLRStringStream, CommonTokenStream}
import pattern.symboltable.SymbolTable

object MonolithicScopeApp extends App {
  val string =
    """int x = 9;
      |float y;
      |int z = x + 2;
    """.stripMargin

  val stream = new ANTLRStringStream(string)
  val lexer  = new CymbolLexer(stream)
  val tokens = new CommonTokenStream(lexer)
  val parser = new CymbolParser(tokens)

  val symbolTable = parser.compilationUnit(SymbolTable())
  /********** 出力 **********
   line 1: ref int
   line 1: def x
   line 2: ref float
   line 2: def y
   line 3: ref int
   line 3: ref to Some(Symbol('x': int))
   line 3: def z
  *************************/

  println(symbolTable)
  /********** 出力 **********
   SymbolTable(
     float -> Symbol('float': float)
     int -> Symbol('int': int)
     x -> Symbol('x': int)
     y -> Symbol('y': float)
     z -> Symbol('z': int)
   ) [scope: global]
  *************************/
}
