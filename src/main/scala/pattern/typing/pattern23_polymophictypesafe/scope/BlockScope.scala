package pattern.typing.pattern23_polymophictypesafe.scope

import pattern.typing.pattern23_polymophictypesafe.symbol.{MapSymbols, Symbols}

case class BlockScope(
  override protected var _symbols: Symbols,
  enclosingScope: Option[Scope]
) extends BaseScope {
  override val symbolName: Option[String] = None
  override val scopeName: String = "Block"
 }

object BlockScope {
  private def apply(_symbols: Symbols, enclosingScope: Option[Scope]): BlockScope =
    new BlockScope(_symbols, enclosingScope)
  def apply(enclosingScope: Option[Scope]): BlockScope = new BlockScope(MapSymbols(), enclosingScope)
}
