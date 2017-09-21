package pattern.typing.pattern22_typesafe.listener

import org.antlr.runtime.TokenStream
import pattern.typing.pattern21_promotetype.ast.CymbolAST

class CymbolListener(tokens: TokenStream) {
  def info(msg: String): Unit = println(s"[info] $msg")
  def error(msg: String): Unit = System.err.println(s"[error] $msg")
  def nodeToText(node: CymbolAST): String = {
    def toText(nodeType: String): String = s"`${tokens.toString(node.getTokenStartIndex, node.getTokenStopIndex)}$nodeType`"
    node.evalType match {
      case Some(nodeType) => toText(s" (type: $nodeType)")
      case None           => toText("")
    }
  }
}
