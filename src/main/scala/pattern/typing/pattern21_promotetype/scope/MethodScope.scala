package pattern.typing.pattern21_promotetype.scope

import pattern.typing.pattern21_promotetype.symbol.{Symbol, Symbols, VectorSymbols}

case class MethodScope(
  symbolName: Option[String],
  symbols: Symbols,
  enclosingScope: Option[Scope]
) extends Scope {
  override def scopeName = "Method"
  override def define(symbol: Symbol): MethodScope = copy(symbols = symbols.define(symbol))
}

object MethodScope {
  def apply(
    symbolName: String,
    enclosingScope: Option[Scope]
  ): MethodScope = new MethodScope(Some(symbolName), VectorSymbols(), enclosingScope)
}