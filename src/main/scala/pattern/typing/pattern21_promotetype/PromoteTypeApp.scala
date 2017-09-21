package pattern.typing.pattern21_promotetype

import antlr3.pattern21_promotetype.`def`.Def
import antlr3.pattern21_promotetype.`type`.Types
import antlr3.pattern21_promotetype.cymbol.{CymbolLexer, CymbolParser}
import org.antlr.runtime.tree._
import org.antlr.runtime.{ANTLRStringStream, TokenRewriteStream}
import pattern.typing.pattern21_promotetype.ast.{CymbolAdaptor, TreeVisitorActions}
import pattern.typing.pattern21_promotetype.symboltable.SymbolTable
import pattern.typing.pattern22_typesafe.listener.CymbolListener

object PromoteTypeApp extends App {
  val string =
    """float a[];
      |int d[];
      |int c = 'z';
      |void f() {
      |	a[3] = (a[0] + 4) * ('i' + d[0]);
      |	boolean b = a[3 + 'a'] < 3.4;
      |}
      |int g(int x, float y) { return 'k'; }
      |int h(int x) { return x; }
      |struct A { int x; };
      |struct A s;
      |int i;
      |int j = (h(1) + 4) * (a[i] + s.x);
      |float f = a['x'] + g('q',10);
      |f = 3;
      |float ff = g(3,3.4);
      |float gg = d[0];
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
  val computeType = new Types(nodes, defineSymbol.getSymtab, new CymbolListener(tokens))
  computeType.downup(tree)

  val visitor = new TreeVisitor(new CommonTreeAdaptor())

  visitor.visit(tree, TreeVisitorActions.typeAction(tokens))
  /********** 出力 **********
   'z'               type: Char
    3                 type: Int
    a[3]              type: Float
    0                 type: Int
    a[0]              type: Float
    4                 type: Int      promoted to Float
    a[0] + 4          type: Float
    'i'               type: Char     promoted to Int
    0                 type: Int
    d[0]              type: Int
    'i' + d[0]        type: Int
    3                 type: Int
    'a'               type: Char     promoted to Int
    3 + 'a'           type: Int
    a[3 + 'a']        type: Float
    3.4               type: Float
    a[3 + 'a'] < 3.4  type: Boolean
    'k'               type: Char
    x                 type: Int
    1                 type: Int
    h(1)              type: Int
    4                 type: Int
    h(1) + 4          type: Int
    i                 type: Int
    a[i]              type: Float
    s                 type: A
    s.x               type: Int      promoted to Float
    a[i] + s.x        type: Float
    'x'               type: Char     promoted to Int
    a['x']            type: Float
    'q'               type: Char     promoted to Int
    10                type: Int      promoted to Float
    g('q',10)         type: Int      promoted to Float
    a['x'] + g('q',10) type: Float
   *************************/

  // 自動型昇格（ASTを書き換えてアップキャストを挿入）
  visitor.visit(tree, TreeVisitorActions.promoteTypeAction(tokens))
  println(tokens)
  /********** 出力 **********
   float a[];
   int d[];
   int c = (Int)'z';
   void f() {
   	a[3] = (a[0] + (Float)4) * ((Int)'i' + d[0]);
   	boolean b = a[3 + (Int)'a'] < 3.4;
   }
   int g(int x, float y) { return (Int)'k'; }
   int h(int x) { return x; }
   struct A { int x; };
   struct A s;
   int i;
   int j = (h(1) + 4) * (a[i] + (Float)(s.x));
   float f = a[(Int)'x'] + (Float)g((Int)'q',(Float)10);
   f = 3;
   float ff = g(3,3.4);
   float gg = d[0];
   *************************/
}
