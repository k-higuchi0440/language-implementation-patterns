package pattern.typing.pattern23_polymophictypesafe.scope

import pattern.typing.pattern23_polymophictypesafe.symbol.{MapSymbols, Symbols}

case class ClassScope(
  symbolName: Option[String],
  override protected var _symbols: Symbols,
  enclosingScope: Option[Scope],
) extends BaseScope {
  override def scopeName = "Class"
}

object ClassScope {
  private def apply(
   symbolName: Option[String],
   _symbols: Symbols,
   enclosingScope: Option[Scope],
  ): ClassScope = new ClassScope(symbolName, _symbols, enclosingScope)

  def apply(
    symbolName: String,
    enclosingScope: Option[Scope]
  ): ClassScope = new ClassScope(Some(symbolName), MapSymbols(), enclosingScope)
}
