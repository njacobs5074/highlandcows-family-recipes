package controller

import com.typesafe.config.Config
import play.api.http.HeaderNames
import play.api.mvc.{ ActionBuilderImpl, _ }
import play.api.{ ConfigLoader, Configuration, Logging }
import util._

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

case class BasicAuthConfig(username: String, password: String) {
  def isAuthorized(username: String, password: String) = this.username == username && this.password == password
}

object BasicAuthConfig {
  implicit val configLoader: ConfigLoader[BasicAuthConfig] = (rootConfig: Config, path: String) => {
    val config = rootConfig.getConfig(path)
    new BasicAuthConfig(
      config.getString("username"),
      config.getString("password")
    )
  }
}

/**
 *  ActionBuilder that implements a basic auth check.
 *  Looks for authorization parameters from `application.conf` as follows:
 *
 *  ```
 *  app.api.auth.username = "foo"
 *  app.api.auth.password = "wizbang"
 *  ```
 *
 *  @param parser
 *  @param configuration
 *  @param ec
 */
class BasicAuthAction @Inject() (
    parser: BodyParsers.Default,
    configuration: Configuration
)(implicit ec: ExecutionContext)
    extends ActionBuilderImpl(parser)
    with Logging {

  private lazy val authConfig = configuration.get[BasicAuthConfig]("app.api.auth")
  private lazy val basicAuthTokenizer = "(Basic )(.*)".r

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    request.getAuthorization.map { authHeader =>
      val basicAuthTokenizer(_, capture) = authHeader

      capture.decodeBase64().split(":") match {
        case Array(username, password) if authConfig.isAuthorized(username, password) =>
          block(request)
        case _ =>
          Future.successful(
            AuthenticationError().asStatus.withHeaders(HeaderNames.WWW_AUTHENTICATE -> "Basic realm=family-recipes")
          )
      }

    }.getOrElse(Future.successful(
      AuthenticationError().asStatus.withHeaders(HeaderNames.WWW_AUTHENTICATE -> "Basic realm=family-recipes")
    ))

  }
}
