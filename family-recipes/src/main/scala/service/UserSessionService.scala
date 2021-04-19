package service

import model.{ Database, UserSession }
import org.apache.commons.lang3.time.DateUtils

import java.util.{ Date, UUID }

class UserSessionService(database: Database) {

  def deleteSession(username: String): Unit = {
    database.Users().find(username) match {
      case Some(user) if user.userSession.isDefined =>
        database.UserSessions().delete(user.userSession.get.id)
      case _ =>
    }
  }

  def deleteSessionBySessionToken(sessionToken: String): Unit = {
    database
      .UserSessions()
      .findBySessionToken(sessionToken)
      .foreach(userSession => database.UserSessions().delete(userSession.id))
  }

  def findBySessionToken(sessionToken: String): Option[model.UserSession] = {
    database.UserSessions().findBySessionToken(sessionToken)
  }

  /**
   *  Create a user session that is tied to a specific user
   */
  def createNewSession(username: String): model.UserSession = {

    database.Users().find(username) match {
      case Some(user) if user.userSession.isDefined =>
        val existingSession = user.userSession.get
        database.UserSessions().delete(existingSession.id)
        newSession(Some(user.id))

      case Some(user) =>
        newSession(Some(user.id))

      case None =>
        throw api.ApiError(404)
    }
  }

  /**
   *  Create an anonymous session that we can use prior to login
   */
  def createNewSession(): model.UserSession = newSession()

  private def newSession(userId: Option[Int] = None): model.UserSession = {
    val now = new Date()
    val expiry = DateUtils.addDays(now, 1)
    val token = UUID.randomUUID().toString
    val sessionKey = UserSession.generateSessionKey(token, userId.getOrElse(0))
    database.UserSessions().insert(UserSession(token, expiry, userId, now, sessionKey = Some(sessionKey)))
  }
}
