package pattern.interpreter.pattern25_treebased.pie.scope

import pattern.interpreter.pattern25_treebased.pie.symbol.{MapSymbols, Symbols}

case class BlockScope(
  enclosingScope: Option[Scope],
  override protected var _symbols: Symbols,
) extends BaseScope {
  override val symbolName: Option[String] = None
  override val scopeName: String = "Block"
 }

object BlockScope {
  private def apply(symbols: Symbols, enclosingScope: Option[Scope]): BlockScope =
    new BlockScope(enclosingScope, symbols)
  def apply(enclosingScope: Option[Scope]): BlockScope = new BlockScope(enclosingScope, MapSymbols())
}
