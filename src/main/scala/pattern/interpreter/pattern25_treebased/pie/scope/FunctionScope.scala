package pattern.interpreter.pattern25_treebased.pie.scope

import pattern.interpreter.pattern25_treebased.pie.symbol.{Symbols, VectorSymbols}

case class FunctionScope(
  symbolName: Option[String],
  enclosingScope: Option[Scope],
  override protected var _symbols: Symbols,
) extends BaseScope {
  override def scopeName = "Function"
}

object FunctionScope {
  private def apply(
    symbolName: Option[String],
    _symbols: Symbols,
    enclosingScope: Option[Scope],
  ): FunctionScope = new FunctionScope(symbolName, enclosingScope, _symbols)

  def apply(
    symbolName: String,
    enclosingScope: Option[Scope]
  ): FunctionScope = new FunctionScope(Some(symbolName), enclosingScope, VectorSymbols())
}
