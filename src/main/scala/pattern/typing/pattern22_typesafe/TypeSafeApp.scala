package pattern.typing.pattern22_typesafe

import antlr3.pattern21_promotetype.`def`.Def
import antlr3.pattern22_typesafe.`type`.Types
import antlr3.pattern21_promotetype.cymbol.{CymbolLexer, CymbolParser}
import org.antlr.runtime.tree.{CommonTree, CommonTreeAdaptor, CommonTreeNodeStream, TreeVisitor}
import org.antlr.runtime.{ANTLRStringStream, TokenRewriteStream}
import pattern.typing.pattern21_promotetype.ast.{CymbolAdaptor, TreeVisitorActions}
import pattern.typing.pattern21_promotetype.symboltable.SymbolTable
import pattern.typing.pattern22_typesafe.listener.CymbolListener

object TypeSafeApp extends App {
  val string =
    """void f() {
      |    char c = 4;               // <char> = <int>                 ERROR
      |    boolean b;
      |    int a[];
      |    if ( 3 ) c = 'a';         // if ( <int> ) ...               ERROR
      |    c = 4 + 1.2;              // <char> = <float>               ERROR
      |    b = !c;                   // !<char>                        ERROR
      |    int i = c < b;            // <char> < <boolean>             ERROR
      |    i = -b;                   // -<boolean> (must be int/float) ERROR
      |    g(9);                     // g(<int>) but needs <char>      ERROR
      |    a[true] = 1;              // <array>[<boolean>] = <int>     ERROR
      |}
      |int g(char c) { return 9.2; } // return <float> needs <int>     ERROR
      |
      |struct A { int x; };
      |struct B { int y; };
      |void h() {
      |  struct A a;
      |  struct B b;
      |  a = b;                      // <struct A> = <struct B>          ERROR
      |  int i;
      |  int c = i.x;                // <int>.x                          ERROR
      |  c = a + 3 + a[3];           // <struct> + <int> + <struct>[]    ERROR
      |  c();                        // <int>()                          ERROR
      |}
    """.stripMargin
  val stream = new ANTLRStringStream(string)
  val lexer  = new CymbolLexer(stream)
  val tokens = new TokenRewriteStream(lexer)
  val parser = new CymbolParser(tokens)
  parser.setTreeAdaptor(CymbolAdaptor)
  val scope  = parser.compilationUnit()
  val tree   = scope.getTree.asInstanceOf[CommonTree]

  val nodes  = new CommonTreeNodeStream(tree)
  nodes.setTokenStream(tokens)
  nodes.setTreeAdaptor(CymbolAdaptor)

  val defineSymbol = new Def(nodes, SymbolTable())
  defineSymbol.downup(tree)

  nodes.reset()

  val computeType = new Types(nodes, defineSymbol.symtab, new CymbolListener(tokens))
  computeType.downup(tree)

  val visitor = new TreeVisitor(new CommonTreeAdaptor())

  visitor.visit(tree, TreeVisitorActions.typeAction(tokens))
  /********** 出力 **********
   [error] line 2 `c` have incompatible types: `char c = 4;`
   [error] line 5 if condition `3 (type: Int)` must be boolean: `if ( 3 ) c = 'a';`
   [error] line 6 `c (type: Char)`, `4 + 1.2 (type: Float)` have incompatible types: `c = 4 + 1.2;`
   [error] line 7 `c (type: Char)` must be boolean: `!c`
   [error] line 7 `b (type: Boolean)`, `!c (type: InvalidType)` have incompatible types: `b = !c;`
   [error] line 8 `c (type: Char)`, `b (type: Boolean)` have incompatible types: `c < b`
   [error] line 8 `i` have incompatible types: `int i = c < b;`
   [error] line 9 `b (type: Boolean)` must be int or float: `-b`
   [error] line 9 `i (type: Int)`, `-b (type: InvalidType)` have incompatible types: `i = -b;`
   [error] line 10 `9 (type: Int)` which is argument `c` of method g() must be Char: `g(9)`
   [error] line 11 `true (type: Boolean)` index must be int: `a[true]`
   [error] line 13 `g(): Int` have incompatible types: `return 9.2;`
   [error] line 20 `a (type: A)`, `b (type: B)` have incompatible types: `a = b;`
   [error] line 22 `i (type: Int)` must be struct: `i.x`
   [error] line 22 `c` have incompatible types: `int c = i.x;`
   [error] line 23 `a (type: A)`, `3 (type: Int)` have incompatible types: `a + 3`
   [error] line 23 `a` must be an array variable: `a[3]`
   [error] line 23 `a + 3 (type: InvalidType)`, `a[3] (type: InvalidType)` have incompatible types: `a + 3 + a[3]`
   [error] line 23 `c (type: Int)`, `a + 3 + a[3] (type: InvalidType)` have incompatible types: `c = a + 3 + a[3];`
   [error] line 24 `c` must be a function: `c()`

   // 省略
   *************************/
}
