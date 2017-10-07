package pattern.interpreter.pattern25_treebased.pie.ast

import org.antlr.runtime.Token
import org.antlr.runtime.tree.CommonTree
import pattern.interpreter.pattern25_treebased.pie.scope.Scope

case class PieAST(var scope: Option[Scope], _token: Option[Token]) extends CommonTree(_token.orNull)

object PieAST {
  def apply(token: Token): PieAST = new PieAST(None, Some(token))
}
