package pattern.symboltable

import pattern.symboltable.scope.Scope
import pattern.symboltable.symbol.{BuiltinTypeSymbol, Symbol}

import scala.collection.SortedMap

/**
  * 単一スコープ(グローバルスコープ)を表現する記号表
  *
  */
case class SymbolTable(symbols: SortedMap[String, Symbol]) extends Scope {
  override val scopeName = "global"
  override val enclosingScope: Option[Scope] = None

  override def define(symbol: Symbol): SymbolTable =
    copy(symbols = symbols.updated(symbol.name, symbol))

  override def resolve(name: String): Option[Symbol] = symbols.get(name)

  override def toString: String =
    s"SymbolTable(${symbols.mkString("\n  ", "\n  ", "\n")}) [scope: $scopeName]"
}

object SymbolTable {
  private def apply(symbols: SortedMap[String, Symbol]): SymbolTable = new SymbolTable(symbols)
  def apply(): SymbolTable = {
    val builtinSymbols = SortedMap(
      "int"   -> BuiltinTypeSymbol("int"),
      "float" -> BuiltinTypeSymbol("float"),
    )
    new SymbolTable(builtinSymbols)
  }
}
