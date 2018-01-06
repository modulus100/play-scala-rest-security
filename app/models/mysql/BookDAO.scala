package models.mysql

import javax.inject.Inject

import anorm.SqlParser._
import anorm.{Macro, RowParser, _}
import anorm.SqlParser._
import anorm.{Macro, RowParser, _}
import models.FavouriteStudio
import play.api.db.DBApi
import models.{Book, FavouriteStudio}
import play.api.db.DBApi


@javax.inject.Singleton
class BookDAO @Inject() (dbapi: DBApi) {

  private val db = dbapi.database("default")

  val parser: RowParser[Book] = Macro.namedParser[Book]

  def create(book: Book) = {
    val query =
      """
        INSERT INTO Book (bookId, author, bookName)
        VALUES ({bookId}, {studioId}, {bookName});
      """
    db.withConnection { implicit c =>
      SQL(query)
        .on("bookId" -> book.bookId, "studioId" -> book.author, "bookName" -> book.bookId)
        .executeInsert()
    }
  }

  def delete(book: Book) = {
    val query =
      """
        DELETE FROM Book
        WHERE bookId={bookId}
        LIMIT 1;
      """
    db.withConnection { implicit c =>
      SQL(query)
        .on("bookId" -> book.bookId)
        .executeUpdate()
    }
  }

  def exists(book: Book): Boolean = {
    val query =
      """
        SELECT COUNT(*) as numMatches
        FROM Book
        WHERE bookId={bookId};
      """
    val result = db.withConnection { implicit c =>
      SQL(query)
        .on("bookId" -> book.bookId)
        .as(scalar[Long].single)
        .asInstanceOf[Long]
    }
    result != 0
  }

  def getBookById (bookId: Int): List[Book] = {
    val query =
      """
        SELECT *
        FROM Book
        WHERE bookId={bookId};
      """
    db.withConnection { implicit c =>
      SQL(query).on("bookId" -> bookId).as(parser.*)
    }
  }

  def getAllBooks: List[Book] = {
    val query =
      """
        SELECT bookId, author, bookName
        FROM Book;
      """
    db.withConnection { implicit c =>
      SQL(query).on().as(parser.*)
    }
  }
}
