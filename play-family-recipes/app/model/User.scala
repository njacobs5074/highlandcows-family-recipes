package model

import java.util.Date

case class User(username: String, password: String, familyRecipeInstanceId: Int = 0, created: Date = new Date(), id: Int = 0) {

  @SuppressWarnings(Array(
    "scalafix:DisableSyntax.var"
  ))
  var userSession: Option[UserSession] = None

  @SuppressWarnings(Array(
    "scalafix:DisableSyntax.var"
  ))
  var familyRecipeInstance: Option[FamilyRecipeInstance] = None

  def isAdmin: Boolean = familyRecipeInstance.exists(_.adminId.contains(id))
}
