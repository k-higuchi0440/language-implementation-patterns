package pattern.typing.pattern21_promotetype.ast

import org.antlr.runtime.tree.{CommonErrorNode, CommonTree}
import org.antlr.runtime.{RecognitionException, Token, TokenStream}
import pattern.typing.pattern21_promotetype.`type`.Type
import pattern.typing.pattern21_promotetype.scope.Scope
import pattern.typing.pattern21_promotetype.symbol.Symbol

class CymbolAST(
  var scope: Option[Scope],
  var symbol: Option[Symbol],
  var evalType: Option[Type],
  var promotionType: Option[Type],
  _token: Token,
) extends CommonTree(_token) {
  override def toString: String = s"${super.toString}${evalType.map(e => s" <${e.name}>").getOrElse("")}"
}

object CymbolAST {
  def apply(token: Token) = new CymbolAST(None, None, None, None, token)
}

class CymbolErrorNode(
  input: TokenStream,
  start: Token,
  stop: Token,
  e: RecognitionException
) extends CymbolAST(None, None, None, None, null) {
  val delegate: CommonErrorNode = new CommonErrorNode(input, start, stop, e)
  override def isNil: Boolean = delegate.isNil
  override def getType: Int = delegate.getType
  override def getText: String = delegate.getText
  override def toString: String = delegate.toString
}
