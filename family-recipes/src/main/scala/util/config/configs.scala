package util.config

case class DatabaseConfig(database: String, portNumber: Int = 5432, user: Option[String] = None, password: Option[String] = None)

case class BasicAuthConfig(user: String, password: String, realm: String)

case class WebResourceConfig(url: String, integrity: Option[String], crossorigin: Option[String] = None)
