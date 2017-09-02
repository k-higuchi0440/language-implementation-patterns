package pattern.ir.pattern11_irregularheteroast.tree

import pattern.ir.pattern10_normheteroast.tree.{ExprNodeType, HeterogeneousAST}

trait ExprNode extends HeterogeneousAST {
  def nodeType: ExprNodeType
  override def toString: String = s"${super.toString}<$nodeType>"
}
