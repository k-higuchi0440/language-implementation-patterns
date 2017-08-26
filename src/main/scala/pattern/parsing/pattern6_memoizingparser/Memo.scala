package pattern.parsing.pattern6_memoizingparser

import pattern.parsing.pattern6_memoizingparser.Memo.ParsePosition

class Memo[Result] private(results: Map[ParsePosition, Result]) {
  def get(position: ParsePosition): Option[Result] = {
    val got = results.get(position)
    println(s"read memo @ position $position: $got ")
    got
  }

  def memoize(position: ParsePosition, result: Result): Memo[Result] = {
    val updated = new Memo(results.updated(position, result))
    println(s"write memo @ position $position: $result")
    updated
  }

  override def toString: String = results.mkString(", ")

}

object Memo {
  type ParsePosition = Int
  def apply[Result]: Memo[Result] = new Memo[Result](Map[ParsePosition, Result]())
}