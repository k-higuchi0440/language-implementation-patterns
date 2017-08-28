package pattern.parsing

/**
  * パターン7: 述語制御構文解析器
  *
  * "本パターンは、あらゆる下向き構文解析器を拡張して、
  *  構文を決定しやすくするために任意の真偽値式を使えるようにします。
  *  これらの真偽値式は意味述語と呼ばれ、どのような意味のときに構文候補を適用できるかを指定します"
  *
  * "意味述語が必要となるのは、(略)実行時の情報を使わないと構文候補を区別できない場合です"
  *
  * ex) C++の変数宣言
  *
  * volatile unsigned long int x;
  *
  * "型指定子にはconst T y;のように型名称を使うこともできます"
  *
  * この場合、構文から、ユーザー定義の型名称とユーザー定義の変数名を区別することが出来ない
  * そこで、型表(type table)から先読みトークンの文字列を検索し、true/falseを返す処理が必要となる
  *
  * type: 'volatile' | 'int' | 'long' | {isTypeName(token)}? name // nameが型表にある場合だけtypeにマッチする
  *
  */
package object pattern7_predicatedparser