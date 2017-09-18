package pattern.typing.pattern20_computetype.scope

trait GlobalScope extends Scope {
  override val scopeName: String = "Global"
  override val symbolName: Option[String] = None
  override def enclosingScope: Option[Scope] = None
}
