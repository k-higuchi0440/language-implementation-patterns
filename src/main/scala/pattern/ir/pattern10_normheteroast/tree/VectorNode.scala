package pattern.ir.pattern10_normheteroast.tree

import pattern.ir.pattern9_homoast.tree.Token

class VectorNode private(
  val token: Token,
  val children: Option[Vector[HeterogeneousAST]], // 正規化子リストを持つ
) extends ExprNode with AddingChild[ExprNode, VectorNode] {
  override def nodeType: ExprNodeType = TypeVector
  override protected def update(children: Option[Vector[HeterogeneousAST]]): VectorNode =
    new VectorNode(token, children)
}

object VectorNode {
  def apply(token: Token): VectorNode =
    new VectorNode(token, None)

  def apply(token: Token, elements: Seq[HeterogeneousAST]): VectorNode =
    new VectorNode(token, Some(elements.toVector))
}