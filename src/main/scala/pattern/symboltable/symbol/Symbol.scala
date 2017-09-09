package pattern.symboltable.symbol

import pattern.symboltable.scope.Scope

import scala.collection.SortedMap

/**
  * 記号
  *
  * "記号とは、変数やメソッドなどのプログラム実体につける名前にすぎません"
  *
  */
sealed trait Symbol {
  def name: String
  def typ: Type
  override def toString: String = s"Symbol('$name': ${typ.name})"
}

case class VariableSymbol(name: String, typ: Type) extends Symbol

case class BuiltinTypeSymbol(name: String) extends Symbol with Type { // 型としても機能する
  override val typ: Type = this
}

case object NullTypeSymbol extends Symbol with Type { // 型としても機能する
  override val name = "null"
  override val typ: Type = this
}

case class MethodSymbol(
  name: String,
  typ: Type,
  enclosingScope: Option[Scope],
  arguments: SortedMap[String, Symbol]) extends Symbol with Scope { // スコープとしても機能する
  override val scopeName: String = name

  override def define(symbol: Symbol): MethodSymbol =
    copy(arguments = arguments.updated(symbol.name, symbol))

  override def resolve(name: String): Option[Symbol] = arguments.get(name) match {
    case some @ Some(_) => some
    case None           => enclosingScope.flatMap(_.resolve(name))
  }

  override def toString: String = toStringAsSymbol
  def toStringAsSymbol = s"Method${super.toString}"
  def toStringAsScope: String = s"MethodScope(${arguments.mkString(", ")})"
}

object MethodSymbol {
  private def apply(
    name: String,
    typ: Type,
    enclosingScope: Option[Scope],
    arguments: SortedMap[String, Symbol],
  ): MethodSymbol = new MethodSymbol(name, typ, enclosingScope, arguments)

  def apply(
    name: String,
    typ: Type,
    enclosingScope: Option[Scope],
  ): MethodSymbol = new MethodSymbol(name, typ, enclosingScope, SortedMap.empty)
}
