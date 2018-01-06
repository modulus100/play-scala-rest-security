package models.service

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.User
import models.mongo.UserDAO

import scala.concurrent.Future


/** Handles actions to users.
  *
  *  @param userDAO The user DAO implementation.
  */
class UserServiceImpl @Inject() (userDAO: UserDAO) extends UserService {

  /** Retrieves a user that matches the specified login info.
    *
    *  @param loginInfo The login info to retrieve a user.
    *  @return The retrieved user or None if no user could be retrieved for the given login info.
    */
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDAO.find(loginInfo)

  /** Retrieves a user that matches the specified login info.
    *
    *  @param loginInfo The login info to retrieve a user.
    *  @return The retrieved user or None if no user could be retrieved for the given login info.
    */
  def find: Future[Seq[User]] = userDAO.find

  /** Saves a user.
    *
    *  @param user The user to save.
    *  @return The saved user.
    */
  def save(user: User): Future[User] = userDAO.save(user)

}