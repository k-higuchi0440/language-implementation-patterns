package pattern.interpreter.pattern25_treebased.pie.symbol

import pattern.interpreter.pattern25_treebased.pie.ast.PieAST

/**
  * 記号
  *
  * "記号とは、変数やメソッドなどのプログラム実体につける名前にすぎません"
  *
  */
sealed trait Symbol {
  def name: String
  override def toString: String =
    s"Symbol($name)"
}

case object NullSymbol extends Symbol {
  override val name = "null"
}

case class VariableSymbol(name: String) extends Symbol {
  override def toString: String = s"Variable${super.toString}"
}

case class FunctionSymbol(
  name: String,
  arguments: Symbols,
  var blockAST: Option[PieAST],
) extends Symbol {

  def hasSameNumOfArguments(num: Int): Boolean = arguments.size == num

  override def toString: String = s"Function${super.toString}"
}

object FunctionSymbol {
  def apply(
    name: String,
  ): FunctionSymbol = new FunctionSymbol(name, VectorSymbols(), None)
}

case class StructSymbol(
  name: String,
  members: Symbols,
) extends Symbol {
  def resolveMember(name: String): Option[Symbol] = members.resolve(name)
  override def toString: String = s"Struct${super.toString}"
}

object StructSymbol {
  def apply(
    name: String,
   ): StructSymbol = new StructSymbol(name, MapSymbols())
}
