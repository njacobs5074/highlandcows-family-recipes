package model

import java.util.Date

@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
case class User(username: String, password: String, familyRecipeInstanceId: Int, created: Date, id: Int = 0) {
  var userSession: Option[UserSession] = None
  var familyRecipeInstance: Option[FamilyRecipeInstance] = None
}

@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
case class UserSession(
    token: String,
    userId: Int,
    expiry: Date,
    created: Date,
    sessionKey: Option[String] = None,
    id: Int = 0
) {
  var user: Option[User] = None

  def isExpired(): Boolean = new Date().after(expiry)
}

object UserSession {
  def generateSessionKey(token: String, userId: Int): String = util.secureHash(s"$token|$userId")
}

@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
case class FamilyRecipeInstance(name: String, description: String, adminId: Int, created: Date, id: Int = 0) {
  var adminUser: Option[User] = None
}
