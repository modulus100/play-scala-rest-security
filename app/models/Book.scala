package models

import play.api.libs.json.{Json, OFormat}


case class Book(bookId: Int, author: String, bookName: String)

object Book {

  implicit val jsonFormat: OFormat[Book] = Json.format[Book]
}

