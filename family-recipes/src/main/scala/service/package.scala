import model.Database

package object service {

  def FamilyRecipeInstanceService() = new FamilyRecipeInstanceService(Database.mainDatabase)
  def UserService() = new UserService(Database.mainDatabase)
  def UserSessionService() = new UserSessionService(Database.mainDatabase)

}
