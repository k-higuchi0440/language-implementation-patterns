package pattern.typing.pattern23_polymophictypesafe.symboltable

import pattern.typing.pattern23_polymophictypesafe.`type`.Type
import pattern.typing.pattern23_polymophictypesafe.scope.GlobalScope
import pattern.typing.pattern23_polymophictypesafe.symbol.{BuiltinSymbol, MapSymbols, Symbols}

import scala.collection.SortedMap

/**
  * 単一スコープ(グローバルスコープ)を表現する記号表
  *
  */
case class SymbolTable private (override protected var _symbols: Symbols) extends GlobalScope {
  override def toString: String =
    s"SymbolTable(${symbols.value.mkString("\n  ", "\n  ", "\n")})"
}

object SymbolTable {
  private def apply(_symbols: Symbols): SymbolTable = new SymbolTable(_symbols)
  def apply(): SymbolTable = {
    val builtinSymbols = MapSymbols(SortedMap(
      "char"    -> BuiltinSymbol("char", Type.tChar, None),
      "int"     -> BuiltinSymbol("int", Type.tInt, None),
      "float"   -> BuiltinSymbol("float", Type.tFloat, None),
      "boolean" -> BuiltinSymbol("boolean", Type.tBoolean, None),
      "void"    -> BuiltinSymbol("void", Type.tVoid, None),
    ))
    new SymbolTable(builtinSymbols)
  }
}
