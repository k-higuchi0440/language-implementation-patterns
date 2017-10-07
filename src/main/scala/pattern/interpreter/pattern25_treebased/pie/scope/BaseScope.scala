package pattern.interpreter.pattern25_treebased.pie.scope

import pattern.interpreter.pattern25_treebased.pie.symbol.{Symbol, Symbols}

trait BaseScope extends Scope {

  protected var _symbols: Symbols

  override def symbols: Symbols = _symbols

  override def define(symbol: Symbol): Unit = {
    this._symbols = _symbols.define(symbol)
  }

  override def resolve(name: String): Option[Symbol] = _symbols.resolve(name) match {
    case symbol: Some[_] => symbol
    case None => enclosingScope.flatMap(_.resolve(name))
  }

}
