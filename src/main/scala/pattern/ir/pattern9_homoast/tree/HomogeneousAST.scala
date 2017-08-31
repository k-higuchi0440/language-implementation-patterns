package pattern.ir.pattern9_homoast.tree

class HomogeneousAST private(
  val token: Option[Token],
  val children: Option[Vector[HomogeneousAST]],
) {

  def addChild(tree: HomogeneousAST): HomogeneousAST = {
    val added = children.getOrElse(Vector.empty) :+ tree
    new HomogeneousAST(token, Some(added))
  }

  override def toString: String =
    if(children.isEmpty)
      s"AST(${token.map(_.string).getOrElse("")})"
    else
      s"AST(${token.map(_.string + " ").getOrElse("")}${children.getOrElse(Vector.empty).mkString(" ")})"

}

object HomogeneousAST {
  def apply(token: Option[Token]): HomogeneousAST = new HomogeneousAST(token, None)
}