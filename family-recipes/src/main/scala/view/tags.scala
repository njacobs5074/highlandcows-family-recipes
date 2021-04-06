package view

import scalatags.Text
import scalatags.Text.all._
import scalatags.Text.tags2.tag

object tags {
  val integrity: Attr = attr("integrity")
  val crossorigin: Attr = attr("crossorigin")
  val dataToggle: Attr = attr("data-toggle")
  val dataTarget: Attr = attr("data-target")
  val nav: Text.TypedTag[String] = tag("nav")
  val navbar: Text.TypedTag[String] = tag("navbar")
  val main: Text.TypedTag[String] = tag("main")
}
