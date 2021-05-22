package model

import play.api.libs.json.JsValue
import util._

import java.util.Date

case class UserSession(
    token: String,
    expiry: Date,
    userId: Option[Int] = None,
    created: Date = new Date(),
    sessionKey: Option[String] = None,
    sessionData: Option[JsValue] = None,
    id: Int = 0
) {
  @SuppressWarnings(Array(
    "scalafix:DisableSyntax.var"
  ))
  var user: Option[User] = None

  def isExpired: Boolean = new Date().after(expiry)
  val isAuthenticated: Boolean = userId.isDefined
  def isValid: Boolean = isAuthenticated && !isExpired

  def generateSessionKey(secretKey: String): String = s"$token|$userId".secureHash(secretKey)
}
