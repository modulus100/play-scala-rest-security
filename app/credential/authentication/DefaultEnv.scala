package credential.authentication

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.User


trait DefaultEnv extends Env {

  /** Identity
    */
  type I = User

  /** Authenticator used for identification.
    *   could've also been used for REST.
    */
  type A = JWTAuthenticator

}
