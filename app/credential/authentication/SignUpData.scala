package credential.authentication

import play.api.libs.json.{Json, OFormat}


case class SignUpData(firstName: String, lastName: String, email: String, password: String)

object SignUpData {
  implicit val jsonFormat: OFormat[SignUpData] = Json.format[SignUpData]
}
