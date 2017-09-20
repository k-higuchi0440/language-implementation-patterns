package pattern.typing.pattern21_promotetype.ast

import antlr3.pattern21_promotetype.cymbol.CymbolParser
import org.antlr.runtime.TokenRewriteStream
import org.antlr.runtime.tree.TreeVisitorAction
import pattern.typing.pattern21_promotetype.`type`.{InvalidType, NullType}

object TreeVisitorActions {

  def typeAction(tokens: TokenRewriteStream) = new TreeVisitorAction() {
    override def pre(t: scala.Any): AnyRef = t.asInstanceOf[AnyRef]
    override def post(t: scala.Any): AnyRef = {
      showTypes(t.asInstanceOf[CymbolAST], tokens)
      t.asInstanceOf[AnyRef]
    }
    def showTypes(node: CymbolAST, tokens: TokenRewriteStream): Unit = {
      if (node.evalType.isDefined && node.getType != CymbolParser.EXPR) {
        printf("%-17s", tokens.toString(node.getTokenStartIndex, node.getTokenStopIndex))
        val ts = node.evalType.map(_.toString).getOrElse("Missing")
        printf(" type: %-8s", ts)
        if (node.promotionType.isDefined) {
          print(s" promoted to ${node.promotionType.getOrElse(NullType)}")
        }
        println()
      }
    }
  }

  def promoteTypeAction(tokens: TokenRewriteStream) = new TreeVisitorAction() {
    override def pre(t: scala.Any): AnyRef = t.asInstanceOf[AnyRef]
    override def post(t: scala.Any): AnyRef = {
      val node = t.asInstanceOf[CymbolAST]
      if(node.promotionType.isDefined) cast(node, tokens)
      t.asInstanceOf[AnyRef]
    }
    def cast(node: CymbolAST, stream: TokenRewriteStream): Unit = {
      val cast = s"(${node.promotionType.getOrElse(InvalidType)})"
      val left =  node.getTokenStartIndex // location in token buffer
      val right = node.getTokenStopIndex
      val t = node.token // tok is node's token payload
      val token =
        if ( t.getType == CymbolParser.EXPR )
          node.getChild(0).asInstanceOf[CymbolAST].token
        else t
      if ( left == right || token.getType == CymbolParser.INDEX || token.getType == CymbolParser.CALL)
        tokens.insertBefore(left, cast)
      else {
        val original = tokens.toString(left, right)
        tokens.replace(left, right, s"$cast($original)")
      }
    }
  }
}
