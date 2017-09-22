package pattern.typing.pattern23_polymophictypesafe.scope

import pattern.typing.pattern23_polymophictypesafe.symbol.{Symbol, Symbols}

trait BaseScope extends Scope {

  protected var _symbols: Symbols

  override def define(symbol: Symbol): Unit = _symbols = _symbols.define(symbol)

  override def resolve(name: String): Option[Symbol] = _symbols.resolve(name) match {
    case symbol: Some[_] => symbol
    case None => enclosingScope.flatMap(_.resolve(name))
  }

  override def symbols: Symbols = _symbols

}
