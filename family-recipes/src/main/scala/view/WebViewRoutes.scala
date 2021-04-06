package view

import cask.model.Response
import cask.router.Decorator
import net.ceedubs.ficus.Ficus._
import org.slf4j.{ Logger, LoggerFactory }

trait WebViewRoutes extends cask.Routes {
  override def decorators: Seq[Decorator[_, _, _]] = Seq(new trapToErrorPage())

  lazy val logger: Logger = LoggerFactory.getLogger(getClass)

  lazy val sessionCookie: String = app.config.as[String]("webSession.cookieName")

  def redirectWithSession(userSession: model.UserSession, page: String): Response[String] = {
    userSession.sessionKey match {
      case Some(sessionKey) =>
        cask.Response(
          "",
          301,
          headers = Seq("Location" -> page),
          cookies = Seq(
            cask.Cookie(sessionCookie, sessionKey, expires = userSession.expiry.toInstant, httpOnly = true)
          ) // need to set secure if only https
        )
      case None =>
        throw api.ApiError(500, Some(s"User session for user ${userSession.user.get.username} not correctly created"))
    }
  }
}
