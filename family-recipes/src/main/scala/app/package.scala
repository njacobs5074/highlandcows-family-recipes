import com.typesafe.config.{ Config, ConfigFactory }

package object app {

  /** Standard configuration for the entire application */
  lazy val config: Config = ConfigFactory.load()

}
