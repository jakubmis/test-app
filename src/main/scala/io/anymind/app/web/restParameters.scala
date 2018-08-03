package io.anymind.app.web

import scala.util.Try

object RestServerHost {

  val ipRegex = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}"

  def apply(host: String): Try[RestServerHost] = {
    Try {
      require(host.matches(ipRegex))
      new RestServerHost(host)
    }
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
