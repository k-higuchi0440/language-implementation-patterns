package pattern.typing.pattern23_polymophictypesafe.scope

import pattern.typing.pattern23_polymophictypesafe.symbol.{Symbol, Symbols}

trait Scope {
  def scopeName: String
  def symbolName: Option[String]
  def symbols: Symbols
  def enclosingScope: Option[Scope]
  def define(symbol: Symbol): Unit
  def resolve(name: String): Option[Symbol]
}
