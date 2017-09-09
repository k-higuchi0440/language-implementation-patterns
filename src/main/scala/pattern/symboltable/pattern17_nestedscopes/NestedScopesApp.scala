package pattern.symboltable.pattern17_nestedscopes

import antlr3.pattern17_nestedscopes.cymbol.{CymbolLexer, CymbolParser}
import antlr3.pattern17_nestedscopes.defref.DefRef
import org.antlr.runtime.tree.{CommonTree, CommonTreeNodeStream}
import org.antlr.runtime.{ANTLRStringStream, CommonTokenStream}
import pattern.symboltable.SymbolTable

object NestedScopesApp extends App {
  val string =
    """int i = 9;
      |float f(int x, float y)
      |{
      |    float i;
      |    { float z = x+y; i = z; }
      |    { float z = i+1; i = z; }
      |    return i;
      |}
      |void g()
      |{
      |    f(i,2);
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
   line 1: def i
   [scope] create MethodScope
   line 2: def method f
   line 2: def x
   line 2: def y
   [scope] create BlockScope
   line 4: def i
   [scope] create BlockScope
   line 5: def z
   line 5: ref Symbol('x': int)
   line 5: ref Symbol('y': float)
   line 5: ref Symbol('z': float)
   line 5: assign to Symbol('i': float)
   [scope] remove BlockScope(z -> Symbol('z': float))
   [scope] create BlockScope
   line 6: def z
   line 6: ref Symbol('i': float)
   line 6: ref Symbol('z': float)
   line 6: assign to Symbol('i': float)
   [scope] remove BlockScope(z -> Symbol('z': float))
   line 7: ref Symbol('i': float)
   [scope] remove BlockScope(i -> Symbol('i': float))
   [scope] remove MethodScope(x -> Symbol('x': int), y -> Symbol('y': float))
   [scope] create MethodScope
   line 9: def method g
   [scope] create BlockScope
   line 11: ref MethodSymbol('f': float)
   line 11: ref Symbol('i': int)
   [scope] remove BlockScope()
   [scope] remove MethodScope()
   *************************/
}
