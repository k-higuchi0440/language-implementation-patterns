package pattern.typing.pattern20_computetype.scope
import pattern.typing.pattern20_computetype.symbol.Symbol

case class StructScope(
  symbolName: Option[String],
  symbols: Symbol.Symbols,
  enclosingScope: Option[Scope]
) extends Scope {
  override def scopeName = "Struct"
  override def define(symbol: Symbol): StructScope = copy(symbols = symbols.updated(symbol.name, symbol))
}

object StructScope {
  def apply(
    symbolName: String,
    enclosingScope: Option[Scope]
  ): StructScope = new StructScope(Some(symbolName), Symbol.emptySymbols, enclosingScope)
}
