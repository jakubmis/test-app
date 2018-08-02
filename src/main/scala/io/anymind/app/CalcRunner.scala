package io.anymind.app

import io.anymind.app.web.RestModule

object CalcRunner extends App
  with ConfigModule
  with RestModule {

  override def configFileName = "application.conf"

}
