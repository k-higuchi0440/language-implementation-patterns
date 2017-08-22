package pattern.parsing

/**
  * パターン4: LL(k) 再帰的下向き構文解析器
  *
  * "このパターンでは、k>1個の先読み字句を使って、句を構成する字句列の構文構造を解析します"
  *
  * ex) LL(2)
  *
  * list     : '[' elements ']' ;
  * elements : element (',' element)* ;
  * element  : NAME '=' NAME
  *          | NAME
  *          | list
  *          ;
  *
  * 構文規則elementの"先頭2つの構文候補が両方とも同じ字句NAMEで始まる"ため、
  * "この2つの構文規則を区別するためには、先読み字句が2つ必要です"
  *
  */
package object pattern4_llkparser