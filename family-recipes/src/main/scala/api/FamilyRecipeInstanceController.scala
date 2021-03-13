package api

import org.slf4j.{ Logger, LoggerFactory }

class FamilyRecipeInstanceController extends ApiRoutes {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  /** Web endpoint to create a new family recipe web site instance. We only
    * allowed specially authenticated callers to connect to this endpoint.
    * See `application.conf` for the definition of this information.
    */
  @authenticated("createInstance")
  @cask.post("/familyRecipeInstance")
  def createInstance(request: cask.Request): String = {

    val payload = request.as[dto.FamilyRecipeInstanceDTO]
    upickle.default.write(service.FamilyRecipeInstanceService().createFamilyRecipeInstance(payload))
  }

  initialize()
}
