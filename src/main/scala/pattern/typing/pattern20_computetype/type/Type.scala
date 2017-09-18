package pattern.typing.pattern20_computetype.`type`

import pattern.typing.pattern20_computetype.ast.CymbolAST
import pattern.typing.pattern20_computetype.scope.GlobalScope
import pattern.typing.pattern20_computetype.symbol.{NullSymbol, StructSymbol}
import pattern.typing.pattern20_computetype.symboltable.SymbolTable

import scala.annotation.tailrec
import scala.collection.mutable

sealed trait Type {
  def name: String
  def enclosingType: Option[Type]
}

case class ArrayType(elementType: Type) extends Type {
  override val name = s"Array[${elementType.name}]"
  override def enclosingType: Option[Type] = None
}

object NullType extends Type {
  override val name = "null"
  override def toString: String = "NullType"
  override def enclosingType: Option[Type] = None
}

object Type {

  private case class TypeImpl(name: String, enclosingType: Option[Type]) extends Type {
    override def toString: String = name
  }

  lazy val tBoolean: Type = TypeImpl("Boolean", None)
  lazy val tChar: Type    = TypeImpl("Char", None)
  lazy val tInt: Type     = TypeImpl("Int", None)
  lazy val tFloat: Type   = TypeImpl("Float", None)
  lazy val tVoid: Type    = TypeImpl("Void", None)

  def defineType(name: String, enclosingType: Option[Type] = None): Type = TypeImpl(name, enclosingType)

  def binaryOp(a: CymbolAST, b: CymbolAST): Type = a.evalType.getOrElse(NullType)

  def relationalOp(a: CymbolAST, b: CymbolAST): Type = tBoolean

  def equalityOp(a: CymbolAST, b: CymbolAST): Type = tBoolean

  def unaryMinus(a: CymbolAST): Type = a.evalType.getOrElse(NullType)

  def unaryNot(a: CymbolAST): Type = tBoolean

  // 型計算だけなのでここのindexはダミー
  def arrayIndex(id: CymbolAST, index: CymbolAST): Type = {
    val symbol = id.scope.flatMap(_.resolve(id.getText))
    id.symbol = symbol
    symbol.getOrElse(NullType) match {
      case ArrayType(elemType) => elemType
      case _ => NullType
    }
  }

  // 型計算だけなのでここのargsはダミー
  def call(id: CymbolAST, args: mutable.Buffer[AnyRef]): Type = {
    val symbol = id.scope.flatMap(_.resolve(id.getText))
    id.symbol = symbol
    symbol.map(_.typ).getOrElse(NullType)
  }

  def member(expr: CymbolAST, field: CymbolAST, symTab: SymbolTable): Type = {
    val evalType = expr.evalType.getOrElse(NullType)
    val exprStruct = field.scope.flatMap(_.resolve(evalType.name))
    val symbol = exprStruct match {
      case Some(sym) => sym
      case None =>
        val enclosingTypes = resolveEnclosingTypes(evalType)
        resolveEnclosedStructSymbol(evalType.name, enclosingTypes, symTab).getOrElse(NullSymbol)
    }
    val member = symbol match {
      case struct: StructSymbol => struct.resolveMember(field.getText)
      case _ => None
    }
    field.symbol = member
    member.map(_.typ).getOrElse(NullType)
  }

  // ネストした構造体を探すためのヘルパー、構造体を包含している型をすべて取得する
  private def resolveEnclosingTypes(structType: Type): Seq[Type] = {
    @tailrec
    def loop(sType: Type, types: Seq[Type]): Seq[Type] = {
        sType.enclosingType match {
          case None => types
          case Some(enclosing) => loop(enclosing, enclosing +: types)
      }
    }
    loop(structType, Seq.empty)
  }

  // ネストした構造体を探すためのヘルパー
  // トップからボトムへ、構造体のメンバーの中からターゲットを探し出す
  private def resolveEnclosedStructSymbol(
    targetName: String,
    enclosings: Seq[Type],
    globalScope: GlobalScope,
  ): Option[StructSymbol] = {
    @tailrec
    def loop(struct: StructSymbol, types: List[Type]): Option[StructSymbol] = {
      struct.resolveMember(targetName) match {
        case Some(member) => Some(member.asInstanceOf[StructSymbol])
        case None =>
          if (types.isEmpty) None
          else {
            struct.resolveMember(types.head.name).getOrElse(NullSymbol) match {
              case child: StructSymbol => loop(child, types.tail)
              case _ => None
            }
          }
      }
    }
    enclosings match {
      case Nil => None
      case head :: tail =>
        globalScope.resolve(head.name) match {
          case None => None
          case Some(sym) => loop(sym.asInstanceOf[StructSymbol], tail)
        }
    }
  }
}
