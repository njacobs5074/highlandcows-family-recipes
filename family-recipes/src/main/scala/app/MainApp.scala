package app

import cask.main.Routes
import view.page

class MainApp extends api.ApiRoutes with view.WebViewRoutes {

  @cask.staticResources("/view")
  def staticResources(): String = "view"

  @view.loggedIn()
  @cask.get("/")
  def index()(session: Option[cask.Cookie]): cask.Response[String] = {
    session match {
      case Some(webSession) =>
        service.UserSessionService().findBySessionKey(webSession.value) match {
          case Some(userSession) if !userSession.isExpired =>
            redirectWithSession(userSession, "/main")

          case _ =>
            cask.Redirect("/login")
        }
      case None =>
        cask.Redirect("/login")

    }
  }

  initialize()
}

object MainApp extends cask.Main {

  override def allRoutes: Seq[Routes] =
    Seq(new MainApp(), new api.AdminController(), new page.LoginPage(), new page.ErrorPage(), new page.MainPage())

}
