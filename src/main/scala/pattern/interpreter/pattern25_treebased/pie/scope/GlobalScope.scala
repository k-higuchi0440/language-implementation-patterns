package pattern.interpreter.pattern25_treebased.pie.scope

import pattern.interpreter.pattern25_treebased.pie.symbol.{MapSymbols, Symbols}

case class GlobalScope() extends BaseScope {

  override protected var _symbols: Symbols = MapSymbols()

  override val scopeName: String = "Global"
  override val symbolName: Option[String] = None

  override def enclosingScope: Option[Scope] = None
}
