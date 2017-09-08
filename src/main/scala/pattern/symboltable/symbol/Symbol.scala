package pattern.symboltable.symbol

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
case class BuiltinTypeSymbol(name: String) extends Symbol with Type {
  override def typ: Type = this
}
