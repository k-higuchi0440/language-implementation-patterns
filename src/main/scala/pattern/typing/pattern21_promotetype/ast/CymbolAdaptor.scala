package pattern.typing.pattern21_promotetype.ast

import org.antlr.runtime.tree.CommonTreeAdaptor
import org.antlr.runtime.{RecognitionException, Token, TokenStream}

object CymbolAdaptor extends CommonTreeAdaptor {
  override def create(payload: Token): AnyRef = CymbolAST(payload)
  override def dupNode(t: scala.Any): AnyRef = if(t == null) null else create(t.asInstanceOf[Token])
  override def errorNode(input: TokenStream, start: Token, stop: Token, e: RecognitionException): AnyRef =
    new CymbolErrorNode(input, start, stop, e)
}
