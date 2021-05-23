package model

import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.ast.BaseTypedType
import slick.jdbc.JdbcType
import slick.lifted.ProvenShape

import java.sql.Timestamp
import javax.inject.{ Inject, Provider }
import scala.concurrent.{ ExecutionContext, Future }
import scala.language.existentials

class UsersDAO @Inject() (
    protected override val dbConfigProvider: DatabaseConfigProvider,
    userSessionsDAO: Provider[UserSessionsDAO],
    familyRecipeInstancesRepository: Provider[FamilyRecipeInstancesDAO],
    implicit val executionContext: ExecutionContext
) extends HasDatabaseConfigProvider[PostgresProfile] {

  import PostgresProfile.api._

  class UsersSchema(tag: Tag) extends Table[User](tag, "users") {
    implicit val javaUtilDateMapper: JdbcType[java.util.Date] with BaseTypedType[java.util.Date] = MappedColumnType
      .base[java.util.Date, java.sql.Timestamp](d => new Timestamp(d.getTime), d => new java.util.Date(d.getTime))

    def username: Rep[String] = column[String]("username", O.Unique)
    def password: Rep[String] = column[String]("password")
    def familyRecipeInstanceId: Rep[Int] = column[Int]("family_recipe_instance_id")
    def created: Rep[java.util.Date] = column[java.util.Date]("created")
    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def * : ProvenShape[User] = (username, password, familyRecipeInstanceId, created, id) <> (User.tupled, User.unapply)
  }

  private[model] val users = TableQuery[UsersSchema]

  private lazy val userSessions = userSessionsDAO.get().userSessions
  private lazy val familyRecipeInstances = familyRecipeInstancesRepository.get().familyRecipeInstances

  def find(username: String): Future[Option[User]] =
    db.run(findQuery(username))

  def update(user: User): Future[User] =
    db.run(updateQuery(user))

  private[model] def insertQuery(user: User): DBIO[User] =
    (users returning users) += user

  private[model] def updateQuery(user: User): DBIO[User] = {
    users.filter(_.id === user.id).update(user).flatMap {
      case 1 =>
        fetchQuery(user.id)
      case n =>
        DBIO.failed(new RuntimeException(s"Failed to update user ${user.id}: updated $n rows"))
    }
  }

  private[model] def findQuery(id: Int): DBIO[Option[User]] = {
    users
      .filter(_.id === id)
      .joinLeft(userSessions).on(_.id === _.userId)
      .joinLeft(familyRecipeInstances).on(_._1.familyRecipeInstanceId === _.id)
      .result.headOption.map(_.map {
        case ((user, userSession), familyRecipeInstance) =>
          user.userSession = userSession
          user.familyRecipeInstance = familyRecipeInstance
          user
      })
  }

  private[model] def findQuery(username: String): DBIO[Option[User]] = {
    users
      .filter(_.username === username)
      .joinLeft(userSessions).on(_.id === _.userId)
      .joinLeft(familyRecipeInstances).on(_._1.familyRecipeInstanceId === _.id)
      .result.headOption.map(_.map {
        case ((user, userSession), familyRecipeInstance) =>
          user.userSession = userSession
          user.familyRecipeInstance = familyRecipeInstance
          user
      })
  }

  private[model] def fetchQuery(id: Int): DBIO[User] = {
    users
      .filter(_.id === id)
      .joinLeft(userSessions).on(_.id === _.userId)
      .joinLeft(familyRecipeInstances).on(_._1.familyRecipeInstanceId === _.id)
      .result.head.map {
        case ((user, userSession), familyRecipeInstance) =>
          user.userSession = userSession
          user.familyRecipeInstance = familyRecipeInstance
          user
      }
  }
}
