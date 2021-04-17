import io.undertow.util.HeaderMap
import play.api.libs.json.{ Format, JsValue, Json, Reads }
import util._

import scala.language.implicitConversions
import scala.util.Try

package object api {

  case class ApiError(
      statusCode: Int,
      statusText: Option[String] = None,
      data: Option[Any] = None,
      headers: Seq[(String, String)] = Nil,
      logAtErrorLevel: Boolean = false
  ) extends Exception(statusText.orNull)

  val AuthenticationError: ApiError = ApiError(403)

  implicit class RequestExt(request: cask.Request) {
    def as[T: Reads]: T =
      Try(Json.parse(request.text()).as[T]).getOrElse {
        throw api.ApiError(400, Some(s"Malformed JSON"), Some(request.text()))
      }
  }

  implicit class HeaderValuesExt(headerMap: HeaderMap) {
    def getHeader(fieldName: String): Option[String] = Option(headerMap.get(fieldName)).flatMap(header => Option(header.getFirst))
  }

  case class Response(statusCode: Int, statusText: Option[String] = None, data: Option[JsValue] = None)

  object Response {
    implicit val format: Format[Response] = Json.format[Response]
    implicit def asString(response: Response): String = response.asJson.asString
  }
}
