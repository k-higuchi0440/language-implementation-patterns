package pattern.interpreter.pattern24_syntaxdirected.q

case class Value(value: String) {
  override def toString: String = value
}

object Value {
  val nullValue: Value = Value("null")
}
