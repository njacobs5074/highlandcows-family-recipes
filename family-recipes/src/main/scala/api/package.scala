import io.undertow.util.HeaderMap

import upickle.default.{ ReadWriter => RW, macroRW }

import scala.util.Try

package object api {

  case class ApiError(
    statusCode: Int,
    statusText: Option[String] = None,
    data: Option[Any] = None,
    headers: Seq[(String, String)] = Nil
  ) extends Exception(statusText.orNull)

  implicit class RequestExt(request: cask.Request) {
    def as[T: upickle.default.Reader]: T = Try(upickle.default.read[T](request.text())).getOrElse {
      throw api.ApiError(400, Some(s"Malformed JSON"), Some(request.text()))
    }
  }

  implicit class HeaderValuesExt(headerMap: HeaderMap) {
    def getHeader(fieldName: String): Option[String] = Option(headerMap.get(fieldName)).flatMap(header => Option(header.getFirst))
  }

  case class Response(statusCode: Int, statusText: Option[String] = None, data: Option[ujson.Value] = None)
  object Response {
    implicit val rw: RW[Response] = macroRW
  }
}
