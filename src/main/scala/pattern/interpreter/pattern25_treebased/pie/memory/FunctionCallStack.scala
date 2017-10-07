package pattern.interpreter.pattern25_treebased.pie.memory

import scala.collection.mutable.ListBuffer

case class FunctionCallStack() {

  val _stack: ListBuffer[FunctionMemorySpace] = ListBuffer.empty

  def value: Seq[FunctionMemorySpace] = _stack.toList

  def size: Int = _stack.size
  def peek: Option[AnyRef] = _stack.headOption
  def push(space: FunctionMemorySpace): Unit = _stack.prepend(space)
  def pop(): Unit = _stack.remove(0)
  def nonEmpty: Boolean = _stack.nonEmpty
}
