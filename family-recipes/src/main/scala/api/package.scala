import cask.model.Response.Raw
import cask.model._
import cask.router.Result
import net.ceedubs.ficus.Ficus._

import scala.util.Try

package object api {

  case class ApiError(statusCode: Int, statusText: Option[String] = None, data: Option[Any] = None)
      extends Exception(statusText.orNull)

  /**
    * Decorator that makes our user session cookie available to endpoints if they want it.
    * Will be available as an addtional argument, `session`
    */
  class LoggedIn extends cask.RawDecorator {
    lazy val sessionName = app.config.as[String]("web.sessionName")

    override def wrapFunction(request: Request, delegate: Delegate): Result[Raw] = {
      val session: Option[cask.Cookie] = request.cookies
        .find { cookie => cookie._1 == sessionName }
        .map(_._2)
      delegate(Map("session" -> session))
    }
  }

  implicit class RequestExt(request: cask.Request) {

    def json: ujson.Value = {
      Try(ujson.read(request.text())).getOrElse {
        throw api.ApiError(400, Some(s"Malformed JSON"), Some(request.text()))
      }
    }
  }
}
