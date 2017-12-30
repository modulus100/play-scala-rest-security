package models.mysql

import javax.inject.Inject

import anorm.SqlParser._
import anorm.{Macro, RowParser, _}
import models.FavouriteStudio
import play.api.db.DBApi


@javax.inject.Singleton
class FavouriteStudioDAO @Inject() (dbapi: DBApi) {

  private val db = dbapi.database("default")

  val parser: RowParser[FavouriteStudio] = Macro.namedParser[FavouriteStudio]

  def create(favourite: FavouriteStudio) = {
    val query =
      """
        INSERT IGNORE INTO favouriteStudio (userId, studioId)
        VALUES ({userId}, {studioId});
      """
    db.withConnection { implicit c =>
      SQL(query)
        .on("userId" -> favourite.userId, "studioId" -> favourite.studioId)
        .executeInsert()
    }
  }

  def delete(favourite: FavouriteStudio) = {
    val query =
      """
        DELETE FROM favouriteStudio
        WHERE userId={userId} AND studioId={studioId}
        LIMIT 1;
      """
    db.withConnection { implicit c =>
      SQL(query)
        .on("userId" -> favourite.userId, "studioId" -> favourite.studioId)
        .executeUpdate()
    }
  }

  def exists(favourite: FavouriteStudio): Boolean = {
    val query =
      """
        SELECT COUNT(*) as numMatches
        FROM favouriteStudio
        WHERE userId={userId} AND studioId={studioId};
      """
    val result = db.withConnection { implicit c =>
      SQL(query)
        .on("userId" -> favourite.userId, "studioId" -> favourite.studioId)
        .as(scalar[Long].single)
        .asInstanceOf[Long]
    }
    result != 0
  }

  def index(userId: Int): List[FavouriteStudio] = {
    val query =
      """
        SELECT *
        FROM favouriteStudio
        WHERE userId={userId};
      """
    db.withConnection { implicit c =>
      SQL(query).on("userId" -> userId).as(parser.*)
    }
  }

  def getAllUsers: List[FavouriteStudio] = {
    val query =
      """
        SELECT userId, studioId
        FROM favouriteStudio;
      """
    db.withConnection { implicit c =>
      SQL(query).on().as(parser.*)
    }
  }
}
