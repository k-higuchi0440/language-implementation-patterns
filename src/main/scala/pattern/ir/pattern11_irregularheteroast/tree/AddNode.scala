package pattern.ir.pattern11_irregularheteroast.tree

import pattern.ir.pattern10_normheteroast.tree.ExprNodeType
import pattern.ir.pattern9_homoast.tree.Token

class AddNode private(
  val nodeType: ExprNodeType,
  val token: Token,
  val left: ExprNode,  // 非正規された子を持つ
  val right: ExprNode, // 非正規された子を持つ
) extends ExprNode {
  override def toString: String = s"AST(${token.string} $left $right)" // 走査が共通化できない
}

object AddNode {
  def apply[Node <: ExprNode](addToken: Token, left: Node, right: Node): AddNode =
    new AddNode(left.nodeType, addToken, left, right)
}