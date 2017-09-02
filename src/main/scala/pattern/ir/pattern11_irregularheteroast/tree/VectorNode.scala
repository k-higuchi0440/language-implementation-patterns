package pattern.ir.pattern11_irregularheteroast.tree

import pattern.ir.pattern10_normheteroast.tree._
import pattern.ir.pattern9_homoast.tree.Token

case class VectorNode private (
  token: Token,
  children: Vector[ExprNode], // 正規化された子リストではない
) extends ExprNode {
  override def nodeType: ExprNodeType = TypeVector
  override def toString: String = s"AST(${token.string} ${children.mkString(" ")})" // 走査が共通化できない
  def add(child: ExprNode): VectorNode = copy(children = children :+ child)
}

object VectorNode {
  def apply(token: Token): VectorNode =
    new VectorNode(token, Vector.empty)

  def apply(token: Token, elements: Seq[ExprNode]): VectorNode =
    new VectorNode(token, elements.toVector)
}