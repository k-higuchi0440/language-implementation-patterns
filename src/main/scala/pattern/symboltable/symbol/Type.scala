package pattern.symboltable.symbol

/**
  * それ自体を型として扱いたいシンボルに継承させる
  */
trait Type { self: Symbol =>
  def name: String
}
