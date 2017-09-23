package pattern.typing.pattern21_promotetype.scope

import pattern.typing.pattern21_promotetype.symbol.{MapSymbols, Symbols}

case class StructScope(
  symbolName: Option[String],
  override protected var _symbols: Symbols,
  enclosingScope: Option[Scope],
) extends BaseScope {
  override def scopeName = "Struct"
}

object StructScope {
  private def apply(
   symbolName: Option[String],
   _symbols: Symbols,
   enclosingScope: Option[Scope],
  ): StructScope = new StructScope(symbolName, _symbols, enclosingScope)

  def apply(
    symbolName: String,
    enclosingScope: Option[Scope]
  ): StructScope = new StructScope(Some(symbolName), MapSymbols(), enclosingScope)
}
