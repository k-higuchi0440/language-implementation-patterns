package pattern.interpreter.pattern25_treebased.pie.ast

import org.antlr.runtime.tree.CommonTreeAdaptor
import org.antlr.runtime.{RecognitionException, Token, TokenStream}

object PieAdaptor extends CommonTreeAdaptor {
  override def create(token: Token): AnyRef = PieAST(token)
  override def dupNode(t: scala.Any): AnyRef = if(t == null) null else create(t.asInstanceOf[Token])
  override def errorNode(input: TokenStream, start: Token, stop: Token, e: RecognitionException): AnyRef =
    new PieErrorNode(input, start, stop, e)
}
