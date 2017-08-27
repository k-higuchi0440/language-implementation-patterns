package pattern.parsing

/**
  * パターン1: 文法を変換して再帰的下向き認識器を作成する
  *
  * "言語仕様を定義している文法を変換して、言語内の句と文を照合する再帰的下向き解析器を構築するパターン"
  *
  * 個別のパターンとして、
  * パターン2: LL(1) 再帰的下向き字句解析器
  * パターン3: LL(1) 再帰的下向き構文解析器
  * パターン4: LL(k) 再帰的下向き構文解析器
  * パターン5: 後戻り構文解析器
  * パターン7: 述語制御構文解析器
  * がある
  *
  * "先読み予測式にどのような性格のものを選ぶかによって、解析手法がどれだけ強力になるかが決まります"
  * LL(1):    "予測式が記号を一個だけ先読みして調べることを意味します"
  * LL(k):    "k個の先読み記号を調べます"(k: あらかじめ決め打ちの数値)
  * 後戻り:   "任意の先読み"(決め打ちでは対応できない場合があるため)
  * 述語制御: "ユーザの定義する任意の実行時条件を使います"(実行時の情報がないと構文が決まらない場合があるため)
  *
  * 単語について
  * LL(from "L"eft to right, performing "L"eftmost derivation)
  *
  * wikipediaの引用
  * "In computer science, an LL parser is a top-down parser for a subset of context-free languages.
  *  It parses the input from Left to right, performing Leftmost derivation of the sentence."
  *
  * したがって、LRなどもある。
  * またLALL, LARRなどとも言うが、LAは前述した先読み(LookAhead)予測を意味する
  *
  */
package object pattern1_grammar