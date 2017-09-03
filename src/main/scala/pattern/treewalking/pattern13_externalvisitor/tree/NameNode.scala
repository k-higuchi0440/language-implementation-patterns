package pattern.treewalking.pattern13_externalvisitor.tree

import pattern.treewalking.pattern13_externalvisitor.Token

case class NameNode(token: Token) extends HeterogeneousAST
