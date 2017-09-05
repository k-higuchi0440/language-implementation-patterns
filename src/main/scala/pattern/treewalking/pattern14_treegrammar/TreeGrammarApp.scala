package pattern.treewalking.pattern14_treegrammar

import antlr3.pattern14_treegrammar.printer.Printer
import antlr3.pattern14_treegrammar.vecmath.{VecMathLexer, VecMathParser}
import org.antlr.runtime.tree.{CommonTree, CommonTreeNodeStream}
import org.antlr.runtime.{ANTLRStringStream, CommonTokenStream}

object TreeGrammarApp extends App {
  val string =
    """
      |x = 3+4
      |print x * [2, 3, 4]
    """.stripMargin

  val stream = new ANTLRStringStream(string)
  val lexer  = new VecMathLexer(stream)
  val tokens = new CommonTokenStream(lexer)
  val parser = new VecMathParser(tokens)
  val scope  = parser.prog
  val tree   = scope.getTree.asInstanceOf[CommonTree]

  println(tree.toStringTree)
  // 出力:
  // (= x (+ 3 4)) (print (* x (VEC 2 3 4)))

  val nodes   = new CommonTreeNodeStream(tree)
  val printer = new Printer(nodes)

  printer.prog()
  // 出力:
  // x = 3+4
  // print x*[2, 3, 4]
}
