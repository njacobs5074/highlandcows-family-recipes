import controller.ApiError
import model.FamilyRecipeInstance
import play.api.http.Status._
import play.api.libs.json._

package object dto {

  case class FamilyRecipeInstanceDTO(name: String, description: String, adminUserEmail: String, password: Option[String]) {
    def toFamilyRecipeInstance: FamilyRecipeInstance =
      FamilyRecipeInstance(
        name,
        description,
        adminId = None
      )
  }

  object FamilyRecipeInstanceDTO {

    def apply(familyRecipeInstance: model.FamilyRecipeInstance): FamilyRecipeInstanceDTO = {
      val adminUser = familyRecipeInstance.adminUser.getOrElse {
        throw new ApiError(INTERNAL_SERVER_ERROR, Some(s"Require adminUser to be populated: id=${familyRecipeInstance.id}"))
      }
      new FamilyRecipeInstanceDTO(
        familyRecipeInstance.name,
        familyRecipeInstance.description,
        adminUser.username,
        Some(adminUser.password)
      )
    }

    implicit val format: Format[FamilyRecipeInstanceDTO] = Json.format[FamilyRecipeInstanceDTO]
  }

  case class UserDTO(username: String, password: String, familyRecipeInstance: String)
  object UserDTO {
    implicit val format: Format[UserDTO] = Json.format[UserDTO]
  }

  case class ResetUserPasswordDTO(username: String, oldPassword: String, newPassword: String)
  object ResetUserPasswordDTO {
    implicit val format: Format[ResetUserPasswordDTO] = Json.format[ResetUserPasswordDTO]
  }

  implicit class JsonExt[T](data: T)(implicit writes: Writes[T]) {
    def toJson: JsValue = Json.toJson(data)
  }

}
