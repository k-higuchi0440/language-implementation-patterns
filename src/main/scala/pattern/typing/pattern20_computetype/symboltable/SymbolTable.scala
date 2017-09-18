package pattern.typing.pattern20_computetype.symboltable

import pattern.typing.pattern20_computetype.`type`.Type
import pattern.typing.pattern20_computetype.scope.GlobalScope
import pattern.typing.pattern20_computetype.symbol.BuiltinSymbol
import pattern.typing.pattern20_computetype.symbol.Symbol

import scala.collection.SortedMap

/**
  * 単一スコープ(グローバルスコープ)を表現する記号表
  *
  */
case class SymbolTable private (symbols: Symbol.Symbols) extends GlobalScope {
  override def define(symbol: Symbol): SymbolTable = copy(symbols = symbols.updated(symbol.name, symbol))
  override def toString: String =
    s"SymbolTable(${symbols.mkString("\n  ", "\n  ", "\n")})"
}

object SymbolTable {
  private def apply(symbols: Symbol.Symbols): SymbolTable = new SymbolTable(symbols)
  def apply(): SymbolTable = {
    val builtinSymbols = SortedMap(
      "char"    -> BuiltinSymbol("char", Type.tChar, None),
      "int"     -> BuiltinSymbol("int", Type.tInt, None),
      "float"   -> BuiltinSymbol("float", Type.tFloat, None),
      "boolean" -> BuiltinSymbol("boolean", Type.tBoolean, None),
      "void"    -> BuiltinSymbol("void", Type.tVoid, None),
    )
    new SymbolTable(builtinSymbols)
  }
}
