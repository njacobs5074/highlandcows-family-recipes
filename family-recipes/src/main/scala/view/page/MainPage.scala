package view.page

import scalatags.Text.all._
import view.bootstrap
import view.tags._

class MainPage extends view.WebViewRoutes {

  val mainPage: Frag = List(
    nav(cls := "navbar navbar-expand-md navbar-light bg-light fixed-top")(
      a(cls := "navbar-brand", href := "#")("Family Recipes"),
      button(
        cls := "navbar-toggler",
        `type` := "button",
        dataToggle := "collapse",
        dataTarget := "#navbarSupportedContent",
        aria.controls := "navbarSupportedContent",
        aria.expanded := "false",
        aria.label := "Toggle navigation"
      )(span(cls := "navbar-toggler-icon")),
      div(cls := "collapse navbar-collapse justify-content-md", id := "navbarSupportedContent")(
        form(cls := "form-inline my-2 my-lg=0")(
          input(`type` := "text", cls := "form-control mr-sm-2", placeholder := "Search for...")
        ),
        div(cls := "btn-group mr-2")(
          button(
            `type` := "button",
            cls := "btn btn-outline-primary dropdown-toggle",
            dataToggle := "dropdown",
            aria.haspopup := "true",
            aria.expanded := "false"
          )(i(cls := "bi-bookmark")("Favorites")),
          div(cls := "dropdown-menu")(
            button(cls := "dropdown-item")("Rice Pudding"),
            button(cls := "dropdown-item")("Scones"),
            button(cls := "dropdown-item active")("Yassa Poulet"),
            button(cls := "dropdown-item")("Pasta")
          )
        ),
        div(cls := "btn-group mr-2")(
          button(
            `type` := "button",
            cls := "btn btn-outline-secondary dropdown-toggle",
            aria.haspopup := true,
            aria.expanded := "false"
          )(i(cls := "bi-person")("Reddie")),
          div(cls := "dropdown-menu dropdown-menu-lg-right")(
            button(cls := "dropdown-item")("Profile"),
            button(cls := "dropdown-item")("Security/Privacy"),
            button(cls := "dropdown-item")("Help"),
            div(cls := "dropdown-divider"),
            button(cls := "dropdown-item")("Logout")
          )
        )
      )
    ),
    div(cls := "jumbotron")(
      div(cls := "container")(
        h1(cls := "display-3")("Yassa Poulet"),
        div(cls := "text-center")(
          img(cls := "img-fluid rounded", src := "/view/images/yassa-poulet.jpg", alt := "Yassa Poulet")
        ),
        p(cls := "lead")("An old family favorite captured here for easy reference!")
      ),
      div(cls := "container")(
        h2("Ingredients")(
          ul(
            li(
              """Lorem ipsum dolor sit amet, consectetur adipiscing elit,
                |sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.""".stripMargin.replace('\n', ' ')
            ),
            li("Tellus orci ac auctor augue mauris augue neque."),
            li("Interdum velit laoreet id donec ultrices tincidunt arcu non."),
            li("Diam phasellus vestibulum lorem sed risus ultricies tristique nulla."),
            li("Nisl purus in mollis nunc sed id semper risus.")
          )
        )
      )
    )
  )

  @view.loggedIn()
  @cask.get("/main")
  def index()(session: Option[cask.Cookie]): cask.Response[String] = {
    session match {
      case Some(webSession) =>
        service.UserSessionService().findBySessionKey(webSession.value) match {
          case Some(userSession) if !userSession.isExpired =>
            val body = page(
              title = "Family Recipes - Family Recipes - Highland Cows - Main",
              pageContent = List(mainPage),
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

  initialize()
}
