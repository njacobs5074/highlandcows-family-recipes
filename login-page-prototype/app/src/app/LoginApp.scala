package app

import cask.main.Routes
import scalatags.Text.all._

import view.bootstrap._
import view._
import org.slf4j.LoggerFactory
import cask.model.{ Request, Response }
import cask.router.Result

import scala.util._

class Controller extends cask.Routes {

  val logger = LoggerFactory.getLogger(getClass())

  val validEmail = "nick@highlandcows.com"
  val validPassword = "Silver123"

  class loggedIn extends cask.RawDecorator {
    def wrapFunction(ctx: Request, delegate: Delegate) = {
      val userCookie: Option[cask.Cookie] = ctx.cookies.get("user").filter(_.value == validEmail)
      delegate(Map("user" -> userCookie))
    }
  }

  override val decorators = Seq(new loggedIn())

  @cask.staticResources("/static")
  def staticResources(): String = "static"

  @cask.get("/")
  def index()(user: Option[cask.Cookie]): doctype = {
    user match {
      case Some(_) =>
        main()(user)

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
  def post(inputEmail: String, inputPassword: String) = {
    if (inputEmail == validEmail && inputPassword == validPassword) {
      cask.Response("", 301, headers = Seq("Location" -> "/main"), cookies = Seq(cask.Cookie("user", inputEmail)))
    } else {
      cask.Redirect("/")
    }
  }

  @cask.post("/logout")
  def logout() = {
    cask.Response(
      "",
      301,
      headers = Seq("Location" -> "/"),
      cookies = Seq(cask.Cookie("user", "", expires = java.time.Instant.EPOCH))
    )
  }

  @cask.get("/main")
  def main()(user: Option[cask.Cookie]): doctype = {
    user match {
      case Some(cookie) =>
        doctype("html")(
          html(
            bootstrap.head(titleText = "Logged In", additionalStyleSheets = Seq(loginFormCssFile)),
            view.main(cookie.value)
          )
        )
      case None =>
        index()(None)
    }
  }

  initialize()
}

object LoginApp extends cask.Main {
  override def allRoutes: Seq[Routes] = Seq(new Controller)

}
