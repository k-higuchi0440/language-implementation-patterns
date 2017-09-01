package pattern.ir.pattern10_normheteroast.tree
import pattern.ir.pattern9_homoast.tree.Token

case class IntNode(token: Token) extends ExprNode {
  override def children: Option[Vector[HeterogeneousAST]] = None
  override val nodeType: ExprNodeType = TypeInteger
}
