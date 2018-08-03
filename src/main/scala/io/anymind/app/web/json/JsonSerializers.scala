package io.anymind.app.web.json

import io.anymind.app.web.command.CalculateCommand
import io.anymind.app.web.dto.{RestError, Result}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

trait JsonSerializers {
  implicit val calculateCommandDecoder: Decoder[CalculateCommand] = deriveDecoder[CalculateCommand]
  implicit val restErrorEncoder: Encoder[RestError] = deriveEncoder[RestError]
  implicit val resultEncoder: Encoder[Result] = deriveEncoder[Result]
}
