package pattern.interpreter.pattern25_treebased.pie.memory

case class FunctionMemorySpace(funcName: String) extends MemorySpace {
  override val name = s"Function $funcName"
}
