package pattern.typing.pattern23_polymophictypesafe.`type`

import pattern.typing.pattern23_polymophictypesafe.`type`.Type.{tBoolean, tChar, tFloat, tInt}

import scala.annotation.tailrec

sealed trait Type {
  def name: String

  def arithmeticResultType(other: Type): Type = InvalidType
  def relationalResultType(other: Type): Type = InvalidType
  def equalityResultType(other: Type): Type = InvalidType
  def promote(other: Type): Option[Type] = None

  def canAssignTo(destType: Type): Boolean = this == destType

  override def toString: String = name
}

object NullType extends Type {
  override val name = "null"
  override def toString: String = "NullType"
}

object InvalidType extends Type {
  override val name = "Invalid"
  override def toString: String = "InvalidType"
}

object Type extends TypeAction {

  lazy val tBoolean: Type = BooleanType("Boolean")
  lazy val tChar: Type    = CharType("Char")
  lazy val tInt: Type     = IntType("Int")
  lazy val tFloat: Type   = FloatType("Float")
  lazy val tVoid: Type    = VoidType("Void")

  def definePointerType(targetType: Type): Type = PointerType(targetType)

  def defineClassType(name: String, superClass: Option[Type]): Type = ClassType(name, superClass)

}

private[`type`] case class BooleanType(name: String) extends Type {
  override def equalityResultType(other: Type): Type = other match {
    case BooleanType(_) => tBoolean
    case _              => InvalidType
  }
}

private[`type`] case class CharType(name: String) extends Type {
  override def arithmeticResultType(other: Type): Type = other match {
    case _: CharType    => tChar
    case _: IntType     => tInt
    case _: FloatType   => tFloat
    case _: PointerType => other
    case _              => InvalidType
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
    case _: IntType     => Some(tInt)
    case _: FloatType   => Some(tFloat)
    case _: PointerType => Some(other)
    case _              => None
  }
}

private[`type`] case class IntType(name: String) extends Type {
  override def arithmeticResultType(other: Type): Type = other match {
    case _: CharType    => tInt
    case _: IntType     => tInt
    case _: FloatType   => tFloat
    case _: PointerType => other
    case _              => InvalidType
  }

  override def relationalResultType(other: Type): Type = other match {
    case _: CharType    => tBoolean
    case _: IntType     => tBoolean
    case _: FloatType   => tBoolean
    case _: PointerType => tBoolean
    case _              => InvalidType
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

private[`type`] case class FloatType(name: String) extends Type {
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

private[`type`] case class VoidType(name: String) extends Type

private[`type`] case class PointerType(targetType: Type) extends Type {
  override val name: String = "Pointer"

  override def canAssignTo(destType: Type): Boolean = {
    destType match {
      case pointer: PointerType =>
        val destTargetType = pointer.targetType
        val srcTargetType  = this.targetType
        destTargetType match {
          case destClass: ClassType =>
            srcTargetType match {
              case srcClass: ClassType =>
                srcClass.isSubClassOf(destClass)
              case _ => srcTargetType == destTargetType
            }
          case _ => srcTargetType == destTargetType
        }
      case _ => false
    }
  }

  override def arithmeticResultType(other: Type): Type = other match {
    case _: CharType  => this
    case _: IntType   => this
    case _            => InvalidType
  }

  override def relationalResultType(other: Type): Type = other match {
    case _: IntType     => tBoolean
    case _: PointerType => tBoolean
    case _              => InvalidType
  }

  override def equalityResultType(other: Type): Type = other match {
    case _: IntType     => tBoolean
    case _: PointerType => tBoolean
    case _              => InvalidType
  }
}

private[`type`] case class ClassType(name: String, superClass: Option[Type]) extends Type {
  def isSubClassOf(superClass: ClassType): Boolean = {
    @tailrec
    def loop(typ: Option[Type]): Boolean = {
      typ match {
        case Some(t) =>
          if(t == superClass) true
          else loop(t.asInstanceOf[ClassType].superClass)
        case None =>
          false
      }
    }
    loop(Some(this))
  }
}