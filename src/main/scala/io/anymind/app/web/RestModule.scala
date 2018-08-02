package io.anymind.app.web

import com.typesafe.scalalogging.StrictLogging
import io.anymind.app.{ConfigModule, ConfigurationException}

import scala.util.control.NonFatal
import scala.util.{Failure, Try}

trait RestModule extends StrictLogging {
  this: ConfigModule
  =>
  private lazy val restServerHost: RestServerHost = {
    val param = "rest-server.host"
    Try(config.getString(param)).flatMap { host =>
      RestServerHost(host)
    }.recoverWith {
      case NonFatal(ex) => Failure(new ConfigurationException(s"Error while reading param $param", ex))
    }.get
  }

  private lazy val restServerPort: RestServerPort = {
    val param = "rest-server.port"
    Try(config.getInt(param)).flatMap { port =>
      RestServerPort(port)
    }.recoverWith {
      case NonFatal(ex) => Failure(new ConfigurationException(s"Error while reading param $param", ex))
    }.get
  }

  lazy val restServer: AkkaRestServer = new AkkaRestServer(
    restServerHost,
    restServerPort,
    actorSystem,
    nonBlockingExecContext
  )
  restServer.started

}
