package app

import cask.main.Routes
import cask.model.Response.Raw
import cask.model.{ Request, Response }
import cask.router.Result
import model.Database
import org.slf4j.{ Logger, LoggerFactory }
import scalatags.Text.all._
import view._

import scala.util.{ Failure, Success, Try }

class Controller(userService: service.UserService, userSessionService: service.UserSessionService)
    extends cask.Routes {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  val sessionName = "user-session"

  /**
    * Decorator that makes our user session cookie available to endpoints if they want it.
    * Will be available as an addtional argument, `session`
    */
  class LoggedIn extends cask.RawDecorator {
    override def wrapFunction(request: Request, delegate: Delegate): Result[Raw] = {
      val session: Option[cask.Cookie] = request.cookies
        .find { cookie => cookie._1 == sessionName }
        .map(_._2)
      delegate(Map("session" -> session))
    }
  }

  override val decorators: Seq[cask.RawDecorator] = Seq(new LoggedIn())

  @cask.staticResources("/static")
  def staticResources(): String = "static"

  @cask.get("/")
  def index()(session: Option[cask.Cookie]): doctype = {

    session match {
      case Some(_) =>
        main()(session)

      case _ =>
        doctype("html")(
          html(
            bootstrap.head(titleText = "Login", additionalStyleSheets = Seq(loginFormCssFile)),
            view.loginForm()
          )
        )
    }
  }

  @cask.postForm("/")
  def post(inputEmail: String, inputPassword: String): Response[String] = {
    def setUserSession(userSession: model.UserSession) = {
      userSession.sessionKey match {
        case Some(sessionKey) =>
          cask.Response(
            "",
            301,
            headers = Seq("Location" -> "/main"),
            cookies = Seq(
              cask.Cookie(sessionName, sessionKey, httpOnly = true)
            ) // need to set secure but only if https
          )
        case None =>
          logger.warn(
            s"User session for $inputEmail was not correctly created -- redirecting to home page"
          )
          cask.Response(
            "",
            301,
            headers = Seq("Location" -> "/"),
            cookies =
              Seq(cask.Cookie(sessionName, "", expires = java.time.Instant.EPOCH, httpOnly = true))
          )

      }
    }

    Try(userService.authenticate(inputEmail, inputPassword)) match {
      case Success(user) if user.userSession.isDefined =>
        if (user.userSession.get.isExpired()) {
          setUserSession(userSessionService.createNewSession(inputEmail))
        } else {
          setUserSession(user.userSession.get)
        }

      case Success(user) =>
        Try(userSessionService.createNewSession(inputEmail)) match {
          case Success(userSession) =>
            setUserSession(userSession)

          case Failure(t) =>
            logger.warn(s"Failed to create new session for $inputEmail", t)
            cask.Abort(500)
        }

      case Failure(apiError: util.ApiError) if apiError.statusCode == 403 =>
        cask.Redirect("/")

      case Failure(t) =>
        logger.error("Unexpected error on login", t)
        cask.Abort(500)

    }
  }

  @cask.post("/logout")
  def logout(): Response[String] = {
    cask.Response(
      "",
      301,
      headers = Seq("Location" -> "/"),
      cookies =
        Seq(cask.Cookie(sessionName, "", expires = java.time.Instant.EPOCH, httpOnly = true))
    )
  }

  @cask.get("/main")
  def main()(session: Option[cask.Cookie]): doctype = {
    session match {
      case Some(cookie) =>
        doctype("html")(
          html(
            bootstrap.head(titleText = "Logged In", additionalStyleSheets = Seq(loginFormCssFile)),
            view.main(s"${cookie.name}=${cookie.value}")
          )
        )
      case None =>
        index()(None)
    }
  }

  initialize()
}

object LoginApp extends cask.Main {

  val logger: Logger = LoggerFactory.getLogger("main")

  override def allRoutes: Seq[Routes] =
    Try(Database()) match {
      case Success(database) =>
        sys.addShutdownHook(database.close())
        Seq(
          new Controller(
            new service.UserService(database),
            new service.UserSessionService(database)
          )
        )

      case Failure(t) =>
        throw t

    }
}
