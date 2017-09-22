package pattern.typing.pattern23_polymophictypesafe

import antlr3.pattern23_polymophictypesafe.`def`.Def
import antlr3.pattern23_polymophictypesafe.`type`.Types
import antlr3.pattern23_polymophictypesafe.cymbol.{CymbolLexer, CymbolParser}
import org.antlr.runtime.tree.{CommonTree, CommonTreeAdaptor, CommonTreeNodeStream, TreeVisitor}
import org.antlr.runtime.{ANTLRStringStream, TokenRewriteStream}
import pattern.typing.pattern23_polymophictypesafe.ast.{CymbolAdaptor, CymbolListener, TreeVisitorActions}
import pattern.typing.pattern23_polymophictypesafe.symboltable.SymbolTable

object PolymorphicTypeSafeApp extends App {
  val string =
    """class A { int x; };       // define class A
      |class B : A { int y; };   // define class B subclass of A
      |class C : A { int z; };   // define class C subclass of A
      |void f() {
      |  A a; A a2; B b; C c;    // define 4 object instances
      |  a = a2;        // a, a2 have same type A, so it's ok
      |  a = b;         // b's class is subclass of A but not ptr; NOT ok
      |  b = a;         // a's class is not below B so it's NOT ok
      |  b = c;         // b and c classes are siblings of A; not compatible
      |
      |  A *pA; B *pB; C *pC;    // define 3 object pointers
      |  pA = pB;       // pB's points to B: B is a subclass of A so it's ok
      |  pB = pA;       // pA's points to class not below B so it's NOT ok
      |  pB = pC;       // pB and pC point to sibling classes of A; NOT ok
      |
      |	 float d[];
      |	 float f = d[pA -> x];
      |}
      |
      |void foo() {
      |  int i;
      |  int *pI = &i + 1;
      |  *(pI + 2) = 4; // pI[2] = 4;
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
   [error] line 7 `a (type: A)`, `b (type: B)` have incompatible types: `a = b;`
   [error] line 8 `b (type: B)`, `a (type: A)` have incompatible types: `b = a;`
   [error] line 9 `b (type: B)`, `c (type: C)` have incompatible types: `b = c;`
   [error] line 12 `pB (type: Pointer)`, `pA (type: Pointer)` have incompatible types: `pB = pA;`
   [error] line 13 `pB (type: Pointer)`, `pC (type: Pointer)` have incompatible types: `pB = pC;`
   a         type: A
   a2        type: A
   a         type: A
   b         type: B
   b         type: B
   a         type: A
   b         type: B
   c         type: C
   pA        type: Pointer
   pB        type: Pointer
   pB        type: Pointer
   pA        type: Pointer
   pB        type: Pointer
   pC        type: Pointer
   pA        type: Pointer
   pA        type: A
   pA -> x   type: Int
   d[pA -> x type: Pointer
   d[pA -> x] type: Float
   i         type: Int
   &i        type: Pointer
   1         type: Int
   &i + 1    type: Pointer
   pI        type: Pointer
   2         type: Int
   (pI + 2)  type: Pointer
   *(pI + 2) type: Int
   4         type: Int
   *************************/
}
