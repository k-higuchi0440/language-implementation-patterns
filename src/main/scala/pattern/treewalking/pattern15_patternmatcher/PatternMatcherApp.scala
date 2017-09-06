package pattern.treewalking.pattern15_patternmatcher

import antlr3.pattern15_patternmatcher.reduce.Reduce
import antlr3.pattern15_patternmatcher.simplify.Simplify
import antlr3.pattern15_patternmatcher.vecmath.{VecMathLexer, VecMathParser}
import org.antlr.runtime.tree.{CommonTree, CommonTreeNodeStream}
import org.antlr.runtime.{ANTLRStringStream, CommonTokenStream}

object PatternMatcherApp {

  def main(args: Array[String]): Unit = {
    simplify()
    reduce()
  }

  /**
    * スカラー・ベクトル乗算を書き換えて単純化する
    *
    * 下向きに木を書き換えていき、書き換えが終わって上に行くときに0に変換する
    *
    */
  def simplify(): Unit = {
    val string = "x = 4 * [0, 5*0, 3]"
    val stream = new ANTLRStringStream(string)
    val lexer  = new VecMathLexer(stream)
    val tokens = new CommonTokenStream(lexer)
    val parser = new VecMathParser(tokens)
    val scope  = parser.prog
    val tree   = scope.getTree.asInstanceOf[CommonTree]

    println("Original AST:")
    println(tree.toStringTree)
    // 出力:
    // (= x (* 4 (VEC 0 (* 5 0) 3)))

    val nodes    = new CommonTreeNodeStream(tree)
    val simplify = new Simplify(nodes)

    println("Simplify:")
    val rewrote = simplify.downup(tree, true).asInstanceOf[CommonTree] // 規則採用戦略
    // 出力:
    // (* 4 (VEC 0 (* 5 0) 3)) -> (VEC (* 4 0) (* 4 (* 5 0)) (* 4 3))
    // (* 4 0) -> 0
    // (* 5 0) -> 0
    // (* 4 0) -> 0

    println("Simplified AST:")
    println(rewrote.toStringTree)
    // 出力:
    // (= x (VEC 0 0 (* 4 3)))
  }

  /**
    * 単一の部分木に対して最適化を繰り返し適用する
    *
    * 例: コンパイラの最適化
    * (3 + 3) -> (2 * 3) -> (3 << 1) (ビットシフト)
    *
    * 先に下へ行き、上に行く際に規則を繰り返し適用する
    *
    */
  def reduce(): Unit = {
    val string = "x = 2*(3+3)"
    val stream = new ANTLRStringStream(string)
    val lexer  = new VecMathLexer(stream)
    val tokens = new CommonTokenStream(lexer)
    val parser = new VecMathParser(tokens)
    val scope  = parser.prog
    val tree   = scope.getTree.asInstanceOf[CommonTree]

    println("Original AST:")
    println(tree.toStringTree)
    // 出力:
    // (= x (* 2 (+ 3 3)))

    val nodes  = new CommonTreeNodeStream(tree)
    val reduce = new Reduce(nodes)

    println("Reduce:")
    val rewrote = reduce.downup(tree, true).asInstanceOf[CommonTree] // 規則採用戦略
    // 出力:
    // (+ 3 3) -> (* 2 3)
    // (* 2 3) -> (<< 3 1)
    // (* 2 (<< 3 1)) -> (<< (<< 3 1) 1)
    // (<< (<< 3 1) 1) -> (<< 3 2)

    println("Reduced AST:")
    println(rewrote.toStringTree)
    // 出力:
    // (= x (<< 3 2))
  }

}
