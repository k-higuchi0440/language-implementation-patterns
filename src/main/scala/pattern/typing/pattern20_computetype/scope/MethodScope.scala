package pattern.typing.pattern20_computetype.scope

import pattern.typing.pattern20_computetype.symbol.Symbol

case class MethodScope(
  symbolName: Option[String],
  symbols: Symbol.Symbols,
  enclosingScope: Option[Scope]
) extends Scope {
  override def scopeName = "Method"
  override def define(symbol: Symbol): MethodScope = copy(symbols = symbols.updated(symbol.name, symbol))
}

object MethodScope {
  def apply(
    symbolName: String,
    enclosingScope: Option[Scope]
  ): MethodScope = new MethodScope(Some(symbolName), Symbol.emptySymbols, enclosingScope)
}