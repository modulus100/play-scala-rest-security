package models.mongo

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.User

import scala.concurrent.Future


/** Gives access to [[User]] objects.
  */
trait UserDAO {

  /** Finds a user by its login info.
    *
    *  @param loginInfo The login info of the user to find.
    *  @return The found user or None if no user for the given login info could be found.
    */
  def find(loginInfo: LoginInfo): Future[Option[User]]

  /** Finds a user by its user ID.
    *
    *  @param userID The ID of the user to find.
    *  @return The found user or None if no user for the given ID could be found.
    */
  def find(userID: UUID): Future[Option[User]]

  /** Finds all users.
    *
    *  @return All active users.
    */
  def find: Future[List[User]]

  /** Saves a user.
    *
    *  @param user The user to save.
    *  @return The saved user.
    */
  def save(user: User): Future[User]
}
