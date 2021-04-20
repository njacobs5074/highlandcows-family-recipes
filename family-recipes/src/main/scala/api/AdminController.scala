package api

import dto.JsonExt
import org.slf4j.{ Logger, LoggerFactory }
import util.helpers.caskExt._

class AdminController extends ApiRoutes {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  @authenticated("createInstance")
  @cask.get("/admin")
  def list(): cask.Response[String] = {
    val response = service.FamilyRecipeInstanceService().list()

    cask.Response[String](api.Response(200, data = Some(response.toJson)))
  }

  /** Web endpoint to create a new family recipe web site instance. We only
   *  allowed specially authenticated callers to connect to this endpoint.
   *  See `application.conf` for the definition of this information.
   */
  @authenticated("createInstance")
  @cask.post("/admin/familyRecipeInstance")
  def familyRecipeInstance(request: cask.Request): cask.Response[String] = {

    val payload = request.as[dto.FamilyRecipeInstanceDTO]
    val response = service.FamilyRecipeInstanceService().createFamilyRecipeInstance(payload)

    cask.Response[String](api.Response(200, data = Some(response.toJson)))
  }

  /** Endpoint to allow resetting an admin user's password */
  @authenticated("createInstance")
  @cask.post("/admin/resetAdminPassword")
  def resetAdminPassword(request: cask.Request): cask.Response[String] = {

    val payload = request.as[dto.ResetAdminPasswordDTO]
    service.FamilyRecipeInstanceService().resetAdminPassword(payload)

    cask.Response[String](api.Response(200))
  }

  initialize()
}
