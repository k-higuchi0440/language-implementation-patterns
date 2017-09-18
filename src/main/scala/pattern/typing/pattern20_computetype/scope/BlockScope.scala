package pattern.typing.pattern20_computetype.scope

import pattern.typing.pattern20_computetype.symbol.Symbol

case class BlockScope(
  symbols: Symbol.Symbols,
  enclosingScope: Option[Scope]
) extends Scope {
  override val symbolName: Option[String] = None
  override val scopeName: String = "Block"
  override def define(symbol: Symbol): Scope = copy(symbols = symbols.updated(symbol.name, symbol))
}

object BlockScope {
  def apply(enclosingScope: Option[Scope]): BlockScope = new BlockScope(Symbol.emptySymbols, enclosingScope)
}
