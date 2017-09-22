package pattern.typing.pattern23_polymophictypesafe.scope

trait GlobalScope extends BaseScope {
  override val scopeName: String = "Global"
  override val symbolName: Option[String] = None
  override def enclosingScope: Option[Scope] = None
}
