package models.service

import javax.inject.Inject

import models.{Book, FavouriteStudio}
import models.mysql.BookDAO
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, Writes}
import play.api.libs.functional.syntax._
import play.api.libs.json._



@javax.inject.Singleton
class BookService @Inject() (bookDAO: BookDAO) {

  /*implicit val bookWrites: Writes[Book] = (
    (JsPath \ "bookId").write[Int] and
      (JsPath \ "author").write[String] and
      (JsPath \ "bookName").write[String]) (unlift(Book.unapply))*/

  /*implicit val favouriteStudioWrites: Writes[Book] = (
    (JsPath \ "userId").write[Int] and
      (JsPath \ "studioId").write[Int])(unlift(Book.unapply))*/

  def getAllBooks: List[Book] =
    bookDAO.getAllBooks
}
