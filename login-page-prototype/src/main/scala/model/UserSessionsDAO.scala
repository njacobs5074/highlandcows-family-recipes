package model

class UserSessionsDAO(database: Database) {
  import database.ctx._

  def insert(userSession: UserSession): UserSession = {
    val id = database.ctx.run(database.schema.userSessions.insert(lift(userSession)).returningGenerated(_.id))
    database.ctx.run(database.schema.userSessions.filter(_.id == lift(id))).head
  }

  def find(id: Int): Option[UserSession] = {

    val action = quote(for {
      userSession <- database.schema.userSessions.filter(_.id == lift(id))
      user <- database.schema.users.join(user => user.id == userSession.userId)
    } yield (userSession, user))

    database.ctx.run(action).headOption.map {
      case (userSession, user) =>
        userSession.user = Some(user)
        userSession
    }
  }

  def delete(id: Int): Unit = {
    database.ctx.run(database.schema.userSessions.filter(_.id == lift(id)).delete)
  }

}
