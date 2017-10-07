package pattern.interpreter.pattern25_treebased.pie.scope

import pattern.interpreter.pattern25_treebased.pie.symbol.{MapSymbols, Symbols}

case class StructScope(
  symbolName: Option[String],
  enclosingScope: Option[Scope],
  override protected var _symbols: Symbols,
) extends BaseScope {
  override def scopeName = "Struct"
}

object StructScope {
  private def apply(
   symbolName: Option[String],
   _symbols: Symbols,
   enclosingScope: Option[Scope],
  ): StructScope = new StructScope(symbolName, enclosingScope, _symbols)

  def apply(
    symbolName: String,
    enclosingScope: Option[Scope]
  ): StructScope = new StructScope(Some(symbolName), enclosingScope, MapSymbols())
}
