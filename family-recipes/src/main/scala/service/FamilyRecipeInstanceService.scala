package service

import dto._
import model.{ Database, FamilyRecipeInstance }

import java.util.Date

class FamilyRecipeInstanceService(database: Database) {

  /** Create an instance of the site based on a config file with the specified name. */
  def createFromConfig(familyRecipeInstanceDTO: FamilyRecipeInstanceDTO): FamilyRecipeInstance = {

    database.Users().find(familyRecipeInstanceDTO.adminUserEmail) match {
      case Some(user) =>
        // Found the specified user so now we can create the instance
        // with that user as the administrator
        val familyRecipeInstance = database
          .FamilyRecipeInstances()
          .insert(
            FamilyRecipeInstance(
              familyRecipeInstanceDTO.name,
              familyRecipeInstanceDTO.description,
              user.id,
              created = new Date()
            )
          )

        // Update the admin user to point to the specified instance
        val adminUser = database
          .Users()
          .update(user.copy(familyRecipeInstanceId = familyRecipeInstance.id))

        // Re-query the instance from the database to ensure that all is well
        database
          .FamilyRecipeInstances()
          .find(familyRecipeInstance.id)
          .getOrElse(throw api.ApiError(500))

      case None =>
        throw api.ApiError(404)
    }
  }
}
