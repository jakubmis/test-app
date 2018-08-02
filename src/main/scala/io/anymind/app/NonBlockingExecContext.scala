package io.anymind.app

import scala.concurrent.ExecutionContext

/**
  * A wrapper for execution context that should always be used only for
  * non-blocking operations and thus can be shared among all the components
  * for such operations.
  */
case class NonBlockingExecContext(ec: ExecutionContext) extends AnyVal
