import upickle.default.{ ReadWriter => RW, macroRW }

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

    implicit val rw: RW[FamilyRecipeInstanceDTO] = macroRW
  }

  case class UserDTO(username: String, password: String, familyRecipeInstance: String)
  object UserDTO {
    implicit val rw: RW[UserDTO] = macroRW
  }

  case class ResetAdminPasswordDTO(adminUserEmail: String, oldPassword: String, newPassword: String)
  object ResetAdminPasswordDTO {
    implicit val rw: RW[ResetAdminPasswordDTO] = macroRW
  }

}
