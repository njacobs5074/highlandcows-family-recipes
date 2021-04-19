package view

import cask.model.Request
import cask.model.Response.Raw
import cask.router.Result
import net.ceedubs.ficus.Ficus._
import org.slf4j.{ Logger, LoggerFactory }
import view.page.ErrorPage

/**
 *  Decorator that makes our user session cookie available to endpoints if they want it.
 *  Will be available as an additional argument, `session`
 */
class useWebSession() extends cask.RawDecorator {
  lazy val sessionName: String = app.config.as[String]("webSession.cookieName")

  override def wrapFunction(request: Request, delegate: Delegate): Result[Raw] = {
    val session: Option[cask.Cookie] = request.cookies.find(cookie => cookie._1 == sessionName).map(_._2)
    delegate(Map("session" -> session))
  }
}

/**
 *  Decorator that if we get an unexpected exception will send the user to an error page
 *  and will also log the issue at the error level (which will send it to our bug tracker)
 */
class trapToErrorPage() extends cask.RawDecorator {
  val logger: Logger = LoggerFactory.getLogger("ErrorPage")

  override def wrapFunction(ctx: Request, delegate: Delegate): Result[Raw] = {
    delegate(Map.empty) match {
      case result: Result.Error.Exception =>
        result.t match {
          case apiError: api.ApiError =>
            val logMsg = s"""${apiError.statusText.getOrElse("No further detail available.")} (${apiError.statusCode})"""
            logger.error(logMsg, apiError)

            Result.Success(new ErrorPage().index(apiError.statusCode, Some(logMsg)))

          case t =>
            val message = Option(t.getMessage).getOrElse(t.getClass.getSimpleName)
            logger.error(message, t)

            Result.Success(new ErrorPage().index(500, Some(message)))
        }
      case result =>
        result
    }
  }
}
