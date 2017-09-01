package pattern.ir.pattern10_normheteroast.tree

trait AddingChild[Child <: HeterogeneousAST, ResultTree <: HeterogeneousAST] { self: NormalizedHeteroAST =>
  def addChild(tree: Child): ResultTree  =
    update(Some(children.getOrElse(Vector.empty) :+ tree))
  protected def update(children: Option[Vector[HeterogeneousAST]]): ResultTree
}
