package pattern.treewalking.pattern13_externalvisitor

import pattern.treewalking.pattern13_externalvisitor.tree.{AssignNode, IntNode, NameNode, PlusNode}

object ExternalTreeWalkerApp extends App {
  val nameNode = NameNode(Token("x", Name))
  val plusNode = PlusNode(IntNode(Token("3", Int)), IntNode(Token("4", Int)))
  val assignNode = AssignNode(nameNode, plusNode)

  // ASTを修正せずに、走査とアクションを変更できる
  ExternalTreeWalker.print(assignNode)
  // 出力:
  // NameNode(<'x', Name>) AssignNode(<'=', Assign>) IntNode(<'3', Int>) PlusNode(<'+', Plus>) IntNode(<'4', Int>)

  // ASTを修正せずに、走査とアクションを変更できる
  ExternalTreeWalker.simplePrint(assignNode)
  // 出力:
  // x = 3 + 4

}
