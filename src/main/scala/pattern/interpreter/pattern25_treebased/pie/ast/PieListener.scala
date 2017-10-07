package pattern.interpreter.pattern25_treebased.pie.ast

import org.antlr.runtime.Token

object PieListener {
  def info(msg: String): Unit = println(s"[info] $msg")
  def error(msg: String): Unit = System.err.println(s"[error] $msg")
  def error(msg: String, token: Token): Unit = System.err.println(s"[error] line ${token.getLine}: $msg")
  def error(msg: String, ex: Exception): Unit = {
    System.err.println(s"[error] $msg")
    ex.printStackTrace(System.err)
  }
}
