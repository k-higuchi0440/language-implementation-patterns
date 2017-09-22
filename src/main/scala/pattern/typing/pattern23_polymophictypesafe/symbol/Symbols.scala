package pattern.typing.pattern23_polymophictypesafe.symbol

import scala.collection.SortedMap

trait Symbols {
  def value: Traversable[_]
  def resolve(name: String): Option[Symbol]
  def define(symbol: Symbol): Symbols
}

case class MapSymbols(value: SortedMap[String, Symbol] = SortedMap.empty) extends Symbols {
  override def resolve(name: String): Option[Symbol] = value.get(name)
  override def define(symbol: Symbol): Symbols = copy(value.updated(symbol.name, symbol))
}

case class VectorSymbols(value: Vector[(String, Symbol)] = Vector.empty) extends Symbols {
  override def resolve(name: String): Option[Symbol] = value.find(_._1 == name).map(_._2)
  override def define(symbol: Symbol): Symbols = copy(value :+ (symbol.name, symbol))
}
