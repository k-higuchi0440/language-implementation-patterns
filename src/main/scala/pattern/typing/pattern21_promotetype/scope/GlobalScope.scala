package pattern.typing.pattern21_promotetype.scope

trait GlobalScope extends BaseScope {
  override val scopeName: String = "Global"
  override val symbolName: Option[String] = None
  override def enclosingScope: Option[Scope] = None
}
