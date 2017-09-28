package pattern.interpreter.pattern24_syntaxdirected.q

import scala.collection.mutable

case class Table(name: String, primaryKey: String) {

  private val _rows: mutable.Map[Value, Row] = mutable.Map.empty
  private val _columns: mutable.Buffer[String] = mutable.Buffer.empty

  def rows: Map[Value, Row] = _rows.clone().toMap
  def columns: mutable.Buffer[String] = _columns.clone()

  def getRow(primaryKeyValue: Value): Option[Row] = _rows.get(primaryKeyValue)

  def addColumn(name: String): Unit = _columns.prepend(name)

  def addRow(row: Row): Unit = {
    val primaryKeyValue = row.getValue(primaryKey)
    _rows.update(primaryKeyValue, row)
  }

  override def toString: String =
    s"""
       |Table(
       |  name = $name
       |  _columns = ${_columns.mkString(", ")}
       |  _rows = ${_rows.mkString(", ")}
       |)
     """.stripMargin
}
