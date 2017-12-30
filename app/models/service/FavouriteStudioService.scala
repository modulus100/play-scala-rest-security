package models.service

import javax.inject.Inject

import models.FavouriteStudio
import models.mysql.FavouriteStudioDAO
import play.api.libs.functional.syntax._
import play.api.libs.json._


@javax.inject.Singleton
class FavouriteStudioService @Inject() (favouriteStudioDAO: FavouriteStudioDAO) {

  implicit val favouriteStudioWrites: Writes[FavouriteStudio] = (
    (JsPath \ "userId").write[Int] and
    (JsPath \ "studioId").write[Int])(unlift(FavouriteStudio.unapply))

  def addFavourite(userId: Int, studioId: Int): FavouriteStudio = {
    val favourite = FavouriteStudio(userId, studioId)
    favouriteStudioDAO.create(favourite)
    favourite
  }

  def delete(userId: Int, studioId: Int): Int =
    favouriteStudioDAO.delete(FavouriteStudio(userId, studioId))

  def findAllByUser(userId: Int): List[FavouriteStudio] =
    favouriteStudioDAO.index(userId)

  def find(userId: Int, studioId: Int): Option[FavouriteStudio] = {
    val favourite = FavouriteStudio(userId, studioId)
    Some(favourite).filter(favouriteStudioDAO.exists)
  }

  def getAllUsers: List[FavouriteStudio] =
    favouriteStudioDAO.getAllUsers
}

