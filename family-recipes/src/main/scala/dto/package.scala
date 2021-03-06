import upickle.default.{ ReadWriter => RW, macroRW }

package object dto {

  case class FamilyRecipeInstanceDTO(
      name: String,
      description: String,
      adminUserEmail: String
  )

  object FamilyRecipeInstanceDTO {
    implicit val rw: RW[FamilyRecipeInstanceDTO] = macroRW
  }
}
