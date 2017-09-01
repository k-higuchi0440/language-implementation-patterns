package pattern.ir.pattern10_normheteroast.tree

import pattern.ir.pattern9_homoast.tree.Token

trait HeterogeneousAST {
  def token: Token
  override def toString: String = s"AST(${token.string})"
}

trait Normalizing { self: HeterogeneousAST =>
  // 正規化子リスト
  def children: Option[Vector[HeterogeneousAST]]
}

trait NormalizedHeteroAST extends HeterogeneousAST with Normalizing {
  override def toString: String =
    if(children.isEmpty)
      s"AST(${token.string})"
    else
      s"AST(${token.string} ${children.getOrElse(Vector.empty).mkString(" ")})"
}
