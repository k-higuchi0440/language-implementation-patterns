package pattern.interpreter.pattern24_syntaxdirected.q

import org.antlr.runtime.Token

object QInterpreterListener {
  def info(message: String): Unit = println(s"[info] $message")
  def error(message: String): Unit = Console.err.println(s"[error] $message")
  def error(message: String, exception: Exception): Unit = {
    error(message)
    exception.printStackTrace(Console.err)
  }
  def error(message: String, token: Token): Unit = error(s"line ${token.getLine}: $message")
}
