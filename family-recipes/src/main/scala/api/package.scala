import play.api.libs.json.{ Format, JsValue, Json }
import util.helpers.jsonExt._

import scala.language.implicitConversions

package object api {

  case class ApiError(
      statusCode: Int,
      statusText: Option[String] = None,
      data: Option[Any] = None,
      headers: Seq[(String, String)] = Nil,
      logAtErrorLevel: Boolean = false
  ) extends Exception(statusText.orNull)

  val AuthenticationError: ApiError = ApiError(403)

  case class Response(statusCode: Int, statusText: Option[String] = None, data: Option[JsValue] = None)

  object Response {
    implicit val format: Format[Response] = Json.format[Response]
    implicit def asString(response: Response): String = response.asJson.asString
  }
}
