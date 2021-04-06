package view.page

import scalatags.Text.all._

import scala.util.{ Failure, Success, Try }
import view.trapToErrorPage

class LoginPage extends view.WebViewRoutes {

  val loginForm: Frag = div(
    view.tags.nav(cls := "navbar navbar-expand-lg navbar-light bg-light fixed-top")(
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
      div(cls := "collapse navbar-collapse justify-content-end", id := "navbarSupportedContent")(
        form(cls := "form-inline my-2 my-lg-0", action := "/login", method := "post")(
          input(
            id := "email",
            name := "email",
            `type` := "text",
            placeholder := "Email",
            cls := "form-control mr-sm-2",
            aria.label := "Email"
          ),
          input(
            id := "password",
            name := "password",
            `type` := "password",
            placeholder := "Password",
            cls := "form-control mr-sm-2",
            aria.label := "Password"
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

  @view.loggedIn()
  @cask.get("/login")
  def login()(session: Option[cask.Cookie]): cask.Response[String] = {
    session match {
      case Some(webSession) =>
        service.UserSessionService().findBySessionKey(webSession.value) match {
          case Some(userSession) if !userSession.isExpired =>
            redirectWithSession(userSession, "/main")

          case _ =>
            val body = page(
              title = "Family Recipes - Login",
              pageContent = List(loginForm),
              stylesheets = List(link(rel := "stylesheet", href := "/view/css/login.css"))
            )
            cask.Response(body.render, 200, headers = Seq("content-type" -> body.httpContentType.get))
        }
      case _ =>
        val body = page(
          title = "Family Recipes - Login",
          pageContent = List(loginForm),
          stylesheets = List(link(rel := "stylesheet", href := "/view/css/login.css"))
        )
        cask.Response(body.render, 200, headers = Seq("content-type" -> body.httpContentType.get))
    }
  }

  @trapToErrorPage()
  @cask.postForm("/login")
  def login(email: String, password: String): cask.Response[String] = {
    // NB: The argument names must match exactly what is the login form above
    Try(service.UserService().authenticate(email, password)) match {
      case Success(user) if user.userSession.isDefined =>
        redirectWithSession(user.userSession.get, "/main")

      case Success(user) =>
        logger.error(s"Failed to create session for user ${user.username}")
        throw api.ApiError(500, Some(s"Failed to create user session for user ${user.username}"))

      case Failure(apiError: api.ApiError) if apiError.statusCode == 403 =>
        logger.warn(s"Failed to authenticate: $email")
        cask.Redirect("/")

      case Failure(apiError: api.ApiError) =>
        service.UserSessionService().deleteSession(email)
        throw apiError

      case Failure(t) =>
        service.UserSessionService().deleteSession(email)
        throw t
    }
  }

  initialize()
}
