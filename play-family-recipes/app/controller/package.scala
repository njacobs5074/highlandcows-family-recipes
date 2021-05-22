import play.api.http.Status._
import play.api.mvc.{ RequestHeader, Results }

package object controller {

  class ApiError(
      val statusCode: Int,
      val statusText: Option[String] = None,
      val data: Option[Any] = None,
      val headers: Seq[(String, String)] = Nil,
      val logAtErrorLevel: Boolean = false
  ) extends Exception(statusText.orNull) {
    def asStatus = Results.Status(statusCode)(statusText.getOrElse(""))
  }

  object ApiError {
    def apply(
      statusCode: Int,
      statusText: Option[String] = None,
      data: Option[Any] = None,
      headers: Seq[(String, String)] = Nil,
      logAtErrorLevel: Boolean = false
    ): ApiError = {
      new ApiError(statusCode, statusText, data, headers, logAtErrorLevel)
    }
  }

  case class AuthenticationError(override val data: Option[Any] = None)
      extends ApiError(FORBIDDEN, Some("Authentication failed"), data = data)

  implicit class RequestHeaderExt(requestHeader: RequestHeader) {
    def getAuthorization: Option[String] = requestHeader.headers.get("Authorization")
  }
}
