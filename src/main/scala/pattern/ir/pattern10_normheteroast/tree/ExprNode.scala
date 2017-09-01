package pattern.ir.pattern10_normheteroast.tree

trait ExprNode extends NormalizedHeteroAST {
  def nodeType: ExprNodeType
  override def toString: String = s"${super.toString}<$nodeType>"
}

sealed trait ExprNodeType
case object TypeInteger extends ExprNodeType
case object TypeVector extends ExprNodeType
