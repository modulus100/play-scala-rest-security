package models

import play.api.libs.json.{Json, OFormat}


case class FavouriteStudio(userId: Int, studioId: Int)

object FavouriteStudio {

  implicit val jsonFormat: OFormat[FavouriteStudio] = Json.format[FavouriteStudio]
}
