package view.page

import scalatags.Text.all._
import view.bootstrap
import view.tags._

class MainPage extends view.WebViewRoutes {

  def mainPage(user: model.User): Frag = {
    val instanceName = user.familyRecipeInstance.map(_.name)
    List(
      nav(cls := "navbar navbar-expand-md navbar-light bg-light fixed-top")(
        a(cls := "navbar-brand", href := "#")(s"Family Recipes${instanceName.map(name => s" - $name").getOrElse("")}"),
        button(
          cls := "navbar-toggler",
          `type` := "button",
          dataToggle := "collapse",
          dataTarget := "#navbarSupportedContent",
          aria.controls := "navbarSupportedContent",
          aria.expanded := "false",
          aria.label := "Toggle navigation"
        )(span(cls := "navbar-toggler-icon")),
        div(cls := "collapse navbar-collapse justify-content-md-end", id := "navbarSupportedContent")(
          form(cls := "form-inline my-2 my-lg-0")(
            input(`type` := "text", cls := "form-control mr-sm-2", placeholder := "Search for...")
          ),
          div(cls := "btn-group mr-2")(
            button(
              `type` := "button",
              cls := "btn btn-outline-primary dropdown-toggle",
              dataToggle := "dropdown",
              aria.haspopup := "true",
              aria.expanded := "false"
            )(i(cls := "bi-bookmark")(" Favorites")),
            div(cls := "dropdown-menu")(
              button(cls := "dropdown-item")("Rice Pudding"),
              button(cls := "dropdown-item")("Scones"),
              button(cls := "dropdown-item active")("Yassa Poulet"),
              button(cls := "dropdown-item")("Pasta")
            )
          ),
          div(cls := "btn-group")(
            button(
              `type` := "button",
              cls := "btn btn-outline-secondary dropdown-toggle",
              dataToggle := "dropdown",
              aria.haspopup := true,
              aria.expanded := "false"
            )(i(cls := "bi bi-gear-wide-connected")(s" ${user.username}")),
            div(cls := "dropdown-menu dropdown-menu-lg-right")(
              button(cls := "dropdown-item")("Profile"),
              button(cls := "dropdown-item")("Security/Privacy"),
              button(cls := "dropdown-item")("Help"),
              div(cls := "dropdown-divider"),
              a(cls := "dropdown-item", href := "/logout", role := "button")("Logout")
            )
          )
        )
      ),
      div(cls := "jumbotron")(
        div(cls := "container")(
          h2("Yassa Poulet"),
          div(cls := "text-center")(
            img(cls := "img-fluid rounded", src := "/view/images/yassa-poulet.jpg", alt := "Yassa Poulet")
          ),
          p(cls := "lead")("An old family favorite captured here for easy reference!")
        ),
        div(cls := "container")(
          h2("Ingredients"),
          ul(
            li(
              p("""Lorem ipsum dolor sit amet, consectetur adipiscing elit,
                |sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.""".stripMargin.replace('\n', ' '))
            ),
            li(p("Tellus orci ac auctor augue mauris augue neque.")),
            li(p("Interdum velit laoreet id donec ultrices tincidunt arcu non.")),
            li(p("Diam phasellus vestibulum lorem sed risus ultricies tristique nulla.")),
            li(p("Nisl purus in mollis nunc sed id semper risus."))
          )
        )
      )
    )
  }

  @view.useWebSession()
  @cask.get("/main")
  def index()(session: Option[cask.Cookie]): cask.Response[String] = {
    session match {
      case Some(webSession) =>
        service.UserSessionService().findBySessionToken(webSession.value) match {
          case Some(userSession) if userSession.isValid =>
            val body = page(
              title = "Family Recipes - Family Recipes - Highland Cows - Main",
              pageContent = List(mainPage(userSession.user.get)),
              stylesheets = List(link(rel := "stylesheet", href := "/view/css/main-page.css"), bootstrap.iconsCSS)
            )
            view.HtmlResponse(body)
          case _ =>
            cask.Redirect("/")
        }

      case _ =>
        cask.Redirect("/")
    }
  }

  @view.useWebSession()
  @cask.get("/logout")
  def logout()(session: Option[cask.Cookie]): cask.Response[String] = {
    session.foreach(cookie => service.UserSessionService().deleteSessionBySessionToken(cookie.value))
    cask.Redirect("/")
  }

  initialize()
}
