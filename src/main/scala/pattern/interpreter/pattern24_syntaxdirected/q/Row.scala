package pattern.interpreter.pattern24_syntaxdirected.q

import org.antlr.runtime.Token

import scala.collection.mutable

case class Row(columns: mutable.Buffer[String]) {
  private val colValMap: mutable.Map[String, Value] =
    columns.foldLeft(mutable.Map.empty[String, Value])((map, col) => map.updated(col, Value.nullValue))

  def getValue(column: String): Value = colValMap.getOrElse(column, Value.nullValue)

  def getValues: mutable.Buffer[Value] = colValMap.values.toBuffer

  def getValuesOf(columns: mutable.Buffer[Token]): mutable.Buffer[Value] = {
    columns
      .foldLeft(mutable.Buffer.empty[Option[Value]])((seq, token) => colValMap.get(token.getText) +: seq)
      .collect { case Some(value) => value }
  }

  def setValue(column: String, value: Value): Unit = colValMap.update(column, value)

  override def toString: String = s"Row(${colValMap.values.mkString(", ")})"
}
