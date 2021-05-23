package model

import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.ast.BaseTypedType
import slick.jdbc.JdbcType
import slick.lifted.ProvenShape

import java.sql.Timestamp
import javax.inject.{ Inject, Provider }
import scala.concurrent.{ ExecutionContext, Future }

class FamilyRecipeInstancesDAO @Inject() (
    protected override val dbConfigProvider: DatabaseConfigProvider,
    usersDAOProvider: Provider[UsersDAO],
    implicit val executionContext: ExecutionContext
) extends HasDatabaseConfigProvider[PostgresProfile] {

  import PostgresProfile.api._

  class FamilyRecipeInstancesSchema(tag: Tag) extends Table[FamilyRecipeInstance](tag, "family_recipe_instances") {
    implicit val javaUtilDateMapper: JdbcType[java.util.Date] with BaseTypedType[java.util.Date] = MappedColumnType
      .base[java.util.Date, java.sql.Timestamp](d => new Timestamp(d.getTime), d => new java.util.Date(d.getTime))

    def name: Rep[String] = column[String]("name")
    def description: Rep[String] = column[String]("description")
    def adminId: Rep[Option[Int]] = column[Option[Int]]("admin_id")
    def created: Rep[java.util.Date] = column[java.util.Date]("created")
    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def * : ProvenShape[FamilyRecipeInstance] =
      (name, description, adminId, created, id) <> (FamilyRecipeInstance.tupled, FamilyRecipeInstance.unapply)
  }

  val familyRecipeInstances = TableQuery[FamilyRecipeInstancesSchema]
  private lazy val usersDAO = usersDAOProvider.get()

  def find(id: Int): Future[Option[FamilyRecipeInstance]] = db.run(findQuery(id))

  def find(name: String): Future[Option[FamilyRecipeInstance]] = db.run(findQuery(name))

  def insert(familyRecipeInstance: FamilyRecipeInstance, adminUser: User): Future[FamilyRecipeInstance] = {
    val actions =
      (for {
        r1 <- insertQuery(familyRecipeInstance)
        r2 <- usersDAO.insertQuery(adminUser.copy(familyRecipeInstanceId = r1.id))
        _ <- updateQuery(r1.copy(adminId = Some(r2.id)))
        r3 <- fetchQuery(r1.id)
      } yield (r3)).transactionally

    db.run(actions)
  }

  def list(): Future[List[FamilyRecipeInstance]] =
    db.run(listQuery())

  private[model] def listQuery(): DBIO[List[FamilyRecipeInstance]] = {
    familyRecipeInstances
      .joinLeft(usersDAO.users).on(_.adminId === _.id)
      .result.map(_.map {
        case (familyRecipeInstance, user) =>
          familyRecipeInstance.adminUser = user
          familyRecipeInstance
      }).map(_.toList)
  }

  private[model] def insertQuery(familyRecipeInstance: FamilyRecipeInstance): DBIO[FamilyRecipeInstance] =
    (familyRecipeInstances returning familyRecipeInstances) += familyRecipeInstance

  private[model] def updateQuery(familyRecipeInstance: FamilyRecipeInstance): DBIO[FamilyRecipeInstance] = {
    familyRecipeInstances.filter(_.id === familyRecipeInstance.id).update(familyRecipeInstance).flatMap {
      case 1 =>
        fetchQuery(familyRecipeInstance.id)
      case n =>
        DBIO.failed(new RuntimeException(s"Failed to update instance ${familyRecipeInstance.id}: updated $n rows"))
    }
  }

  private[model] def findQuery(id: Int): DBIO[Option[FamilyRecipeInstance]] = {
    familyRecipeInstances
      .filter(_.id === id)
      .joinLeft(usersDAO.users).on(_.adminId === _.id)
      .result.headOption.map(_.map {
        case (familyRecipeInstance, user) =>
          familyRecipeInstance.adminUser = user
          familyRecipeInstance
      })
  }

  private[model] def findQuery(name: String): DBIO[Option[FamilyRecipeInstance]] = {
    familyRecipeInstances
      .filter(_.name === name)
      .joinLeft(usersDAO.users).on(_.adminId === _.id)
      .result.headOption.map(_.map {
        case (familyRecipeInstance, user) =>
          familyRecipeInstance.adminUser = user
          familyRecipeInstance
      })
  }

  private[model] def fetchQuery(id: Int): DBIO[FamilyRecipeInstance] = {
    familyRecipeInstances
      .filter(_.id === id)
      .joinLeft(usersDAO.users).on(_.adminId === _.id)
      .result.head.map {
        case (familyRecipeInstance, user) =>
          familyRecipeInstance.adminUser = user
          familyRecipeInstance
      }
  }
}
