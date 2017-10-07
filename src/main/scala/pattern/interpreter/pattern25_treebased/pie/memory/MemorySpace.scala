package pattern.interpreter.pattern25_treebased.pie.memory

import scala.collection.mutable

trait MemorySpace {
  val name: String
  val members: mutable.Map[String, Any] = mutable.Map.empty
  def read(name: String): Option[Any] = members.get(name)
  def write(name: String, value: Any): Unit = members.update(name, value)

  override def toString: String = s"[MemorySpace] $name {${members.mkString(", ")}}"
}
