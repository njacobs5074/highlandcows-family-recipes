package view.page

import scalatags.Text.all._
import view.tags

class ErrorPage extends cask.Routes {

  def errorPage(statusCode: Int, errorMessage: Option[String]): List[Frag] =
    List(
      tags.main(role := "main", cls := "flex-shrink-0")(
        div(cls := "container")(
          h1(cls := "mt-5")(s"We're having technical difficulties ($statusCode)"),
          p(cls := "lead")(
            "An unexpected system error has occurred. We are looking into the cause of this straight away."
          ),
          errorMessage.map(message => pre()(code()(message))).getOrElse(div())
        )
      ),
      footer(cls := "footer mt-auto py-3")(
        div(cls := "container")(
          span(cls := "text-muted")(a(href := "/")("Family Recipes Main Page"))
        )
      )
    )

  @cask.get("/error")
  def index(statusCode: Int, errorMessage: Option[String] = None): cask.Response[String] = {
    val body = page(
      title = "Family Recipes - Error",
      errorPage(statusCode, errorMessage),
      stylesheets = List(link(rel := "stylesheet", href := "/view/css/error.css")),
      htmlAttributes = List(cls := "h-100"),
      extraAttributes = List(cls := "d-flex flex-column h-100")
    )

    view.HtmlResponse(body)

  }

  initialize()
}
