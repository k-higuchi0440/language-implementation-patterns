package pattern.treewalking

/**
  * パターン14: 木文法
  *
  * "木文法は、外部訪問器を簡潔にきちんとした形式で構築するための方法です。
  *  木文法から生成された訪問器は、通常、木構文解析器（tree parser）と呼ばれます"
  *
  *  "木文法は、部分木パターンと照合することもできるという違いはあるものの、
  *   伝統的な構文解析器文法とよく似ています。
  *   構文解析器文法のときと同じように情報を抽出したり
  *   入力（この場合は木）を認識したりするためのアクションを組み込むことができます"
  *
  *
  * ※ANTLR4では木文法のようにアクションを文法内に記述するよりも、
  * 　Listener/Visitorを使って文法外にアクションを記述する方が良しとされている
  *
  * https://github.com/antlr/antlr4/blob/master/doc/listeners.md
  *
  * "Listeners and visitors are great because they keep application-specific code out of grammars, making grammars
  *  easier to read and preventing them from getting entangled with a particular application."
  *
  *
  * ※また、ANTLR4ではASTも木文法も廃止されたのでこのパターンは使えない
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
  */
package object pattern14_treegrammar
