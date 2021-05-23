package model

import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import play.api.libs.json.JsValue
import slick.ast.BaseTypedType
import slick.jdbc.JdbcType
import slick.lifted.ProvenShape

import java.sql.Timestamp
import javax.inject.{ Inject, Provider }
import scala.concurrent.ExecutionContext
import scala.language.existentials

class UserSessionsDAO @Inject() (
    protected override val dbConfigProvider: DatabaseConfigProvider,
    usersDAO: Provider[UsersDAO],
    implicit val executionContext: ExecutionContext
) extends HasDatabaseConfigProvider[PostgresProfile] {

  import PostgresProfile.api._

  class UserSessionsSchema(tag: Tag) extends Table[UserSession](tag, "user_sessions") {
    implicit val javaUtilDateMapper: JdbcType[java.util.Date] with BaseTypedType[java.util.Date] = MappedColumnType
      .base[java.util.Date, java.sql.Timestamp](d => new Timestamp(d.getTime), d => new java.util.Date(d.getTime))

    def token: Rep[String] = column[String]("token")
    def expiry: Rep[java.util.Date] = column[java.util.Date]("expiry")
    def userId: Rep[Option[Int]] = column[Option[Int]]("user_id")
    def created: Rep[java.util.Date] = column[java.util.Date]("created")
    def sessionKey: Rep[Option[String]] = column[Option[String]]("session_key")
    def sessionData: Rep[Option[JsValue]] = column[Option[JsValue]]("session_data")
    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def * : ProvenShape[UserSession] =
      (token, expiry, userId, created, sessionKey, sessionData, id) <> (UserSession.tupled, UserSession.unapply)
  }

  val userSessions: TableQuery[UserSessionsSchema] = TableQuery[UserSessionsSchema]

  private lazy val users = usersDAO.get().users

  def insert(userSession: UserSession): DBIO[UserSession] =
    (userSessions returning userSessions) += userSession

  def findBySessionToken(secureSessionToken: String): DBIO[Option[UserSession]] = {
    userSessions
      .filter(_.token === secureSessionToken)
      .joinLeft(users).on(_.userId === _.id)
      .result.headOption.map(_.map {
        case ((userSession, user)) =>
          userSession.user = user
          userSession
      })
  }

  def delete(id: Int): DBIO[Unit] = {
    userSessions.filter(_.id === id).delete.flatMap {
      case 1 =>
        DBIO.successful(())
      case 0 =>
        DBIO.failed(throw new RuntimeException(s"Failed to find user session $id"))
      case _ =>
        DBIO.failed(throw new RuntimeException(s"Deleted multiple rows for user session $id"))
    }
  }
}
