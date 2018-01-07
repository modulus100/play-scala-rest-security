package controllers

import javax.inject._

import com.mohiva.play.silhouette.api.Silhouette
import credential.authentication.DefaultEnv
import models.Book
import models.service.BookService
import play.api.i18n.Messages
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Action, _}

import scala.concurrent.Future


@Singleton
class BookController @Inject()(cc: ControllerComponents,
                               val silhouette: Silhouette[DefaultEnv],
                               service: BookService) extends AbstractController(cc) {

  val jsonHeader = "data"
  implicit val userReads: Reads[(String, Long)] = formatter.JsonFormatter.userReads
  implicit val bookWrites: Writes[Book] = formatter.JsonFormatter.bookWrites
  implicit val bookReads: Reads[(Int, String, String)] = formatter.JsonFormatter.bookReads

  def create: Action[JsValue] = silhouette.SecuredAction.async(parse.json) { request =>
    request.body.validate[Book].fold(
      errors =>
        Future.successful(
          BadRequest(Json.obj(jsonHeader -> "Invalid Data", "errors" -> JsError.toJson(errors)))),
      book => {
        if (service.saveBook(book))
          Future.successful(Ok(Json.obj(jsonHeader -> "Book saved")))
        else
          Future.successful(BadRequest(Json.obj(jsonHeader -> "A book could't be saved")))
      }
    )
  }

  def read(bookId: Int) = silhouette.SecuredAction {
    Ok(Json.obj(jsonHeader -> service.getBookById(bookId)))
  }

  def update: Action[JsValue] = silhouette.SecuredAction.async(parse.json) { request =>
    request.body.validate[Book].fold(
      errors =>
        Future.successful(
          BadRequest(Json.obj(jsonHeader -> "Invalid Data", "errors" -> JsError.toJson(errors)))),
      book => {
        if (service.updateBook(book))
          Future.successful(Ok(Json.obj(jsonHeader -> "Book updated")))
        else
          Future.successful(BadRequest(Json.obj(jsonHeader -> "Book could't be updated")))
      }
    )
  }

  def delete(bookId: Int) = silhouette.SecuredAction {
    if (service.deleteBookById(bookId))
      Ok(Json.obj(jsonHeader -> "Book succesfully deleted"))
    else
      BadRequest(Json.obj(jsonHeader -> "No book with this id"))
  }

  def allBooks = silhouette.SecuredAction {
    Ok(Json.obj(jsonHeader -> service.getAllBooks))
  }

  def testJson: Action[JsValue] = silhouette.SecuredAction.async(parse.json) { request =>
    Future.successful(
      request.body.validate[(String, Long)]
        .map { case (name, age) => Ok(Json.obj("result" -> name)) }
        .recoverTotal { e => BadRequest("error:" + JsError.toJson(e))}
    )
  }

  /*
  implicit val userFormat: Reads[(String, Long)] = (
    (JsPath \ 'name).read[String] and (JsPath \ 'age).read[Long]) tupled
  * */

  /*implicit val residentWrites: Writes[Book] = (
    (JsPath \ "name").write[String] and
      (JsPath \ "age").write[Int] and
      (JsPath \ "role").writeNullable[String]
    )(unlift(Book.unapply))*/

  /*def add(userId: Int, studioId: Int) = Action {
    Ok(Json.obj("data" -> service.addFavourite(userId, studioId)))
  }

  def remove(userId: Int, studioId: Int) = Action {
    service.delete(userId, studioId)
    Ok(Json.obj("data" -> Json.obj()))
  }

  def find(userId: Int, studioId: Int) = Action {
    service.find(userId, studioId) match {
      case Some(favourite) => Ok(Json.obj("data" -> favourite))
      case None => NotFound(Json.obj("error" -> "NOT_FOUND"))
    }
  }

  def findAll(userId: Int) =
    Action {
      Ok(Json.obj("data" -> service.findAllByUser(userId)))
    }

  def getAll = Action {
    Ok(Json.obj("data" -> service2.getAllBooks))
  }

  def sayHello: Action[JsValue] = silhouette.SecuredAction.async(parse.json) { request =>
    Future.successful(
      request.body.validate[(String, Long)]
        .map { case (name, age) => Ok(Json.obj("result" -> name)) }
        .recoverTotal { e => BadRequest("error:" + JsError.toJson(e))}
    )
  }

  def studio: Action[JsValue] = silhouette.SecuredAction.async(parse.json) { request =>
    Future.successful(
      request.body.validate[FavouriteStudio]
        .map { case (studio) => Ok(Json.obj("result" -> studio)) }
        .recoverTotal { e => BadRequest("error:" + JsError.toJson(e)) })
  }*/
}
