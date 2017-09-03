package pattern.treewalking.pattern13_externalvisitor.tree

import pattern.treewalking.pattern13_externalvisitor.{Assign, Token}

case class AssignNode(name: NameNode, expr: HeterogeneousAST) extends HeterogeneousAST {
  val token: Token = Token("=", Assign)
}
