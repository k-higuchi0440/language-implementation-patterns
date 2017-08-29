package pattern.ir.pattern8_parsetree.tree

import pattern.parsing.pattern2_ll1lexer.Token

case class TokenNode(
  token: Token,
  level: Int,
  children: Option[Vector[ParseTree]],
) extends ParseTree {
  override protected def update(children: Option[Vector[ParseTree]]): TokenNode = copy(children = children)
  override def toString: String =
    s"${Seq.fill(level*4)(" ").mkString}" +
      s"TokenNode($token)"
}
