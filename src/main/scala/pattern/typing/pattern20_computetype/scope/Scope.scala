package pattern.typing.pattern20_computetype.scope

import pattern.typing.pattern20_computetype.symbol.Symbol

trait Scope {
  def scopeName: String
  def symbolName: Option[String]
  def symbols: Symbol.Symbols
  def enclosingScope: Option[Scope]
  def define(symbol: Symbol): Scope

  def resolve(name: String): Option[Symbol] = symbols.get(name) match {
    case some @ Some(_) => some
    case None => enclosingScope.flatMap(_.resolve(name))
  }
}
