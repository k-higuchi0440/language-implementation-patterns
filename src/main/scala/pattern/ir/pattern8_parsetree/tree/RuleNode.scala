package pattern.ir.pattern8_parsetree.tree

case class RuleNode(
  name: String,
  level: Int,
  children: Option[Vector[ParseTree]],
) extends ParseTree {
  override protected def update(children: Option[Vector[ParseTree]]): RuleNode = copy(children = children)
  override def toString: String =
    s"${Seq.fill(level*4)(" ").mkString}" +
      s"RuleNode($name, children: \n${children.getOrElse(Vector.empty).mkString("\n")})"
}
