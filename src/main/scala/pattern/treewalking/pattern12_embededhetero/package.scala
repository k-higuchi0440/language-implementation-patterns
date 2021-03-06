package pattern.treewalking

/**
  * パターン12: 組込み非均質木走査器
  *
  * "本パターンは、ノードクラス定義の中で定義した一連の再帰メソッドを使って非均質抽象構文木を走査します"
  *
  * "オブジェクト指向プログラムを使う場合は、木走査メソッドはノード定義に付け加えるもの、と考えるのが自然です"
  *
  * "例えば、木をテキスト形式にして書き戻すのであれば、
  *  print()という抽象メソッドを根クラスの中に定義することができます。
  *  式を評価するのであれば、eval()という抽象メソッドを抽象式クラスに追加できます"
  *
  * "これは最も簡単に理解できる木走査パターンですが、規模が大きくなるといずれはうまくいかなくなります。
  *  木走査コードがあらゆるノード定義の中に分散するため、
  *  この方法がうまく動作するのはノード定義の数が数えるほどしかない場合です"
  *
  *  "さらに大事なことに、ソースコードが手に入らない
  *  （または、木走査の機能を実行中に変更しないといけない）場合は、
  *  「パターン13　外部木訪問器」を代わりに使わないといけません"
  *
  * ※実装は、非均質ASTのtoStringメソッドで実装して分かっているので省略
  *
  */
package object pattern12_embededhetero
