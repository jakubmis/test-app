package io.anymind.app

abstract class CalcException(msg: String, cause: Option[Throwable])
  extends RuntimeException(msg, cause.orNull) {

  def this(msg: String) = this(msg, None)

  def this(msg: String, cause: Throwable) = this(msg, Some(cause))
}

class ConfigurationException(msg: String, cause: Option[Throwable])
  extends CalcException(msg, cause) {

  def this(msg: String) = this(msg, None)

  def this(msg: String, cause: Throwable) = this(msg, Some(cause))
}
