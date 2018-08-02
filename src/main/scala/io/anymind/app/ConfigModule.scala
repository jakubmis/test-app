package io.anymind.app

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

trait ConfigModule {
  def configFileName: String

  lazy val config: Config = {
    ConfigFactory.load(configFileName)
  }

  lazy val actorSystem: ActorSystem = ActorSystem(config.getString("actor-system-name"), config)
  lazy val nonBlockingExecContext: NonBlockingExecContext = NonBlockingExecContext(actorSystem.dispatcher)

}
