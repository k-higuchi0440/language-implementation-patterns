package pattern.treewalking.pattern13_externalvisitor.tree

import pattern.treewalking.pattern13_externalvisitor.{Plus, Token}

case class PlusNode(left: IntNode, right: IntNode) extends HeterogeneousAST {
  val token: Token = Token("+", Plus)
}
