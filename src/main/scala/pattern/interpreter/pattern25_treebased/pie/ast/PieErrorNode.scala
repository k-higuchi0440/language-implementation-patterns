package pattern.interpreter.pattern25_treebased.pie.ast

import org.antlr.runtime.{RecognitionException, Token, TokenStream}

class PieErrorNode(
  input: TokenStream,
  start: Token,
  stop: Token,
  e: RecognitionException
) extends PieAST(None, Some(start))
