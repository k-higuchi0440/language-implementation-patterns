package pattern.ir.pattern10_normheteroast.tree

import pattern.ir.pattern9_homoast.tree.Token

class AddNode private(
  val nodeType: ExprNodeType,
  val token: Token,
  val children: Option[Vector[HeterogeneousAST]], // 正規化子リストを持つ
) extends ExprNode with AddingChild[ExprNode, AddNode] {
  override protected def update(children: Option[Vector[HeterogeneousAST]]): AddNode =
    new AddNode(nodeType, token, children)
}

object AddNode {
  def apply[Node <: ExprNode](addToken: Token, left: Node, right: Node): AddNode =
    new AddNode(left.nodeType, addToken, None)
      .addChild(left)
      .addChild(right)
}