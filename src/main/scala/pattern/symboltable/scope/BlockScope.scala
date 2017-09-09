package pattern.symboltable.scope

import pattern.symboltable.symbol.Symbol

import scala.collection.SortedMap

/**
  * ブロックスコープ（ローカルスコープ）を表現する記号表
  *
  * スコープを作成するときは、外包しているスコープを与えて作成し、取り外すまで使い続ける
  *
  */
case class BlockScope(
  enclosingScope: Option[Scope],
  symbols: SortedMap[String, Symbol]) extends Scope {
  override val scopeName = "block"

  override def define(symbol: Symbol): BlockScope =
    copy(symbols = symbols.updated(symbol.name, symbol))

  override def resolve(name: String): Option[Symbol] = symbols.get(name) match {
    case some@Some(_) => some
    case None => enclosingScope.flatMap(_.resolve(name)) // このスコープになければ、外包スコープを参照
  }

  override def toString: String = s"BlockScope(${symbols.mkString(", ")})"
}

object BlockScope {
  private def apply(enclosingScope: Option[Scope], symbols: SortedMap[String, Symbol]): BlockScope =
    new BlockScope(enclosingScope, symbols)

  def apply(enclosingScope: Scope): BlockScope = new BlockScope(Some(enclosingScope), SortedMap.empty)
}
