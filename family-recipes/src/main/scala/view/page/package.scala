package view

import scalatags.Text.all._
import scalatags.Text.tags2

package object page {

  def page(title: String,
           pageContent: List[Frag],
           stylesheets: List[Frag] = List.empty,
           scripts: List[Frag] = List.empty,
           htmlAttributes: List[Modifier] = List.empty,
           extraAttributes: List[Modifier] = List.empty
  ): doctype = {
    doctype("html")(
      html(lang := "en", htmlAttributes)(
        head(
          meta(charset := "utf-8"),
          meta(name := "viewport", content := "width=device-width, initial-scale=1, shrink-to-fit=no"),
          bootstrap.standardCSS,
          stylesheets,
          tags2.title(title)
        ),
        body(extraAttributes, pageContent, bootstrap.jqueryScript, bootstrap.javaScript, scripts)
      )
    )
  }

  case class Field(name: String,
                   value: Option[String] = None,
                   isValid: Option[Boolean] = None,
                   placeholder: Option[String] = None
  )
}
