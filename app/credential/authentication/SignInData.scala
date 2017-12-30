package credential.authentication

import play.api.libs.json.Json


case class SignInData(email: String, password: String, rememberMe: Boolean)

object SignInData {
  implicit val jsonFormat = Json.format[SignInData]
}
