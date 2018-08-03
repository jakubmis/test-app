package io.anymind.app

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}

trait ConfigModule {
  def configFileName: String

  lazy val config: Config = {
    ConfigFactory.load(configFileName)
  }

  implicit lazy val actorSystem: ActorSystem = ActorSystem(config.getString("actor-system-name"), config)
  implicit lazy val materializer: ActorMaterializer = ActorMaterializer()
  lazy val nonBlockingExecContext: NonBlockingExecContext = NonBlockingExecContext(actorSystem.dispatcher)

}
