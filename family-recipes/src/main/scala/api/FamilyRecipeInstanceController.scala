package api

import cask.model.Response
import org.slf4j.{ Logger, LoggerFactory }

class FamilyRecipeInstanceController extends ApiRoutes {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  @cask.post("/familyRecipeInstance")
  def createInstance(request: cask.Request): Response[String] = {

    val payload = request.json
    logger.debug(s"payload = $payload request = ${request.headers.mkString(",")}")
    cask.Response(ujson.Obj("message" -> "Hello, world").toString, 200)

  }

  initialize()
}
