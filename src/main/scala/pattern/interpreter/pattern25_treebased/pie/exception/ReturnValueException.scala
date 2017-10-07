package pattern.interpreter.pattern25_treebased.pie.exception

class ReturnValueException private extends Throwable {
  var value: Any = _
}

object ReturnValueException {
  lazy val exception: ReturnValueException = new ReturnValueException
}
