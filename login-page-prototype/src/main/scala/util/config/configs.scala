package util.config

case class DatabaseConfig(
    database: String,
    portNumber: Int = 5432,
    user: Option[String] = None,
    password: Option[String] = None
)
