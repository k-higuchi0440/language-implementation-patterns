package pattern.ir.pattern11_irregularheteroast.tree

import pattern.ir.pattern10_normheteroast.tree.{ExprNodeType, TypeInteger}
import pattern.ir.pattern9_homoast.tree.Token

// 整数ノードは子が不要なので持たない
case class IntNode(token: Token) extends ExprNode {
  override val nodeType: ExprNodeType = TypeInteger
}
