package pattern.typing.pattern23_polymophictypesafe.symbol

import pattern.typing.pattern23_polymophictypesafe.`type`.{NullType, Type}
import pattern.typing.pattern23_polymophictypesafe.ast.CymbolAST

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
     node: Option[CymbolAST],
   ): MethodSymbol = new MethodSymbol(name, typ, VectorSymbols(), MapSymbols(), node)
}

case class ClassSymbol(
  name: String,
  typ: Type,
  superClass: Option[ClassSymbol],
  members: Symbols,
  node: Option[CymbolAST],
) extends Symbol {
  def resolveMember(name: String): Option[Symbol] = members.resolve(name) match {
    case symbol: Some[Symbol] => symbol
    case None => superClass.flatMap(_.resolveMember(name))
  }
  override def toString: String = s"Struct${super.toString}"
}

object ClassSymbol {
  def apply(
    name: String,
    typ: Type,
    superClass: Option[ClassSymbol],
    node: Option[CymbolAST],
  ): ClassSymbol = new ClassSymbol(name, typ, superClass, MapSymbols(), node)
}
