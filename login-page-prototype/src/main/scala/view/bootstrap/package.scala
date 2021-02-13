package view

import scalatags.Text.all._
import scalatags.Text.{ tags, tags2 }

package object bootstrap {

  /** Generate the HTML head tag with view.bootstrap scripts already provided */
  def head(
    titleText: String,
    description: Option[String] = None,
    author: Option[String] = None,
    additionalStyleSheets: Seq[String] = Seq.empty[String],
    favIcon: String = "favicon.ico"
  ): Frag = {
    tags.head(
      meta(charset := "utf-8"),
      meta(httpEquiv := "X-UA-Compatible", content := "IE=edge"),
      meta(name := "viewport", content := "width=device-width, initial-scale=1"),
      meta(name := "description", content := description.getOrElse("")),
      meta(name := "author", content := author.getOrElse("")),
      link(rel := "icon", href := favIcon),
      tags2.title(titleText),
      link(href := "/static/view/bootstrap/css/bootstrap.min.css", rel := "stylesheet"),
      for (stylesheet <- additionalStyleSheets) yield link(href := stylesheet, rel := "stylesheet")
    )
  }
}
