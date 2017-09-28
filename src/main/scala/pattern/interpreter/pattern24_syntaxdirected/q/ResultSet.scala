package pattern.interpreter.pattern24_syntaxdirected.q

import scala.collection.mutable

/**
  * Selectクエリの結果
  */
case class ResultSet(value: mutable.Buffer[mutable.Buffer[Value]]) {
  def firstValue: Value = value.headOption.flatMap(_.headOption).getOrElse(Value.nullValue)
  override def toString: String =
    s"ResultSet(${value.map(_.mkString("  Result(", ", ", ")")).mkString("\n", "\n", "\n")})"
}

object ResultSet {
  def apply: ResultSet = new ResultSet(mutable.Buffer.empty)
}
