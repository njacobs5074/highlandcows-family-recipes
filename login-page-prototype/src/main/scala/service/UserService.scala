package service

import model.{ Database, UserSession }
import org.apache.commons.lang3.time.DateUtils

import java.util.Date

class UserService(database: Database) {

  def authenticate(username: String, password: String): model.User = {

    database.Users.find(username) match {
      case Some(user) if util.secureHash(password) == user.password =>
        user
      case _ =>
        throw util.ApiError(403)

    }
  }

  def createNewSession(username: String): model.UserSession = {

    def newSession(id: Int): model.UserSession = {
      val now = new Date()
      val expiry = DateUtils.addDays(now, 30)
      database.UserSessions().insert(UserSession(util.newSessionKey(), id, expiry, now))
    }

    database.Users.find(username) match {
      case Some(user) if user.userSession.isDefined =>
        val existingSession = user.userSession.get
        database.UserSessions().delete(existingSession.id)
        newSession(user.id)

      case Some(user) =>
        newSession(user.id)

      case None =>
        throw util.ApiError(404)
    }
  }

}
