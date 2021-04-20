package util.helpers

import io.undertow.util.HeaderMap
import play.api.libs.json.{ Json, Reads }

import scala.util.Try

package object caskExt {

  implicit class HeaderValuesExt(headerMap: HeaderMap) {
    def getHeader(fieldName: String): Option[String] = Option(headerMap.get(fieldName)).flatMap(header => Option(header.getFirst))
  }

  implicit class RequestExt(request: cask.Request) {
    def as[T: Reads]: T =
      Try(Json.parse(request.text()).as[T]).getOrElse {
        throw api.ApiError(400, Some(s"Malformed JSON"), Some(request.text()))
      }

    def getSessionCookie(cookieName: String): Option[cask.Cookie] =
      Option(request.exchange.getRequestCookie(cookieName)).map(cask.Cookie.fromUndertow)
  }
}
