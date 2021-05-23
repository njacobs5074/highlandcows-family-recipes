package model

import com.github.tminglei.slickpg._
import slick.basic.Capability
import slick.jdbc.JdbcCapabilities

trait PostgresProfile extends ExPostgresProfile with PgPlayJsonSupport {
  def pgjson = "jsonb"

  override protected def computeCapabilities: Set[Capability] = super.computeCapabilities + JdbcCapabilities.insertOrUpdate

  override val api: API with JsonImplicits = PostgresJsonSupportAPI

  object PostgresJsonSupportAPI extends API with JsonImplicits

}

object PostgresProfile extends PostgresProfile
