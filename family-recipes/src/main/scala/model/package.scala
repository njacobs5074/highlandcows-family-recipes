import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }
import io.getquill.{ PostgresJdbcContext, SnakeCase }
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import org.postgresql.ds.PGSimpleDataSource
import util.config

package object model {

  class Database(databaseConfig: config.DatabaseConfig) {
    private val pgDataSource = new PGSimpleDataSource()
    pgDataSource.setDatabaseName(databaseConfig.database)
    pgDataSource.setPortNumbers(Array(databaseConfig.portNumber))
    databaseConfig.user.foreach(pgDataSource.setUser)
    databaseConfig.password.foreach(pgDataSource.setPassword)

    private val hikariConfig = new HikariConfig()
    hikariConfig.setDataSource(pgDataSource)

    private val dataSource = new HikariDataSource(hikariConfig)
    def close(): Unit = dataSource.close()

    implicit val ctx: PostgresJdbcContext[SnakeCase] = new PostgresJdbcContext(SnakeCase, dataSource)

    // This object provides the mapping between our entity Scala classes and the actual database schema
    // If the schema is changed, this mapping needs to be updated.
    object schema {
      import ctx._

      // We need to be careful we don't widen the type of these schema objects as this will cause
      // us to use dynamic queries and we want Quill to generate compile-time code.

      val users = quote {
        querySchema[User](
          "users",
          _.username -> "username",
          _.password -> "password",
          _.familyRecipeInstanceId -> "family_recipe_instance_id",
          _.created -> "created",
          _.id -> "id"
        )
      }

      val userSessions = quote {
        querySchema[UserSession](
          "user_sessions",
          _.token -> "token",
          _.userId -> "user_id",
          _.sessionKey -> "session_key",
          _.expiry -> "expiry",
          _.created -> "created",
          _.id -> "id"
        )
      }

      val familyRecipeInstances = quote {
        querySchema[FamilyRecipeInstance](
          "family_recipe_instances",
          _.name -> "name",
          _.description -> "description",
          _.adminId -> "admin_id",
          _.created -> "created",
          _.id -> "id"
        )
      }

    }

    def Users(): UsersDAO = new UsersDAO(this)
    def UserSessions(): UserSessionsDAO = new UserSessionsDAO(this)
    def FamilyRecipeInstances(): FamilyRecipeInstancesDAO = new FamilyRecipeInstancesDAO(this)

  }

  object Database {

    // Instantiate the default database -- see 'database.default' in application.conf
    val mainDatabase: Database = {
      val defaultConfig: String = app.config.getString("database.defaultConfig")
      val databaseConfig: config.DatabaseConfig = app.config.as[config.DatabaseConfig](s"database.$defaultConfig")
      new Database(databaseConfig)
    }
  }

}
