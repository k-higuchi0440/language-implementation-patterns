package pattern.interpreter.pattern25_treebased.pie.memory

import pattern.interpreter.pattern25_treebased.pie.symbol.StructSymbol

case class StructInstanceMemorySpace(struct: StructSymbol) extends MemorySpace {

  struct.members.value.foreach { case (n: String, _) => this.members.update(n, None) }

  override val name = s"${struct.name} instance"
  override def toString: String =
    s"${struct.name}{ ${members.map{ case (n, v) => s"$n = $v"}.mkString(", ")} }"
}
