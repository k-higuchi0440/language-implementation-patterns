package pattern.symboltable.pattern19_class

import antlr3.pattern19_class.`def`.Def
import antlr3.pattern19_class.cymbol.{CymbolAdaptor, CymbolLexer, CymbolParser}
import antlr3.pattern19_class.ref.Ref
import antlr3.pattern19_class.symboltable.SymbolTable
import org.antlr.runtime.tree.{CommonTree, CommonTreeNodeStream}
import org.antlr.runtime.{ANTLRStringStream, CommonTokenStream}

object ClassScopeApp extends App {
  val string =
    """class A {
      |public:
      | int x;
      | void foo()
      | { ; }
      | void bar()
      | { ; }
      |};
      |class B : public A {
      | void foo()
      | {
      |   this.x = this.y;  // forward reference
      |   bar();
      | }
      | int y;
      |};
    """.stripMargin

  val stream = new ANTLRStringStream(string)
  val lexer  = new CymbolLexer(stream)
  val tokens = new CommonTokenStream(lexer)
  val parser = new CymbolParser(tokens)
  parser.setTreeAdaptor(new CymbolAdaptor())
  val scope  = parser.compilationUnit()
  val tree   = scope.getTree.asInstanceOf[CommonTree]

  val nodes  = new CommonTreeNodeStream(new CymbolAdaptor(), tree)
  nodes.setTokenStream(tokens)

  val define = new Def(nodes, new SymbolTable())
  define.downup(tree)
  /********** 出力 **********
   line 1: def class A
   line 3: def x
   line 4: def method foo
   locals: []
   args: A.foo()
   line 6: def method bar
   locals: []
   args: A.bar()
   members: class A:{x, foo, bar}
   line 9: def class B
   line 10: def method foo
   locals: []
   args: B.foo()
   line 15: def y
   members: class B:{foo, y}
   *************************/

  nodes.reset()

  val ref = new Ref(nodes)
  ref.downup(tree)
  /********** 出力 **********
   line 1: set A
   line 3: set var type <A.x:global.int>
   line 4: set method type <A.foo():global.void>
   line 6: set method type <A.bar():global.void>
   line 9: set B super to A
   line 10: set method type <B.foo():global.void>
   line 12: resolve this.x to <A.x:global.int>
   line 12: resolve this.y to B.y
   line 13: resolve bar to <A.bar():global.void>
   line 15: set var type <B.y:global.int>
   *************************/
}
