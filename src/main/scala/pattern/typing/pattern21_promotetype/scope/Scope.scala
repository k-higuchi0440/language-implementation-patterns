package pattern.typing.pattern21_promotetype.scope

import pattern.typing.pattern21_promotetype.symbol.{Symbol, Symbols}

trait Scope {
  def scopeName: String
  def symbolName: Option[String]
  def symbols: Symbols
  def enclosingScope: Option[Scope]
  def define(symbol: Symbol): Scope

  def resolve(name: String): Option[Symbol] = symbols.resolve(name) match {
    case symbol: Some[_] => symbol
    case None => enclosingScope.flatMap(_.resolve(name))
  }
}
