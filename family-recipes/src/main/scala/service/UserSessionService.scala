package service

import model.{ Database, UserSession }
import org.apache.commons.lang3.time.DateUtils

import java.util.{ Date, UUID }

class UserSessionService(database: Database) {

  def createNewSession(username: String): model.UserSession = {

    def newSession(userId: Int): model.UserSession = {
      val now = new Date()
      val expiry = DateUtils.addDays(now, 1)
      val token = UUID.randomUUID().toString
      val sessionKey = UserSession.generateSessionKey(token, userId)
      database.UserSessions().insert(UserSession(token, userId, expiry, now, sessionKey = Some(sessionKey)))
    }

    database.Users().find(username) match {
      case Some(user) if user.userSession.isDefined =>
        val existingSession = user.userSession.get
        database.UserSessions().delete(existingSession.id)
        newSession(user.id)

      case Some(user) => newSession(user.id)

      case None => throw api.ApiError(404)
    }
  }
}
