import play.api.libs.json._

package object dto {

  case class FamilyRecipeInstanceDTO(name: String, description: String, adminUserEmail: String, password: Option[String])
  object FamilyRecipeInstanceDTO {

    def apply(familyRecipeInstance: model.FamilyRecipeInstance): FamilyRecipeInstanceDTO = {
      val adminUser = familyRecipeInstance.adminUser.getOrElse(throw new RuntimeException("Require adminUser to be populated"))
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

  case class ResetAdminPasswordDTO(adminUserEmail: String, oldPassword: String, newPassword: String)
  object ResetAdminPasswordDTO {
    implicit val format: Format[ResetAdminPasswordDTO] = Json.format[ResetAdminPasswordDTO]
  }

  implicit class JsonExt[T](data: T)(implicit writes: Writes[T]) {
    def toJson: JsValue = Json.toJson(data)
  }

}
