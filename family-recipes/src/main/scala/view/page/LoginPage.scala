package view.page

import scalatags.Text.all._
import util.helpers.caskExt._
import view.tags._
import view.trapToErrorPage

import scala.util.{ Failure, Success, Try }

class LoginPage extends view.WebViewRoutes {

  lazy val fields = collection.mutable.HashMap.empty[String, Field]

  lazy val loginForm: Frag = {
    val emailField = fields.get("email")
    val passwordField = fields.get("password")

    div(
      nav(cls := "navbar navbar-expand-md navbar-light bg-light fixed-top")(
        a(cls := "navbar-brand", href := "#")("Family Recipes"),
        button(
          cls := "navbar-toggler",
          `type` := "button",
          view.tags.dataToggle := "collapse",
          view.tags.dataTarget := "#navbarSupportedContent",
          aria.controls := "navbarSupportedContent",
          aria.label := "Toggle Navigation",
          aria.expanded := "false"
        )(span(cls := "navbar-toggler-icon")),
        div(cls := "collapse navbar-collapse justify-content-md-end", id := "navbarSupportedContent")(
          form(cls := "form-inline my-2 my-lg-0", action := "/login", method := "post")(
            input(
              id := "email",
              name := "email",
              `type` := "text",
              value := emailField.flatMap(_.value).getOrElse(""),
              placeholder := emailField.flatMap(_.placeholder).getOrElse("Email"),
              cls := "form-control mr-sm-2",
              aria.label := "Email",
              required
            ),
            input(
              id := "password",
              name := "password",
              `type` := "password",
              value := passwordField.flatMap(_.value).getOrElse(""),
              placeholder := passwordField.flatMap(_.placeholder).getOrElse("Password"),
              cls := "form-control mr-sm-2",
              aria.label := "Password",
              required
            ),
            button(`type` := "submit", cls := "btn btn-outline-primary my-2 my-sm-0")("Sign in")
          )
        )
      ),
      div(cls := "jumbotron jumbotron-fluid")(
        div(cls := "container d-flex justify-content-center")(
          img(cls := "img-fluid rounded", alt := "Chocolate chip cookies", src := "/view/images/cookies.jpg")
        )
      )
    )
  }

  @view.useWebSession()
  @cask.get("/login")
  def login()(session: Option[cask.Cookie]): cask.Response[String] = {
    lazy val body = page(
      title = "Family Recipes - Login",
      pageContent = List(loginForm),
      stylesheets = List(link(rel := "stylesheet", href := "/view/css/login.css"))
    )
    session match {
      case Some(webSession) =>
        service.UserSessionService().findBySessionToken(webSession.value) match {
          case Some(userSession) if userSession.isValid =>
            redirectWithSession(userSession, "/main")

          case _ =>
            view.HtmlResponse(body)
        }
      case _ =>
        view.HtmlResponse(body)
    }
  }

  @trapToErrorPage()
  @cask.postForm("/login")
  def login(email: String, password: String, request: cask.Request): cask.Response[String] = {
    Try(service.UserService().authenticate(email, password, request.getSessionCookie(sessionCookie).map(_.value))) match {
      case Success(user) if user.userSession.exists(_.isValid) =>
        redirectWithSession(user.userSession.get, "/main")

      case Success(user) =>
        throw api.ApiError(500, Some(s"Failed to create session correctly for user ${user.username}"))

      case Failure(apiError: api.ApiError) if apiError.statusCode == 403 =>
        logger.warn(s"Failed to authenticate: $email")
        cask.Redirect("/login")

      case Failure(apiError: api.ApiError) =>
        throw apiError

      case Failure(t) =>
        throw t
    }
  }

  initialize()
}
