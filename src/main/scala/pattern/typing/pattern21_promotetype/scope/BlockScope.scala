package pattern.typing.pattern21_promotetype.scope

import pattern.typing.pattern21_promotetype.symbol.{MapSymbols, Symbol, Symbols}

case class BlockScope(
  symbols: Symbols,
  enclosingScope: Option[Scope]
) extends Scope {
  override val symbolName: Option[String] = None
  override val scopeName: String = "Block"
  override def define(symbol: Symbol): Scope = copy(symbols = symbols.define(symbol))
}

object BlockScope {
  def apply(enclosingScope: Option[Scope]): BlockScope = new BlockScope(MapSymbols(), enclosingScope)
}
