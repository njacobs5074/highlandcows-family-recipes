package view

import scalatags.Text.all._

class HtmlResponse(htmlPage: doctype, statusCode: Int = 200, addlHeaders: Seq[(String, String)] = Seq.empty) {
  val response = cask.Response(htmlPage.render, statusCode, addlHeaders ++ Seq("content-type" -> "text/html"))
}

object HtmlResponse {
  def apply(htmlPage: doctype, statusCode: Int = 200, addlHeaders: Seq[(String, String)] = Seq.empty): cask.Response[String] = {
    new HtmlResponse(htmlPage, statusCode, addlHeaders).response
  }
}
