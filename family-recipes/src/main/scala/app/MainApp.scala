package app

import cask.main.Routes
import view.page

class MainApp extends api.ApiRoutes with view.WebViewRoutes {

  @cask.staticResources("/view")
  def staticResources(): String = "view"

  @view.useWebSession()
  @cask.get("/")
  def index()(session: Option[cask.Cookie]): cask.Response[String] = {
    session match {
      case Some(webSession) =>
        service.UserSessionService().findBySessionToken(webSession.value) match {
          case Some(userSession) if userSession.isValid =>
            redirectWithSession(userSession, "/main")

          case Some(userSession) =>
            redirectWithSession(userSession, "/login")

          case None =>
            val newSession = service.UserSessionService().createNewSession()
            redirectWithSession(newSession, "/login")
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
