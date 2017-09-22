package pattern.typing.pattern23_polymophictypesafe.ast

import antlr3.pattern23_polymophictypesafe.cymbol.CymbolParser
import org.antlr.runtime.TokenRewriteStream
import org.antlr.runtime.tree.TreeVisitorAction
import pattern.typing.pattern23_polymophictypesafe.`type`.NullType

object TreeVisitorActions {

  def typeAction(tokens: TokenRewriteStream) = new TreeVisitorAction() {
    override def pre(t: scala.Any): AnyRef = t.asInstanceOf[AnyRef]
    override def post(t: scala.Any): AnyRef = {
      showTypes(t.asInstanceOf[CymbolAST], tokens)
      t.asInstanceOf[AnyRef]
    }
    def showTypes(node: CymbolAST, tokens: TokenRewriteStream): Unit = {
      if (node.evalType.isDefined && node.getType != CymbolParser.EXPR) {
        printf("%-9s", tokens.toString(node.getTokenStartIndex, node.getTokenStopIndex))
        val ts = node.evalType.map(_.toString).getOrElse("Missing")
        printf(" type: %-8s", ts)
        if (node.promotionType.isDefined) {
          print(s" promoted to ${node.promotionType.getOrElse(NullType)}")
        }
        println()
      }
    }
  }
}
