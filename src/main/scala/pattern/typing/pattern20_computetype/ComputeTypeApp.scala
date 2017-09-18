package pattern.typing.pattern20_computetype


import antlr3.pattern20_statictype.`def`.Def
import antlr3.pattern20_statictype.`type`.Types
import antlr3.pattern20_statictype.cymbol.{CymbolLexer, CymbolParser}
import org.antlr.runtime.tree._
import org.antlr.runtime.{ANTLRStringStream, TokenRewriteStream}
import pattern.typing.pattern20_computetype.ast.{CymbolAST, CymbolAdaptor}
import pattern.typing.pattern20_computetype.symboltable.SymbolTable

object ComputeTypeApp extends App {
  val string  =
    """struct A {
      |  int x;
      |  struct B {
      |    int y;
      |    struct C {
      |      char ch;
      |    };
      |    struct C c;
      |  };
      |  struct B b;
      |};
      |int i = 0; int j = 0;
      |void f() {
      |  struct A a;
      |  a.x = 1 + i * j;
      |  a.b.y = 2;
      |  boolean b = 3 == a.x;
      |  if ( i < j ) f();
      |}
      |char g() {
      |  struct A a;
      |  return a.b.c.ch;
      |};
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
  val computeType = new Types(nodes, defineSymbol.getSymtab)
  computeType.downup(tree)

  val visitor = new TreeVisitor(new CommonTreeAdaptor())

  val actions = new TreeVisitorAction() {
    override def pre(t: scala.Any): AnyRef = t.asInstanceOf[AnyRef]
    override def post(t: scala.Any): AnyRef = {
      showTypes(t.asInstanceOf[CymbolAST], tokens)
      t.asInstanceOf[AnyRef]
    }
    def showTypes(node: CymbolAST, tokens: TokenRewriteStream): Unit = {
      if (node.evalType.isDefined && node.getType != CymbolParser.EXPR) {
        printf("%-17s", tokens.toString(node.getTokenStartIndex, node.getTokenStopIndex))
        val ts = node.evalType.map(_.toString).getOrElse("Missing")
        printf(" type: %-8s\n", ts)
      }
    }
  }
  visitor.visit(tree, actions)
  /********** 出力 **********
   0                 type: Int
   0                 type: Int
   a                 type: A
   a.x               type: Int
   1                 type: Int
   i                 type: Int
   j                 type: Int
   i * j             type: Int
   1 + i * j         type: Int
   a                 type: A
   a.b               type: B
   a.b.y             type: Int
   2                 type: Int
   3                 type: Int
   a                 type: A
   a.x               type: Int
   3 == a.x          type: Boolean
   i                 type: Int
   j                 type: Int
   i < j             type: Boolean
   f()               type: Void
   a                 type: A
   a.b               type: B
   a.b.c             type: C
   a.b.c.ch          type: Char
   *************************/
}
