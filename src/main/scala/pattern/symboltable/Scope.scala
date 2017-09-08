package pattern.symboltable

import pattern.symboltable.symbol.Symbol

trait Scope {
  def scopeName: String
  def enclosingScope: Option[Scope]
  def define(symbol: Symbol): Scope
  def resolve(name: String): Option[Symbol]
}
