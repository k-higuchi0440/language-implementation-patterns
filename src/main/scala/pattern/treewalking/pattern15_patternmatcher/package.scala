package pattern.treewalking

/**
  * パターン15: 木パターン照合器
  *
  * "本パターンは、木を走査しながら処理対象の木パターンを見つけたらアクションや木書換え処理を発動します。
  *  木を照合して書き換える処理のことを、正式な名称では項書換えと呼びます"
  *
  * "本パターン照合器を使う場合は、木文法を使うときとは2つの大事な点で異なります。
  *  ・処理したい部分木パターンだけを指定すれば十分です。
  *  ・木走査を支持する必要がありません。"
  *
  *
  * ※ANTLR4ではASTも木文法も廃止されたのでこのパターンは使えない
  *
  * https://github.com/antlr/antlr4/blob/master/doc/faq/general.md
  *
  * "Q: What are the main design decisions in ANTLR4?
  *
  *  Ease-of-use over performance. I will worry about performance later. Simplicity over complexity. For example,
  *  I have taken out explicit/manual AST construction facilities and the tree grammar facilities. For 20 years
  *  I've been trying to get people to go that direction, but I've since decided that it was a mistake. It's much better
  *  to give people a parser generator that can automatically build trees and then let them use pure code to do whatever
  *  tree walking they want. People are extremely familiar and comfortable with visitors, for example."
  *
  *
  */
package object pattern15_patternmatcher