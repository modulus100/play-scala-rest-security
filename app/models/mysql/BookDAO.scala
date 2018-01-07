package models.mysql

import javax.inject.Inject

import anorm.SqlParser._
import anorm.{Macro, RowParser, _}
import models.Book
import play.api.db.DBApi


@javax.inject.Singleton
class BookDAO @Inject()(dbapi: DBApi) {

  private val db = dbapi.database("default")

  val parser: RowParser[Book] = Macro.namedParser[Book]

  def create(book: Book) = {
    val query =
      """
        INSERT INTO Book (bookId, author, bookName)
        VALUES ({bookId}, {author}, {bookName});
      """
    val result = db.withConnection { implicit c =>
      SQL(query)
        .on("bookId" -> book.bookId, "author" -> book.author, "bookName" -> book.bookName)
        .executeInsert()
    }
    result != 0
  }

  def read(bookId: Int): List[Book] = {
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

  def update(book: Book): Boolean = {
    val query =
      """
        UPDATE Book
        SET bookId={bookId},
            author={author},
            bookName={bookName}
        WHERE bookId={bookId};
      """
    val result = db.withConnection { implicit c =>
      SQL(query)
        .on("bookId" -> book.bookId, "author" -> book.author, "bookName" -> book.bookName)
        .executeUpdate()
    }
    result != 0
  }

  def delete(bookId: Int): Boolean = {
    val query =
      """
        DELETE FROM Book
        WHERE bookId={bookId}
        LIMIT 1;
      """
    val result = db.withConnection { implicit c =>
      SQL(query)
        .on("bookId" -> bookId)
        .executeUpdate()
    }
    result != 0
  }

  def exists(bookId: Int): Boolean = {
    val query =
      """
        SELECT COUNT(*) as numMatches
        FROM Book
        WHERE bookId={bookId};
      """
    val result = db.withConnection { implicit c =>
      SQL(query)
        .on("bookId" -> bookId)
        .as(scalar[Long].single)
        .asInstanceOf[Long]
    }
    result != 0
  }

  def books: List[Book] = {
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
