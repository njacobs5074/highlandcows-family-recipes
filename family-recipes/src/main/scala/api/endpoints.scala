package api

import cask.model.Response.Raw
import cask.model.{ Request, Response => CaskResponse }
import cask.router.Result
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import org.apache.commons.codec.binary.Base64
import org.slf4j.{ Logger, LoggerFactory }
import util.config
import util.config.BasicAuthConfig
import util.helpers.caskExt._

/** Decorator the catches exceptions and maps them to specific responses */
class wrapExceptions() extends cask.RawDecorator {

  def wrapFunction(ctx: cask.Request, delegate: Delegate): Result[Raw] = {
    val requestPath = ctx.exchange.getRequestPath
    val logger: Logger = LoggerFactory.getLogger(requestPath)
    delegate(Map.empty) match {
      case result: Result.Error.Exception =>
        result.t match {
          case apiError: ApiError =>
            val logMsg =
              s"""${apiError.statusText.map(statusText => s"message=$statusText ").getOrElse("")}
                 |status=${apiError.statusCode}
                 |${apiError.data.map(data => s"data=$data").getOrElse("")}""".stripMargin.replace('\n', ' ')
            if (apiError.logAtErrorLevel) {
              logger.error(logMsg)
            } else {
              logger.warn(logMsg)
            }
            Result.Success(CaskResponse[String](apiError.statusText.getOrElse(""), apiError.statusCode, apiError.headers))
          case t =>
            val message = Option(t.getMessage).getOrElse(t.getClass.getSimpleName)
            logger.warn(s"""message=$message status=500""".stripMargin.replace('\n', ' '), t)
            Result.Success(CaskResponse[String](message, 500))
        }

      case result =>
        result
    }
  }
}

/** Decorator that can perform Basic Auth using credentials from the app configuration */
class authenticated(authenticAsUser: String) extends cask.RawDecorator {

  val credentials: BasicAuthConfig = app.config.as[config.BasicAuthConfig](s"authentications.$authenticAsUser")

  override def wrapFunction(ctx: Request, delegate: Delegate): Result[Raw] =
    // See https://en.wikipedia.org/wiki/Basic_access_authentication
    ctx.exchange.getRequestHeaders.getHeader("Authorization") match {
      case Some(authHeader) =>
        val fields = authHeader.split(" ")
        if (
          fields.headOption.contains("Basic") && fields.lastOption.exists { providedCredentials =>
            val tokens = new String(Base64.decodeBase64(providedCredentials)).split(":")
            tokens.length == 2 && tokens(0) == credentials.user && tokens(1) == credentials.password
          }
        ) {
          delegate(Map.empty)
        } else {
          throw ApiError(401, headers = Seq("WWW-Authenticate" -> s"""Basic realm="${credentials.realm}""""))
        }

      case None =>
        throw ApiError(401, headers = Seq("WWW-Authenticate" -> s"""Basic realm="${credentials.realm}""""))
    }
}
