package pattern.treewalking.pattern13_externalvisitor

import pattern.treewalking.pattern13_externalvisitor.tree._

/**
  * 2. 独立した訪問器を構築するために字句型に基づいて訪問器を切り替える
  *
  * "抽象構文木ノード型をやめて字句型に基づいて切り替えるようにすれば、
  *  抽象構文木ノードごとにvisit()メソッドを用意しないで済みます。
  *  代わりに使う新しい振分けメソッドは、訪問器の中に1つだけあります"
  *
  * Visitorではなく単なるユーティリティクラスになったのでVisitorという名前を避けた
  *
  */
object ExternalTreeWalker {

  // ASTを修正せずに、走査とアクションを変更できる
  def print(node: HeterogeneousAST): Unit = {
    printEachNode(node)
    println
  }

  private def printEachNode(node: HeterogeneousAST): Unit =
    node.token.tokenType match {
      case Name   => printEachNode(node.asInstanceOf[NameNode])
      case Int    => printEachNode(node.asInstanceOf[IntNode])
      case Plus   => printEachNode(node.asInstanceOf[PlusNode])
      case Assign => printEachNode(node.asInstanceOf[AssignNode])
    }

  private def printEachNode(node: NameNode): Unit = Console.print(node)

  private def printEachNode(node: IntNode): Unit = Console.print(node)

  private def printEachNode(node: PlusNode): Unit = {
    printEachNode(node.left)
    Console.print(s" PlusNode(${node.token}) ")
    printEachNode(node.right)
  }

  private def printEachNode(node: AssignNode): Unit = {
    printEachNode(node.name)
    Console.print(s" AssignNode(${node.token}) ")
    printEachNode(node.expr)
  }

  // ASTを修正せずに、走査とアクションを変更できる
  def simplePrint(node: HeterogeneousAST): Unit = {
    simplePrintEachNode(node)
    println
  }

  private def simplePrintEachNode(node: HeterogeneousAST): Unit =
    node.token.tokenType match {
      case Name   => simplePrintEachNode(node.asInstanceOf[NameNode])
      case Int    => simplePrintEachNode(node.asInstanceOf[IntNode])
      case Plus   => simplePrintEachNode(node.asInstanceOf[PlusNode])
      case Assign => simplePrintEachNode(node.asInstanceOf[AssignNode])
    }

  private def simplePrintEachNode(node: NameNode): Unit = Console.print(node.token.string)

  private def simplePrintEachNode(node: IntNode): Unit = Console.print(node.token.string)

  private def simplePrintEachNode(node: PlusNode): Unit = {
    simplePrintEachNode(node.left)
    Console.print(s" ${node.token.string} ")
    simplePrintEachNode(node.right)
  }

  private def simplePrintEachNode(node: AssignNode): Unit = {
    simplePrintEachNode(node.name)
    Console.print(s" ${node.token.string} ")
    simplePrintEachNode(node.expr)
  }

}
