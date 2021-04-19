package model

import play.api.libs.json.JsValue

import java.util.Date

@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
case class User(username: String, password: String, familyRecipeInstanceId: Int, created: Date = new Date(), id: Int = 0) {
  var userSession: Option[UserSession] = None
  var familyRecipeInstance: Option[FamilyRecipeInstance] = None

  def isAdmin: Boolean = familyRecipeInstance.exists(_.adminUser.exists(_.id == familyRecipeInstanceId))
}

@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
case class UserSession(
    token: String,
    expiry: Date,
    userId: Option[Int] = None,
    created: Date = new Date(),
    sessionKey: Option[String] = None,
    metaData: Option[JsValue] = None,
    id: Int = 0
) {
  var user: Option[User] = None

  def isExpired: Boolean = new Date().after(expiry)
  val isAuthenticated: Boolean = userId.isDefined
  def isValid: Boolean = isAuthenticated && !isExpired
}

object UserSession {
  def generateSessionKey(token: String, userId: Int): String = util.secureHash(s"$token|$userId")
}

@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
case class FamilyRecipeInstance(
    name: String,
    description: String,
    adminId: Option[Int],
    created: Date = new Date(),
    id: Int = 0
) {
  var adminUser: Option[User] = None
}
