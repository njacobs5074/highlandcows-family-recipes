package model

class FamilyRecipeInstancesDAO(database: Database) {

  import database.ctx._

  /** Create a new FamilyRecipeInstance in the database */
  def insert(familyRecipeInstance: FamilyRecipeInstance): FamilyRecipeInstance = database.ctx.transaction {
    val id = database.ctx.run(database.schema.familyRecipeInstances.insert(lift(familyRecipeInstance)).returningGenerated(_.id))
    database.ctx.run(database.schema.familyRecipeInstances.filter(_.id == lift(id))).head
  }

  /** Retrieve the specified instance of the web site */
  def find(id: Int): Option[FamilyRecipeInstance] = {
    val action = quote(for {
      familyRecipeInstance <- database.schema.familyRecipeInstances.filter(_.id == lift(id))
      adminUser <- database.schema.users.leftJoin(users => familyRecipeInstance.adminId.contains(users.id))
    } yield (familyRecipeInstance, adminUser))

    database.ctx.run(action).headOption.map { case (familyRecipeInstance, maybeAdminUser) =>
      familyRecipeInstance.adminUser = maybeAdminUser
      familyRecipeInstance
    }
  }

  def find(name: String): Option[FamilyRecipeInstance] = {
    val action = quote(for {
      familyRecipeInstance <- database.schema.familyRecipeInstances.filter(_.name == lift(name))
      adminUser <- database.schema.users.leftJoin(users => familyRecipeInstance.adminId.contains(users.id))
    } yield (familyRecipeInstance, adminUser))

    database.ctx.run(action).headOption.map { case (familyRecipeInstance, maybeAdminUser) =>
      familyRecipeInstance.adminUser = maybeAdminUser
      familyRecipeInstance
    }
  }

  def update(familyRecipeInstance: FamilyRecipeInstance): FamilyRecipeInstance = {
    val action = quote {
      database.schema.familyRecipeInstances.filter(_.id == lift(familyRecipeInstance.id)).update(lift(familyRecipeInstance))
    }
    database.ctx.transaction {
      database.ctx.run(action) match {
        case 1 =>
          find(familyRecipeInstance.id).getOrElse(throw new RuntimeException(s"Failed to re-query $familyRecipeInstance"))
        case 0 | _ =>
          throw new RuntimeException(s"Failed to update $familyRecipeInstance")
      }
    }
  }
}
