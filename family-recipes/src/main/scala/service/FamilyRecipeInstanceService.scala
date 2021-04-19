package service

import dto._
import model.{ Database, FamilyRecipeInstance }
import org.apache.commons.lang3.{ RandomStringUtils => RSU }

class FamilyRecipeInstanceService(database: Database) {

  /** Create an instance of the specified family recipe site. We do the necessary checks to ensure that
   *  duplicate sites and/or admin users are not creatd.
   */
  def createFamilyRecipeInstance(familyRecipeInstanceDTO: FamilyRecipeInstanceDTO): FamilyRecipeInstanceDTO = {
    database.ctx.transaction {

      database.FamilyRecipeInstances().find(familyRecipeInstanceDTO.name) match {
        case Some(_) =>
          throw api.ApiError(409, Some(s"A family recipes instance with the name ${familyRecipeInstanceDTO.name} already exists"))

        case None =>
          // First create the new FamilyRecipeInstance.
          val familyRecipeInstance = database
            .FamilyRecipeInstances()
            .insert(FamilyRecipeInstance(familyRecipeInstanceDTO.name, familyRecipeInstanceDTO.description, adminId = None))

          // Then create the admin user and assign it to the new instance.
          val (adminUser, tempPassword) =
            database.Users().find(familyRecipeInstanceDTO.adminUserEmail) match {
              case Some(_) =>
                throw api.ApiError(
                  409,
                  Some(s"User ${familyRecipeInstanceDTO.adminUserEmail} is already assigned to an instance")
                )

              case None =>
                // Create a temporary password for the admin user.
                val tempPassword = RSU.randomPrint(20)
                val passwordHash = util.secureHash(tempPassword)
                (
                  database
                    .Users()
                    .insert(model.User(familyRecipeInstanceDTO.adminUserEmail, passwordHash, familyRecipeInstance.id)),
                  tempPassword
                )
            }

          // Copy the temporary password into the DTO so that we can send it back to the caller.
          // This will be the only time we'll share the plaintext version of it
          FamilyRecipeInstanceDTO(
            database
              .FamilyRecipeInstances()
              .update(familyRecipeInstance.copy(adminId = Some(adminUser.id)))
          ).copy(password = Some(tempPassword))
      }
    }
  }

  def resetAdminPassword(resetAdminPasswordDTO: ResetAdminPasswordDTO): Unit = {
    database.Users().find(resetAdminPasswordDTO.adminUserEmail) match {
      case Some(user) if util.secureHash(resetAdminPasswordDTO.oldPassword) == user.password =>
        database.Users().update(user.copy(password = util.secureHash(resetAdminPasswordDTO.newPassword)))

      case Some(_) =>
        throw api.AuthenticationError

      case None =>
        throw api.ApiError(404, Some(s"User not found"))
    }
  }

  def list(): List[FamilyRecipeInstanceDTO] = database.FamilyRecipeInstances().list().map(dto.FamilyRecipeInstanceDTO(_))

}
