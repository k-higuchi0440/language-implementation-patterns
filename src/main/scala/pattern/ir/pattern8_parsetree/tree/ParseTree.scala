package pattern.ir.pattern8_parsetree.tree

import pattern.parsing.pattern2_ll1lexer.Token

trait ParseTree {
  def level: Int
  def children: Option[Vector[ParseTree]]
  protected def update(children: Option[Vector[ParseTree]]): ParseTree

  def addChild(token: Token): AddChildResult =
    addChild(TokenNode(token, level + 1, None))

  def addChild(tree: ParseTree): AddChildResult = {
    val added = children.getOrElse(Vector.empty) :+ tree
    AddChildResult(update(Some(added)), tree)
  }

}

case class AddChildResult(parent: ParseTree, child: ParseTree)
