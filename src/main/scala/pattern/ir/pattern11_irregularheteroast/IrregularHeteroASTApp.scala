package pattern.ir.pattern11_irregularheteroast

import pattern.ir.pattern11_irregularheteroast.tree.{AddNode, IntNode, VectorNode}
import pattern.ir.pattern9_homoast.tree._

object IrregularHeteroASTApp extends App {
  val one    = Token("1", One)
  val two    = Token("2", Two)
  val plus   = Token("+", Plus)
  val vector = Token("vector", TVector)

  val oneAST = IntNode(one)
  val twoAST = IntNode(two)

  val plusAST = AddNode(plus, oneAST, twoAST)
  println(s"1 + 2 tree: $plusAST")
  // 出力: 1 + 2 tree: AST(+ AST(1)<TypeInteger> AST(2)<TypeInteger>)<TypeInteger>

  val vectorAST = VectorNode(vector, Vector(oneAST, twoAST))
  println(s"1 and 2 in vector: $vectorAST")
  // 出力: 1 and 2 in vector: AST(vector AST(1)<TypeInteger> AST(2)<TypeInteger>)<TypeVector>
}
