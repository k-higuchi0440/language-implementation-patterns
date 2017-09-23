package pattern.typing.pattern21_promotetype.scope

import pattern.typing.pattern21_promotetype.symbol.{Symbols, VectorSymbols}

case class MethodScope(
  symbolName: Option[String],
  override protected var _symbols: Symbols,
  enclosingScope: Option[Scope],
) extends BaseScope {
  override def scopeName = "Method"
}

object MethodScope {
  private def apply(
    symbolName: Option[String],
    _symbols: Symbols,
    enclosingScope: Option[Scope],
  ): MethodScope = new MethodScope(symbolName, _symbols, enclosingScope)

  def apply(
    symbolName: String,
    enclosingScope: Option[Scope]
  ): MethodScope = new MethodScope(Some(symbolName), VectorSymbols(), enclosingScope)
}