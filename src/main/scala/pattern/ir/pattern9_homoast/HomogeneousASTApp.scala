package pattern.ir.pattern9_homoast

import pattern.ir.pattern9_homoast.tree._

object HomogeneousASTApp extends App {
  val one  = Token("1", One)
  val two  = Token("2", Two)
  val plus = Token("+", Plus)

  val oneAST = HomogeneousAST(Some(one))
  val twoAST = HomogeneousAST(Some(two))

  val plusAST = HomogeneousAST(Some(plus)).addChild(oneAST).addChild(twoAST)
  println(s"1 + 2 tree: $plusAST") // 出力: 1 + 2 tree: AST(+ AST(1) AST(2))

  // 根のないツリー = リスト
  val list = HomogeneousAST(None).addChild(oneAST).addChild(twoAST)
  println(s"1 and 2 in list: $list") // 出力: 1 and 2 in list: AST(AST(1) AST(2))
}
