package view

import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import scalatags.Text.all._
import util.config.WebResourceConfig

object bootstrap {
  private val cssConfig: WebResourceConfig = app.config.as[WebResourceConfig]("view.bootstrap.css")
  private val iconsCssConfig: WebResourceConfig = app.config.as[WebResourceConfig]("view.bootstrap.iconsCss")
  private val jqueryScriptConfig: WebResourceConfig = app.config.as[WebResourceConfig]("view.bootstrap.jqueryScript")
  private val scriptConfig: WebResourceConfig = app.config.as[WebResourceConfig]("view.bootstrap.script")

  val standardCSS: ConcreteHtmlTag[String] = link(
    rel := "stylesheet",
    href := cssConfig.url,
    cssConfig.integrity
      .map(tags.integrity := _)
      .getOrElse(throw new RuntimeException("Bootstrap CSS requires 'integrity' config")),
    cssConfig.crossorigin
      .map(tags.crossorigin := _)
      .getOrElse(throw new RuntimeException("Bootstrap CSS requires 'crossorigin' config"))
  )

  val iconsCSS: ConcreteHtmlTag[String] = link(rel := "stylesheet", href := iconsCssConfig.url)

  val javaScript: ConcreteHtmlTag[String] = script(
    src := scriptConfig.url,
    scriptConfig.integrity
      .map(tags.integrity := _)
      .getOrElse(throw new RuntimeException("Bootstrap script requires 'integrity' config")),
    scriptConfig.crossorigin
      .map(tags.crossorigin := _)
      .getOrElse(throw new RuntimeException("Bootstrap script requires 'crossorigin' config"))
  )

  val jqueryScript: ConcreteHtmlTag[String] = script(
    src := jqueryScriptConfig.url,
    jqueryScriptConfig.integrity
      .map(tags.integrity := _)
      .getOrElse(throw new RuntimeException("Bootstrap jQuery script requires 'integrity' config")),
    jqueryScriptConfig.crossorigin
      .map(tags.crossorigin := _)
      .getOrElse(throw new RuntimeException("Bootstrap jQuery script requires 'crossorigin' config"))
  )
}
