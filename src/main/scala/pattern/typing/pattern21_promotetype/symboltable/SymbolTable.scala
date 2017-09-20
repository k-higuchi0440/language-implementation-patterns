package pattern.typing.pattern21_promotetype.symboltable

import pattern.typing.pattern21_promotetype.`type`.Type
import pattern.typing.pattern21_promotetype.scope.GlobalScope
import pattern.typing.pattern21_promotetype.symbol.{BuiltinSymbol, MapSymbols, Symbol, Symbols}

import scala.collection.SortedMap

/**
  * 単一スコープ(グローバルスコープ)を表現する記号表
  *
  */
case class SymbolTable private (symbols: Symbols) extends GlobalScope {
  override def define(symbol: Symbol): SymbolTable = copy(symbols = symbols.define(symbol))
  override def toString: String =
    s"SymbolTable(${symbols.value.mkString("\n  ", "\n  ", "\n")})"
}

object SymbolTable {
  private def apply(symbols: Symbols): SymbolTable = new SymbolTable(symbols)
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
