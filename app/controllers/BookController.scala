package controllers

import javax.inject._

import com.mohiva.play.silhouette.api.Silhouette
import credential.authentication.DefaultEnv
import io.swagger.annotations.{Api, ApiOperation}
import models.Book
import models.service.BookService
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.libs.json._
import play.api.mvc.{Action, _}

import scala.concurrent.Future

@Api(value = "Crud API")
@Singleton
class BookController @Inject()(cc: ControllerComponents,
                               val silhouette: Silhouette[DefaultEnv],
                               service: BookService) extends AbstractController(cc) {

  val jsonHeader = "data"
  implicit val bookWrites: Writes[Book] = formatter.JsonFormatter.bookWrites
  implicit val bookReads: Reads[(Int, String, String)] = formatter.JsonFormatter.bookReads

  @ApiOperation(value = "Get bad password value")
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

  @ApiOperation(value = "Get bad password value")
  def read(bookId: Int) = silhouette.SecuredAction.async {
    Future.successful(
      Ok(Json.obj(jsonHeader -> service.getBookById(bookId))))
  }

  @ApiOperation(value = "Get bad password value")
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

  @ApiOperation(value = "Get bad password value")
  def delete(bookId: Int) = silhouette.SecuredAction {
    if (service.deleteBookById(bookId))
      Ok(Json.obj(jsonHeader -> "Book succesfully deleted"))
    else
      BadRequest(Json.obj(jsonHeader -> "No book with this id"))
  }

  @ApiOperation(value = "Get bad password value")
  def allBooks = silhouette.SecuredAction.async {
    Future.successful(
      Ok(Json.obj(jsonHeader -> service.getAllBooks)))
  }
}
