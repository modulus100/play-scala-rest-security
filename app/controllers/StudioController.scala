package controllers

import javax.inject._

import com.mohiva.play.silhouette.api.Silhouette
import credential.authentication.DefaultEnv
import models.{Book, FavouriteStudio}
import models.service.{BookService, FavouriteStudioService}
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Action, _}

import scala.concurrent.Future


@Singleton
class StudioController @Inject() (cc: ControllerComponents,
                                  val silhouette: Silhouette[DefaultEnv],
                                  service: FavouriteStudioService,
                                  service2: BookService) extends AbstractController(cc) {

  implicit val userFormat: Reads[(String, Long)] = formatter.JsonFormatter.userFormat

  implicit val StudioReads: Reads[(Int, Int)] = (
    (JsPath \ 'id1).read[Int] and (JsPath \ 'id2).read[Int]) tupled

  implicit val favouriteStudioWrites: Writes[FavouriteStudio] = (
    (JsPath \ "userId").write[Int] and
    (JsPath \ "studioId").write[Int])(unlift(FavouriteStudio.unapply))

  implicit val booksWrites: Writes[Book] = (
    (JsPath \ "bookId").write[Int] and
      (JsPath \ "author").write[String] and
      (JsPath \ "bookName").write[String]
    )(unlift(Book.unapply))

  implicit val bookFormat: Reads[(Int, String, String)] = (
    (JsPath \ 'bookId).read[Int] and
      (JsPath \ 'author).read[String] and
      (JsPath \ 'bookName).read[String]) tupled

  /*
  implicit val userFormat: Reads[(String, Long)] = (
    (JsPath \ 'name).read[String] and (JsPath \ 'age).read[Long]) tupled
  * */

  /*implicit val residentWrites: Writes[Book] = (
    (JsPath \ "name").write[String] and
      (JsPath \ "age").write[Int] and
      (JsPath \ "role").writeNullable[String]
    )(unlift(Book.unapply))*/

  def add(userId: Int, studioId: Int) = Action {
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
  }
}
