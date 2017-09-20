package pattern.typing.pattern21_promotetype.symbol

import pattern.typing.pattern21_promotetype.`type`.{NullType, Type}
import pattern.typing.pattern21_promotetype.ast.CymbolAST
import pattern.typing.pattern21_promotetype.scope.Scope

/**
  * 記号
  *
  * "記号とは、変数やメソッドなどのプログラム実体につける名前にすぎません"
  *
  */
sealed trait Symbol {
  def name: String
  def typ: Type
  def node: Option[CymbolAST]
  override def toString: String = s"Symbol('$name'): ${typ.name}"
}

case object NullSymbol extends Symbol {
  override val name = "null"
  override val typ: Type = NullType
  override def node: Option[CymbolAST] = None
}

case class BuiltinSymbol(name: String, typ: Type, node: Option[CymbolAST]) extends Symbol {
  override def toString: String = s"Builtin${super.toString}"
}

case class VariableSymbol(name: String, typ: Type, node: Option[CymbolAST]) extends Symbol {
  override def toString: String = s"Variable${super.toString}"
}

case class MethodSymbol(
  name: String,
  typ: Type,
  enclosingScope: Option[Scope],
  arguments: Symbols,
  blockSymbols: Symbols,
  node: Option[CymbolAST],
) extends Symbol {
  override def toString: String = s"Method${super.toString}"
}

object MethodSymbol {
  def apply(
     name: String,
     typ: Type,
     enclosingScope: Option[Scope],
     node: Option[CymbolAST],
   ): MethodSymbol = new MethodSymbol(name, typ, enclosingScope, VectorSymbols(), MapSymbols(), node)
}

case class StructSymbol(
  name: String,
  typ: Type,
  enclosingScope: Option[Scope],
  members: Symbols,
  node: Option[CymbolAST],
) extends Symbol {
  def resolveMember(name: String): Option[Symbol] = members.resolve(name)
  override def toString: String = s"Struct${super.toString}"
}

object StructSymbol {
  def apply(
    name: String,
    typ: Type,
    enclosingScope: Option[Scope],
    node: Option[CymbolAST],
  ): StructSymbol = new StructSymbol(name, typ, enclosingScope, MapSymbols(), node)
}
