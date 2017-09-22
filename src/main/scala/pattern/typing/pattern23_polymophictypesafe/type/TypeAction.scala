package pattern.typing.pattern23_polymophictypesafe.`type`

import pattern.typing.pattern23_polymophictypesafe.`type`.Type.{tBoolean, tFloat, tInt}
import pattern.typing.pattern23_polymophictypesafe.ast.{CymbolAST, CymbolListener}
import pattern.typing.pattern23_polymophictypesafe.symbol.{ClassSymbol, MethodSymbol, NullSymbol, Symbol}
import pattern.typing.pattern23_polymophictypesafe.symboltable.SymbolTable

import scala.collection.mutable

trait TypeAction {

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
    result match {
      case _: PointerType if aType.isInstanceOf[PointerType] => aType
      case _: PointerType if bType.isInstanceOf[PointerType] => bType
      case _ => result
    }
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

  def pointerRefCheck(expr: CymbolAST, listener: CymbolListener): Type = {
    val typ = expr.evalType.getOrElse(NullType)
    typ match {
      case pointer: PointerType => pointer.targetType
      case _ =>
        listener.error(s"${listener.nodeToText(expr)} must be a pointer")
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
    if(evalType.isInstanceOf[ClassType]) {
      val exprStruct = field.scope.flatMap(_.resolve(evalType.name)).getOrElse(NullSymbol)
      val member = exprStruct match {
        case clazz: ClassSymbol => clazz.resolveMember(field.getText)
        case _ => None
      }
      field.symbol = member
      member.map(_.typ).getOrElse(NullType)
    } else {
      listener.error(s"line ${expr.getLine} ${listener.nodeToText(expr)} must be class: " +
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

  def canAssignTo(valueType: Type, destType: Type, promotion: Type): Boolean =
    valueType.canAssignTo(destType) || promotion == destType

}
