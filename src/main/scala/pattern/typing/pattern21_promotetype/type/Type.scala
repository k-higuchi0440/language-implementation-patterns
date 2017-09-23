package pattern.typing.pattern21_promotetype.`type`

import pattern.typing.pattern21_promotetype.ast.CymbolAST
import pattern.typing.pattern21_promotetype.symbol.{MethodSymbol, NullSymbol, StructSymbol, Symbol, VariableSymbol}
import pattern.typing.pattern21_promotetype.symboltable.SymbolTable
import pattern.typing.pattern22_typesafe.listener.CymbolListener

import scala.collection.mutable

sealed trait Type {
  def name: String

  def arithmeticResultType(other: Type): Type = InvalidType
  def relationalResultType(other: Type): Type = InvalidType
  def equalityResultType(other: Type): Type = InvalidType
  def promote(other: Type): Option[Type] = None

  override def toString: String = name
}

case class ArrayType(elementType: Type) extends Type {
  override val name = s"Array[${elementType.name}]"
}

object NullType extends Type {
  override val name = "null"
  override def toString: String = "NullType"
}

object InvalidType extends Type {
  override val name = "Invalid"
  override def toString: String = "InvalidType"
}

object Type {

  private case class BooleanType(name: String) extends Type {
    override def equalityResultType(other: Type): Type = other match {
      case BooleanType(_) => tBoolean
      case _                 => InvalidType
    }
  }

  private case class CharType(name: String) extends Type {
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

  private case class IntType(name: String) extends Type {
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

  private case class FloatType(name: String) extends Type {
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

  private case class VoidType(name: String) extends Type

  private case class StructType(name: String) extends Type

  lazy val tBoolean: Type = BooleanType("Boolean")
  lazy val tChar: Type    = CharType("Char")
  lazy val tInt: Type     = IntType("Int")
  lazy val tFloat: Type   = FloatType("Float")
  lazy val tVoid: Type    = VoidType("Void")

  def defineType(name: String): Type = StructType(name)

  def binaryOp(a: CymbolAST, b: CymbolAST, listener: CymbolListener): Type = {
    val aType = a.evalType.getOrElse(NullType)
    val bType = b.evalType.getOrElse(NullType)
    val result = aType.arithmeticResultType(bType)
    if(result == InvalidType) {
      listener.error(s"line ${a.getLine} ${listener.nodeToText(a)}, ${listener.nodeToText(b)} have incompatible types:" +
        s" ${listener.nodeToText(a.getParent.asInstanceOf[CymbolAST])}")
    } else {
      a.promotionType = aType.promote(bType)
      b.promotionType = bType.promote(aType)
    }
    result
  }

  def relationalOp(a: CymbolAST, b: CymbolAST, listener: CymbolListener): Type = {
    val aType = a.evalType.getOrElse(NullType)
    val bType = b.evalType.getOrElse(NullType)
    val result = aType.relationalResultType(bType)
    if(result == InvalidType) {
      listener.error(s"line ${a.getLine} ${listener.nodeToText(a)}, ${listener.nodeToText(b)} have incompatible types:" +
        s" ${listener.nodeToText(a.getParent.asInstanceOf[CymbolAST])}")
    } else {
      a.promotionType = aType.promote(bType)
      b.promotionType = bType.promote(aType)
    }
    tBoolean
  }

  def equalityOp(a: CymbolAST, b: CymbolAST, listener: CymbolListener): Type = {
    val aType = a.evalType.getOrElse(NullType)
    val bType = b.evalType.getOrElse(NullType)
    a.promotionType = aType.promote(bType)
    b.promotionType = bType.promote(aType)
    val result = aType.equalityResultType(bType)
    if(result == InvalidType) {
      listener.error(s"line ${a.getLine} ${listener.nodeToText(a)}, ${listener.nodeToText(b)} have incompatible types:" +
        s" ${listener.nodeToText(a.getParent.asInstanceOf[CymbolAST])}")
    } else {
      a.promotionType = aType.promote(bType)
      b.promotionType = bType.promote(aType)
    }
    tBoolean
  }

  def unaryMinus(a: CymbolAST, listener: CymbolListener): Type = {
    val aType = a.evalType.getOrElse(NullType)
    if(aType == tInt || aType == tFloat) aType
    else {
      listener.error(s"line ${a.getLine} ${listener.nodeToText(a)} must be int or float: ${listener.nodeToText(a.getParent.asInstanceOf[CymbolAST])}")
      InvalidType
    }
  }

  def unaryNot(a: CymbolAST, listener: CymbolListener): Type = {
    val aType = a.evalType.getOrElse(NullType)
    if(aType == tBoolean) tBoolean
    else {
      listener.error(s"line ${a.getLine} ${listener.nodeToText(a)} must be boolean: ${listener.nodeToText(a.getParent.asInstanceOf[CymbolAST])}")
      InvalidType
    }
  }

  def arrayIndex(id: CymbolAST, index: CymbolAST, listener: CymbolListener): Type = {
    val symbol = id.scope.flatMap(_.resolve(id.getText))
    id.symbol = symbol
    val sym = symbol.getOrElse(NullSymbol)
    if(sym.isInstanceOf[VariableSymbol] && sym.typ.isInstanceOf[ArrayType]) {
      symbol.map(_.typ).getOrElse(NullType) match {
        case ArrayType(elemType) =>
          index.promotionType = index.evalType.flatMap(_.promote(tInt)) // indexの式がIntに昇格する必要があるか確認
          if(!canAssignTo(index.evalType.getOrElse(NullType), tInt, index.promotionType.getOrElse(NullType)))
            listener.error(s"line ${index.getLine} ${listener.nodeToText(index)} index must be int: " +
              s"${listener.nodeToText(id.getParent.asInstanceOf[CymbolAST])}")
          elemType
        case _ => NullType
      }
    } else {
      listener.error(s"line ${id.getLine} ${listener.nodeToText(id)} must be an array variable: " +
        s"${listener.nodeToText(id.getParent.asInstanceOf[CymbolAST])}")
      InvalidType
    }
  }

  def call(id: CymbolAST, args: mutable.Buffer[AnyRef], listener: CymbolListener): Type = {
    val symbol = id.scope.flatMap(_.resolve(id.getText))
    id.symbol = symbol
    if(symbol.getOrElse(NullSymbol).isInstanceOf[MethodSymbol]) {
      symbol.getOrElse(NullSymbol) match {
        case method: MethodSymbol =>
          val argPairs = method.arguments.value.toSeq.zip(args).map { pair =>
            val ((_, sym), node) = pair
            (sym.asInstanceOf[Symbol], node.asInstanceOf[CymbolAST])
          }
          argPairs.foreach { case (sym, node) =>
            val paramType = sym.typ
            val argType   = node.evalType.getOrElse(NullType)
            node.promotionType = argType.promote(paramType)
            if(!canAssignTo(argType, paramType, node.promotionType.getOrElse(NullType))) {
              listener.error(
                s"line ${node.getLine} ${listener.nodeToText(node)} which is argument `${sym.name}` of method ${method.name}() must be ${sym.typ}: " +
                  s"${listener.nodeToText(id.getParent.asInstanceOf[CymbolAST])}"
              )
            }
          }
          method.typ
        case _ => NullType
      }
    } else {
      listener.error(s"line ${id.getLine} ${listener.nodeToText(id)} must be a function: " +
        s"${listener.nodeToText(id.getParent.asInstanceOf[CymbolAST])}")
      InvalidType
    }

  }

  def member(expr: CymbolAST, field: CymbolAST, symTab: SymbolTable, listener: CymbolListener): Type = {
    val evalType = expr.evalType.getOrElse(NullType)
    if(evalType.isInstanceOf[StructType]) {
      val exprStruct = field.scope.flatMap(_.resolve(evalType.name)).getOrElse(NullSymbol)
      val member = exprStruct match {
        case struct: StructSymbol => struct.resolveMember(field.getText)
        case _ => None
      }
      field.symbol = member
      member.map(_.typ).getOrElse(NullType)
    } else {
      listener.error(s"line ${expr.getLine} ${listener.nodeToText(expr)} must be struct: " +
        s"${listener.nodeToText(expr.getParent.asInstanceOf[CymbolAST])}")
      InvalidType
    }
  }

  def promoteDeclExprType(id: CymbolAST, expr: CymbolAST, listener: CymbolListener): Unit = {
    val idType = id.symbol.map(_.typ).getOrElse(NullType)
    expr.promotionType = expr.evalType.flatMap(_.promote(idType))
    if(!canAssignTo(expr.evalType.getOrElse(NullType), idType, expr.promotionType.getOrElse(NullType))) {
      listener.error(s"line ${id.getLine} ${listener.nodeToText(id)} have incompatible types: " +
        s"${listener.nodeToText(id.getParent.asInstanceOf[CymbolAST])}")
    }
  }

  def promoteReturnExprType(method: MethodSymbol, expr: CymbolAST, listener: CymbolListener): Unit = {
    val returnType = method.typ
    expr.promotionType = expr.evalType.flatMap(_.promote(returnType))
    if(!canAssignTo(expr.evalType.getOrElse(NullType), returnType, expr.promotionType.getOrElse(NullType))) {
      listener.error(s"line ${expr.getLine} `${method.name}(): ${method.typ}` have incompatible types: " +
        s"${listener.nodeToText(expr.getParent.asInstanceOf[CymbolAST])}")
    }
  }

  def promoteAssignExprType(lhs: CymbolAST, rhs: CymbolAST, listener: CymbolListener): Unit = {
    val decl = lhs.evalType.getOrElse(NullType)
    rhs.promotionType = rhs.evalType.flatMap(_.promote(decl))
    if(!canAssignTo(rhs.evalType.getOrElse(NullType), decl, rhs.promotionType.getOrElse(NullType))) {
      listener.error(s"line ${lhs.getLine} ${listener.nodeToText(lhs)}, ${listener.nodeToText(rhs)} have incompatible types: " +
        s"${listener.nodeToText(lhs.getParent.asInstanceOf[CymbolAST])}")
    }
  }

  def ifStat(condition: CymbolAST, listener: CymbolListener): Unit = {
    if(condition.evalType.getOrElse(NullType) != tBoolean) {
      listener.error(s"line ${condition.getLine} if condition ${listener.nodeToText(condition)} must be boolean: " +
        s"${listener.nodeToText(condition.getParent.asInstanceOf[CymbolAST])}")
    }
  }

  def canAssignTo(valueType: Type, declType: Type, promotion: Type): Boolean =
    valueType == declType || promotion == declType

}
