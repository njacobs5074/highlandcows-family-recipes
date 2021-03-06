package api

import cask.model.Response
import cask.model.Response.Raw
import cask.router.Result
import org.slf4j.{ Logger, LoggerFactory }

class wrapExceptions() extends cask.RawDecorator {

  def wrapFunction(ctx: cask.Request, delegate: Delegate): Result[Raw] = {
    val requestPath = ctx.exchange.getRequestPath
    val logger: Logger = LoggerFactory.getLogger(requestPath)
    delegate(Map.empty) match {
      case result: Result.Error.Exception =>
        result.t match {
          case apiError: ApiError =>
            logger.warn(
              s"""${apiError.statusText.map(statusText => s"message=$statusText").getOrElse("")}
              |status=${apiError.statusCode}""".stripMargin.replace('\n', ' ')
            )
            Result.Success(Response[String](apiError.statusText.getOrElse(""), apiError.statusCode))
          case t =>
            val errmsg = Option(t.getMessage).getOrElse(t.getClass.getSimpleName)
            logger.warn(s"""message=$errmsg status=500""".stripMargin.replace('\n', ' '))
            Result.Success(Response[String](errmsg, 500))
        }

      case result =>
        logger.info(s"Success: result = $result")
        result
    }
  }
}
