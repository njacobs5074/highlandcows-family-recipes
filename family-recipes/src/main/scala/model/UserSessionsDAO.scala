package model

import play.api.libs.json.JsValue

class UserSessionsDAO(database: Database) {
  import database.ctx._
  import database._

  def insert(userSession: UserSession): UserSession = {
    val id = database.ctx.run(database.schema.userSessions.insert(lift(userSession)).returningGenerated(_.id))
    database.ctx.run(database.schema.userSessions.filter(_.id == lift(id))).head
  }

  def find(id: Int): Option[UserSession] = {

    val action = quote(for {
      userSession <- database.schema.userSessions.filter(_.id == lift(id))
      user <- database.schema.users.leftJoin(users => userSession.userId.contains(users.id))
    } yield (userSession, user))

    database.ctx.run(action).headOption.map { case (userSession, user) =>
      userSession.user = user
      userSession
    }
  }

  def findBySessionToken(secureSessionToken: String): Option[UserSession] = {
    val action = quote(for {
      userSession <- database.schema.userSessions.filter(_.sessionKey.exists(_ == lift(secureSessionToken)))
      user <- database.schema.users.leftJoin(users => userSession.userId.contains(users.id))
    } yield (userSession, user))

    database.ctx.run(action).headOption.map { case (userSession, user) =>
      userSession.user = user
      userSession
    }
  }

  def delete(id: Int): Unit = database.ctx.run(database.schema.userSessions.filter(_.id == lift(id)).delete)

  def updateMetaData(id: Int, metaData: JsValue): Unit = {
    val action = quote {
      query[UserSession].filter(us => us.id == lift(id)).update(us => us.metaData -> Some(lift(metaData)))
    }
    val numUpdated = database.ctx.run(action)
    if (numUpdated != 1) {
      throw api.ApiError(406, Some(s"Update failed for user session $id: numUpdated = $numUpdated"))
    }
  }

}
