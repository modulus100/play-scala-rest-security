package controllers

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration
import play.api.Configuration
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import net.ceedubs.ficus.Ficus.{finiteDurationReader, optionValueReader, toFicusConfig}
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.Authenticator.Implicits.RichDateTime
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials, PasswordHasher}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.User
import credential.authentication.{DefaultEnv, SignInData, SignUpData}
import models.service.UserService


class CredentialController @Inject()(implicit ec: ExecutionContext,
                                     messagesApi: MessagesApi,
                                     silhouette: Silhouette[DefaultEnv],
                                     cc: ControllerComponents,
                                     userService: UserService,
                                     credentialsProvider: CredentialsProvider,
                                     configuration: Configuration,
                                     passwordHasher: PasswordHasher,
                                     authInfoRepository: AuthInfoRepository,
                                     clock: Clock) extends AbstractController(cc) with I18nSupport {

  def signIn: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[SignInData].fold(
      errors => {
        Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"), "errors" -> JsError.toJson(errors))))
      },
      data => {
        credentialsProvider.authenticate(Credentials(data.email, data.password)).flatMap { loginInfo =>
          userService.retrieve(loginInfo).flatMap {
            case Some(user) => silhouette.env.authenticatorService.create(loginInfo).map {
              case authenticator if data.rememberMe =>
                val c = configuration.underlying
                authenticator.copy(
                  expirationDateTime = clock.now + c.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry"),
                  idleTimeout = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout")
                )
              case authenticator => authenticator
            }.flatMap { authenticator =>
              silhouette.env.eventBus.publish(LoginEvent(user, request))
              silhouette.env.authenticatorService.init(authenticator).map { token =>
                Ok(Json.obj("token" -> token))
              }
            }
            case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
          }
        }.recover {
          case e: ProviderException =>
            Unauthorized(Json.obj("message" -> Messages("invalid.credentials")))
        }
      })
  }

  def signUp: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[SignUpData].fold(
      errors => {
        Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"), "errors" -> JsError.toJson(errors))))
      },
      data => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) => Future.successful(BadRequest(Json.obj("message" -> Messages("user.exists"))))
          case None =>
            val user = User(
              userId = UUID.randomUUID(),
              loginInfo = loginInfo,
              firstName = Some(data.firstName),
              lastName = Some(data.lastName),
              fullName = Some(data.firstName + " " + data.lastName),
              email = Some(data.email),
              passwordInfo = None)

            val authInfo = passwordHasher.hash(data.password)
            for {
              user <- userService.save(user)
              authInfo <- authInfoRepository.add(loginInfo, authInfo)
              authenticator <- silhouette.env.authenticatorService.create(loginInfo)
              token <- silhouette.env.authenticatorService.init(authenticator)
            } yield {
              silhouette.env.eventBus.publish(SignUpEvent(user, request))
              silhouette.env.eventBus.publish(LoginEvent(user, request))
              Ok(Json.obj("token" -> token))
            }
        }
      }
    )
  }

  def signOut: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, Ok)
  }
}