package formatter

import models.Book
import play.api.libs.json.{JsPath, Reads, Writes}
import play.api.libs.functional.syntax._

object JsonFormatter {

  implicit val userReads: Reads[(String, Long)] = (
    (JsPath \ 'name).read[String] and (JsPath \ 'age).read[Long]) tupled

  implicit val bookWrites: Writes[Book] = (
    (JsPath \ "bookId").write[Int] and
      (JsPath \ "author").write[String] and
      (JsPath \ "bookName").write[String]
    )(unlift(Book.unapply))

  implicit val bookReads: Reads[(Int, String, String)] = (
    (JsPath \ 'bookId).read[Int] and
      (JsPath \ 'author).read[String] and
      (JsPath \ 'bookName).read[String]) tupled
}
