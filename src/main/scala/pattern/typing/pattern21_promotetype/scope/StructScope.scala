package pattern.typing.pattern21_promotetype.scope

import pattern.typing.pattern21_promotetype.symbol.{MapSymbols, Symbol, Symbols}

case class StructScope(
  symbolName: Option[String],
  symbols: Symbols,
  enclosingScope: Option[Scope]
) extends Scope {
  override def scopeName = "Struct"
  override def define(symbol: Symbol): StructScope = copy(symbols = symbols.define(symbol))
}

object StructScope {
  def apply(
    symbolName: String,
    enclosingScope: Option[Scope]
  ): StructScope = new StructScope(Some(symbolName), MapSymbols(), enclosingScope)
}
