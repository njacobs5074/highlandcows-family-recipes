package view

import cask.model.Response
import scalatags.Text.all._

import scala.language.implicitConversions

case class HtmlResponse(htmlPage: doctype, statusCode: Int = 200, addlHeaders: Seq[(String, String)] = Seq.empty) {
  val response = cask.Response(htmlPage.render, statusCode, addlHeaders ++ Seq("content-type" -> "text/html"))
}

object HtmlResponse {
  implicit def toCaskResponse(htmlResponse: HtmlResponse): Response[String] = {
    cask.Response(
      htmlResponse.htmlPage.render,
      htmlResponse.statusCode,
      htmlResponse.addlHeaders ++ Seq("content-type" -> "text/html")
    )
  }
}
