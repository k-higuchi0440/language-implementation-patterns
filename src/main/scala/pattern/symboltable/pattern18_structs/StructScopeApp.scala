package pattern.symboltable.pattern18_structs

import antlr3.pattern18_structs.cymbol.{CymbolLexer, CymbolParser}
import antlr3.pattern18_structs.defref.DefRef
import org.antlr.runtime.{ANTLRStringStream, CommonTokenStream}
import org.antlr.runtime.tree.{CommonTree, CommonTreeNodeStream}
import pattern.symboltable.SymbolTable

object StructScopeApp extends App {
  val string =
    """struct A {
      |  int x;
      |  struct B { int y; };
      |  B b;
      |  struct C { int z; };
      |  C c;
      |};
      |A a;
      |
      |void f()
      |{
      |  struct D {
      |    int i;
      |  };
      |  D d;
      |  d.i = a.b.y;
      |}
    """.stripMargin

  val stream = new ANTLRStringStream(string)
  val lexer  = new CymbolLexer(stream)
  val tokens = new CommonTokenStream(lexer)
  val parser = new CymbolParser(tokens)
  val scope  = parser.compilationUnit()
  val tree   = scope.getTree.asInstanceOf[CommonTree]

  val nodes  = new CommonTreeNodeStream(tree)
  nodes.setTokenStream(tokens)

  val defRef = new DefRef(nodes, SymbolTable())
  defRef.downup(tree)
  /********** 出力 **********
   [scope] create A's StructScope
   line 1: def struct A
   line 2: def x
   [scope] create B's StructScope
   line 3: def struct B
   line 3: def y
   [scope] remove B's StructScope(y -> VariableSymbol('y': int))
   line 4: def b
   [scope] create C's StructScope
   line 5: def struct C
   line 5: def z
   [scope] remove C's StructScope(z -> VariableSymbol('z': int))
   line 6: def c
   [scope] remove A's StructScope(B -> StructSymbol('B': B), C -> StructSymbol('C': C), b -> VariableSymbol('b': B), c -> VariableSymbol('c': C), x -> VariableSymbol('x': int))
   line 8: def a
   [scope] create MethodScope
   line 10: def method f
   [scope] create BlockScope
   [scope] create D's StructScope
   line 12: def struct D
   line 13: def i
   [scope] remove D's StructScope(i -> VariableSymbol('i': int))
   line 15: def d
   line 16: ref d = VariableSymbol('d': D)
   line 16: ref D.i = VariableSymbol('i': int)
   line 16: assign to type int
   line 16: ref a = VariableSymbol('a': A)
   line 16: ref A.b = VariableSymbol('b': B)
   line 16: ref B.y = VariableSymbol('y': int)
   [scope] remove BlockScope(D -> StructSymbol('D': D), d -> VariableSymbol('d': D))
   [scope] remove MethodScope()
   *************************/
}
