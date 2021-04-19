package service

import model.Database

class UserService(database: Database) {

  def authenticate(username: String, password: String, maybeSessionToken: Option[String]): model.User = {
    database.Users().find(username) match {
      case Some(user) if util.secureHash(password) == user.password =>
        maybeSessionToken.foreach(service.UserSessionService().deleteSessionBySessionToken)
        user.userSession match {
          case Some(userSession) if !userSession.isExpired =>
            user
          case _ =>
            val newSession = UserSessionService().createNewSession(username)
            user.userSession = Some(newSession)
            user
        }
      case _ =>
        throw api.AuthenticationError

    }
  }
}
