package pattern.typing.pattern21_promotetype.`type`

import pattern.typing.pattern21_promotetype.ast.CymbolAST
import pattern.typing.pattern21_promotetype.scope.GlobalScope
import pattern.typing.pattern21_promotetype.symbol.{Symbol, MethodSymbol, NullSymbol, StructSymbol}
import pattern.typing.pattern21_promotetype.symboltable.SymbolTable

import scala.annotation.tailrec
import scala.collection.mutable

sealed trait Type {
  def name: String
  def enclosingType: Option[Type]

  def arithmeticResultType(other: Type): Type = InvalidType
  def relationalResultType(other: Type): Type = InvalidType
  def equalityResultType(other: Type): Type = InvalidType
  def promote(other: Type): Option[Type] = None

  override def toString: String = name
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

object InvalidType extends Type {
  override val name = "Invalid"
  override def toString: String = "InvalidType"
  override def enclosingType: Option[Type] = None
}

object Type {

  private case class BooleanType(name: String, enclosingType: Option[Type]) extends Type {
    override def equalityResultType(other: Type): Type = other match {
      case BooleanType(_, _) => tBoolean
      case _                 => InvalidType
    }
  }

  private case class CharType(name: String, enclosingType: Option[Type]) extends Type {
    override def arithmeticResultType(other: Type): Type = other match {
      case _: CharType  => tChar
      case _: IntType   => tInt
      case _: FloatType => tFloat
      case _            => InvalidType
    }

    override def relationalResultType(other: Type): Type = other match {
      case _: CharType  => tBoolean
      case _: IntType   => tBoolean
      case _: FloatType => tBoolean
      case _            => InvalidType
    }

    override def equalityResultType(other: Type): Type = other match {
      case _: CharType  => tBoolean
      case _: IntType   => tBoolean
      case _: FloatType => tBoolean
      case _            => InvalidType
    }

    override def promote(other: Type): Option[Type] = other match {
      case _: IntType   => Some(tInt)
      case _: FloatType => Some(tFloat)
      case _            => None
    }
  }

  private case class IntType(name: String, enclosingType: Option[Type]) extends Type {
    override def arithmeticResultType(other: Type): Type = other match {
      case _: CharType  => tInt
      case _: IntType   => tInt
      case _: FloatType => tFloat
      case _            => InvalidType
    }

    override def relationalResultType(other: Type): Type = other match {
      case _: CharType  => tBoolean
      case _: IntType   => tBoolean
      case _: FloatType => tBoolean
      case _            => InvalidType
    }

    override def equalityResultType(other: Type): Type = other match {
      case _: CharType  => tBoolean
      case _: IntType   => tBoolean
      case _: FloatType => tBoolean
      case _            => InvalidType
    }

    override def promote(other: Type): Option[Type] = other match {
      case _: FloatType => Some(tFloat)
      case _            => None
    }
  }

  private case class FloatType(name: String, enclosingType: Option[Type]) extends Type {
    override def arithmeticResultType(other: Type): Type = other match {
      case _: CharType  => tFloat
      case _: IntType   => tFloat
      case _: FloatType => tFloat
      case _            => InvalidType
    }

    override def relationalResultType(other: Type): Type = other match {
      case _: CharType  => tBoolean
      case _: IntType   => tBoolean
      case _: FloatType => tBoolean
      case _            => InvalidType
    }

    override def equalityResultType(other: Type): Type = other match {
      case _: CharType  => tBoolean
      case _: IntType   => tBoolean
      case _: FloatType => tBoolean
      case _            => InvalidType
    }
  }

  private case class VoidType(name: String, enclosingType: Option[Type]) extends Type

  private case class UserDefinedType(name: String, enclosingType: Option[Type]) extends Type {
    override def promote(other: Type): Option[Type] = enclosingType
  }

  lazy val tBoolean: Type = BooleanType("Boolean", None)
  lazy val tChar: Type    = CharType("Char", None)
  lazy val tInt: Type     = IntType("Int", None)
  lazy val tFloat: Type   = FloatType("Float", None)
  lazy val tVoid: Type    = VoidType("Void", None)

  def defineType(name: String, enclosingType: Option[Type] = None): Type = UserDefinedType(name, enclosingType)

  def binaryOp(a: CymbolAST, b: CymbolAST): Type = {
    val aType = a.evalType.getOrElse(NullType)
    val bType = b.evalType.getOrElse(NullType)
    a.promotionType = aType.promote(bType)
    b.promotionType = bType.promote(aType)
    aType.arithmeticResultType(bType)
  }

  def relationalOp(a: CymbolAST, b: CymbolAST): Type = {
    val aType = a.evalType.getOrElse(NullType)
    val bType = b.evalType.getOrElse(NullType)
    a.promotionType = aType.promote(bType)
    b.promotionType = bType.promote(aType)
    aType.relationalResultType(bType)
  }

  def equalityOp(a: CymbolAST, b: CymbolAST): Type = {
    val aType = a.evalType.getOrElse(NullType)
    val bType = b.evalType.getOrElse(NullType)
    a.promotionType = aType.promote(bType)
    b.promotionType = bType.promote(aType)
    aType.equalityResultType(bType)
  }

  def unaryMinus(a: CymbolAST): Type = a.evalType.getOrElse(NullType)

  def unaryNot(a: CymbolAST): Type = tBoolean

  def arrayIndex(id: CymbolAST, index: CymbolAST): Type = {
    val symbol = id.scope.flatMap(_.resolve(id.getText))
    id.symbol = symbol
    symbol.map(_.typ).getOrElse(NullType) match {
      case ArrayType(elemType) =>
        index.promotionType = index.evalType.flatMap(_.promote(tInt)) // indexの式がIntに昇格する必要があるか確認
        elemType
      case _ => NullType
    }
  }

  def call(id: CymbolAST, args: mutable.Buffer[AnyRef]): Type = {
    val symbol = id.scope.flatMap(_.resolve(id.getText))
    id.symbol = symbol
    symbol.getOrElse(NullSymbol) match {
      case method: MethodSymbol =>
        val argPairs = method.arguments.value.toSeq.zip(args).map {
          case ((_, sym), node) => (sym.asInstanceOf[Symbol], node.asInstanceOf[CymbolAST])
        }
        argPairs.foreach { case (sym, node) =>
          val paramType = sym.typ
          val argType   = node.evalType.getOrElse(NullType)
          node.promotionType = argType.promote(paramType)
        }
        method.typ
      case _ => NullType
    }
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

  def promoteDeclExprType(id: CymbolAST, expr: CymbolAST): Unit = {
    val idType = id.symbol.map(_.typ).getOrElse(NullType)
    expr.promotionType = expr.evalType.flatMap(_.promote(idType))
  }

  def promoteReturnExprType(method: MethodSymbol, expr: CymbolAST): Unit = {
    val returnType = method.typ
    expr.promotionType = expr.evalType.flatMap(_.promote(returnType))
  }

  def promoteAssignExprType(lhs: CymbolAST, rhs: CymbolAST): Unit = {
    val decl = lhs.evalType.getOrElse(NullType)
    rhs.promotionType = rhs.evalType.flatMap(_.promote(decl))
  }

}