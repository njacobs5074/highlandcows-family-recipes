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
  def familyRecipeInstance(request: cask.Request): String = {

    val payload = request.as[dto.FamilyRecipeInstanceDTO]
    val response = service.FamilyRecipeInstanceService().createFamilyRecipeInstance(payload)

    upickle.default.write(api.Response(200, Some("Created"), data = Some(upickle.default.writeJs(response))))
  }

  /** Endpoint to allow resetting an admin user's password */
  @authenticated("createInstance")
  @cask.post("/resetAdminPassword")
  def resetAdminPassword(request: cask.Request) = {

    val payload = request.as[dto.ResetAdminPasswordDTO]
    service.FamilyRecipeInstanceService().resetAdminPassword(payload)

    upickle.default.write(api.Response(200, Some("Password updated")))
  }
  initialize()
}
