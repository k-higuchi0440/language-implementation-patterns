package pattern.interpreter.pattern25_treebased.pie.scope

import pattern.interpreter.pattern25_treebased.pie.symbol.{Symbol, Symbols}

trait Scope {
  def scopeName: String
  def symbolName: Option[String]
  def symbols: Symbols
  def enclosingScope: Option[Scope]
  def define(symbol: Symbol): Unit
  def resolve(name: String): Option[Symbol]
}
