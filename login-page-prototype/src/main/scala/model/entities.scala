package model

import java.util.Date

@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
case class User(username: String, password: String, created: Date, id: Int = 0) {
  var userSession: Option[UserSession] = None
}

@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
case class UserSession(token: String, userId: Int, expiry: Date, created: Date, id: Int = 0) {
  var user: Option[User] = None

  lazy val encoded: String = util.secureHash(s"$token|$userId")
}
