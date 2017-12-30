package formatter

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

object JsonFormatter {

  implicit val userFormat: Reads[(String, Long)] = (
    (JsPath \ 'name).read[String] and (JsPath \ 'age).read[Long]) tupled
}
