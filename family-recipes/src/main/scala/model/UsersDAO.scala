package model

class UsersDAO(database: Database) {

  import database.ctx._
  import database._

  /** Insert the specified user into the database */
  def insert(user: User): User =
    database.ctx.transaction {
      val id = database.ctx.run(database.schema.users.insert(lift(user)).returningGenerated(_.id))
      database.ctx.run(database.schema.users.filter(_.id == lift(id))).head
    }

  /** Retrieve the specified user and its session from the database */
  def find(id: Int): Option[User] = {
    val action = quote(for {
      user <- database.schema.users.filter(_.id == lift(id))
      userSession <- database.schema.userSessions.leftJoin(userSession => userSession.userId.contains(user.id))
      familyRecipeInstance <- database.schema.familyRecipeInstances.leftJoin(familyRecipeInstance =>
        familyRecipeInstance.id == user.familyRecipeInstanceId
      )
    } yield (user, userSession, familyRecipeInstance))

    database.ctx.run(action).headOption.map { case (user, maybeUserSession, maybeFamilyRecipeInstance) =>
      user.familyRecipeInstance = maybeFamilyRecipeInstance
      user.userSession = maybeUserSession
      user
    }
  }

  def find(username: String): Option[User] = {
    val action = quote(for {
      user <- database.schema.users.filter(_.username == lift(username))
      userSession <- database.schema.userSessions.leftJoin(userSession => userSession.userId.contains(user.id))
      familyRecipeInstance <-
        database.schema.familyRecipeInstances.join(familyRecipeInstance => familyRecipeInstance.id == user.familyRecipeInstanceId)
    } yield (user, userSession, familyRecipeInstance))

    database.ctx.run(action).headOption.map { case (user, maybeUserSession, familyRecipeInstance) =>
      user.familyRecipeInstance = Some(familyRecipeInstance)
      user.userSession = maybeUserSession
      user
    }
  }

  /** Update an existing user */
  def update(user: User): User = {
    val action = quote {
      database.schema.users.filter(_.id == lift(user.id)).update(lift(user))
    }
    database.ctx.transaction {
      database.ctx.run(action) match {
        case 1 =>
          database.ctx.run(database.schema.users.filter(_.id == lift(user.id))).head
        case 0 | _ =>
          throw new RuntimeException(s"Failed to update $user")
      }
    }
  }

  /** Delete an existing user */
  def delete(user: User): Unit = {
    val action = quote {
      database.schema.users.filter(_.id == lift(user.id)).delete
    }
    database.ctx.run(action)
  }

  /** Insert a user or update if it already exists */
  def upsert(user: User): User =
    database.ctx.transaction {
      find(user.id) match {
        case Some(user) =>
          update(user)
        case None =>
          insert(user)
      }
    }
}
