package controllers

import javax.inject._

import com.mohiva.play.silhouette.api.Silhouette
import credential.authentication.DefaultEnv
import models.FavouriteStudio
import models.service.FavouriteStudioService
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Action, _}

import scala.concurrent.Future


@Singleton
class StudioController @Inject() (cc: ControllerComponents,
                                  val silhouette: Silhouette[DefaultEnv],
                                  service: FavouriteStudioService) extends AbstractController(cc) {

  implicit val userFormat: Reads[(String, Long)] = formatter.JsonFormatter.userFormat

  implicit val StudioReads: Reads[(Int, Int)] = (
    (JsPath \ 'id1).read[Int] and (JsPath \ 'id2).read[Int]) tupled

  implicit val favouriteStudioWrites: Writes[FavouriteStudio] = (
    (JsPath \ "userId").write[Int] and
    (JsPath \ "studioId").write[Int])(unlift(FavouriteStudio.unapply))

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
    Ok(Json.obj("data" -> service.getAllUsers))
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
