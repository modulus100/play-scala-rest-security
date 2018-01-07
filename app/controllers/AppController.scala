package controllers

import javax.inject.{Inject, Singleton}

import com.mohiva.play.silhouette.api.Silhouette
import credential.authentication.DefaultEnv
import io.swagger.annotations.{Api, ApiOperation}
import models.service.BookService
import play.api.libs.json.{JsError, JsValue, Json, Reads}
import play.api.mvc.{AbstractController, Action, ControllerComponents}

import scala.concurrent.Future


@Api(value = "App Api")
@Singleton
class AppController @Inject()(cc: ControllerComponents,
                               val silhouette: Silhouette[DefaultEnv],
                               service: BookService) extends AbstractController(cc) {

  implicit val userReads: Reads[(String, Long)] = formatter.JsonFormatter.userReads

  def testJson: Action[JsValue] = silhouette.SecuredAction.async(parse.json) { request =>
    Future.successful(
      request.body.validate[(String, Long)]
        .map { case (name, age) => Ok(Json.obj("data" -> name)) }
        .recoverTotal { e => BadRequest("error:" + JsError.toJson(e))}
    )
  }

  @ApiOperation(value = "", hidden = true)
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  @ApiOperation(value = "", hidden = true)
  def redirectDocs = Action { implicit request =>
    Redirect(
      url = "/assets/lib/swagger-ui/index.html",
      queryString = Map("url" -> Seq("http://" + request.host + "/swagger.json")))
  }
}