package pattern.interpreter.pattern24_syntaxdirected.q

import antlr3.pattern24_syntaxdirected.q.{QLexer, QParser}
import org.antlr.runtime.{ANTLRStringStream, CommonTokenStream, Token}

import scala.collection.mutable

class QInterpreter(string: String) {

  private val lexer  = new QLexer(new ANTLRStringStream(string))
  private val tokens = new CommonTokenStream(lexer)
  private val parser = new QParser(tokens, this)

  private val globals = mutable.Map.empty[String, ResultSet]
  private val tables  = mutable.Map.empty[String, Table]

  def run(): Unit = parser.program()

  def createTable(tableName: String, primaryKey: String, columns: mutable.Buffer[Token]): Unit = {
    val table = Table(tableName, primaryKey)
    columns.foreach(token => table.addColumn(token.getText))
    tables.update(tableName, table)
  }

  def insertInto(tableName: String, row: Row): Unit = {
    tables.get(tableName) match {
      case Some(table) => table.addRow(row)
      case None        => QInterpreterListener.error(s"No such table: $tableName")
    }
  }

  def select(tableName: String, columns: mutable.Buffer[Token]): Option[ResultSet] =
    tables.get(tableName) match {
      case Some(table) =>
        val result = table.rows.values.map {
          row => columns.map(token => row.getValue(token.getText))
        }
        Some(ResultSet(result.toBuffer))
      case None =>
        QInterpreterListener.error(s"No such table: $tableName")
        None
    }

  def select(tableName: String, columns: mutable.Buffer[Token], whereKey: String, value: Value): Option[ResultSet] = {
    tables.get(tableName) match {
      case Some(table) =>
        if(whereKey == table.primaryKey) {
          val result = table.getRow(value).map { row =>
            columns.map(token => row.getValue(token.getText))
          }
          result.map(buf => ResultSet(mutable.Buffer(buf)))
        } else {
          val result = table.rows.values.collect {
            case row: Row if row.getValue(whereKey) == value =>
              columns.map(token => row.getValue(token.getText))
          }
          Some(ResultSet(result.toBuffer))
        }
      case None =>
        QInterpreterListener.error(s"No such table: $tableName")
        None
    }
  }

  def getTable(tableName: String): Option[Table] = tables.get(tableName)

  def store(name: String, resultSet: ResultSet): Unit = globals.update(name, resultSet)

  def load(name: String): Option[ResultSet] = globals.get(name)

  def print(resultSet: ResultSet): Unit = println(resultSet)
}
