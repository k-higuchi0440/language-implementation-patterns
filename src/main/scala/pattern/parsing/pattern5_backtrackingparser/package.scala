package pattern.parsing

/**
  * パターン5: 後戻り構文解析器
  *
  * "本パターンを使うと、あらゆる再帰的下向き認識器に対して
  *  投機的構文解析（任意長先読み）機能を追加することができます"
  *
  * 投機的構文解析:
  * 要するに、まず実際に構文解析してみて、構文候補と一致しない場合は、
  * 最初の状態に巻き戻したうえで、次の構文候補の投機的照合を行うということ
  *
  * ex) C++の関数定義と宣言
  *
  * "規則function用に構文解析メソッドを作ると、次の疑似コードのような構造になります"
  *
  * void function() {
  *   if( <投機的照合でdefと一致した> ) def();
  *   else if( <投機的照合でdeclと一致した> ) decl();
  *   else throw new RecognitionError("expecting function")
  * }
  *
  * "C++関数の冒頭の長さは任意であり、どこまでも長くなる可能性があるため、
  *  文の左側から固定長の先読みをしても判定の手掛かりとなる字句が必ず登場するとは限りません。
  *  ということは、"LL(k)"では力不足であり、"上記の2つを区別することができません
  *
  * void bar() {...} // 関数定義
  * void bar();      // 関数宣言(前方宣言)
  *
  *
  * 投機的構文解析の欠点
  * 1. "デバッグ作業が難しくなる"
  * 2. "後戻り処理は極端に遅くなることがあ"る(巻き戻すことで解析済みのトークンをまた解析することになるから)
  *    => メモ化を利用して"冗長な構文解析処理を避けさえすればいいのです"
  */
package object pattern5_backtrackingparser