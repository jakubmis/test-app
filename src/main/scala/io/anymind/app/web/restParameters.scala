package io.anymind.app.web

import scala.util.Try

object RestServerHost {
  def apply(host: String): Try[RestServerHost] = {
    //TODO valiate the host
    Try(new RestServerHost(host))
  }
}

class RestServerHost private(val value: String)

object RestServerPort {
  def apply(port: Int): Try[RestServerPort] = {
    Try {
      require(port > 0, "port has to be > 0")
      require(port < 65535, "port has to be < 65535")
      new RestServerPort(port)
    }
  }
}

class RestServerPort private(val value: Int)
