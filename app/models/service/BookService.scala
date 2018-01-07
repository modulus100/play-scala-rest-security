package models.service

import javax.inject.Inject

import models.Book
import models.mysql.BookDAO


@javax.inject.Singleton
class BookService @Inject()(bookDAO: BookDAO) {

  def saveBook(book: Book): Boolean = bookDAO.create(book)

  def getBookById(bookId: Int): List[Book] = bookDAO.read(bookId)

  def updateBook(book: Book): Boolean = bookDAO.update(book)

  def deleteBookById(bookId: Int): Boolean = bookDAO.delete(bookId)

  def isBookInDb(bookId: Int): Boolean = bookDAO.exists(bookId)

  def getAllBooks: List[Book] = bookDAO.books
}