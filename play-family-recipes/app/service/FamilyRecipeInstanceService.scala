package service

import dto.FamilyRecipeInstanceDTO
import model.{ FamilyRecipeInstance, FamilyRecipeInstancesDAO, User }
import play.api.Configuration
import util._

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class FamilyRecipeInstanceService @Inject() (
    val familyRecipeInstancesDAO: FamilyRecipeInstancesDAO,
    val config: Configuration,
    implicit val executionContext: ExecutionContext
) {

  private lazy val secretKey: String = config.get[String]("app.secretKey")

  def createFamilyRecipeInstance(
    familyRecipeInstanceDTO: FamilyRecipeInstanceDTO
  ): Future[FamilyRecipeInstanceDTO] = {

    val newInstance = familyRecipeInstanceDTO.toFamilyRecipeInstance
    val tempPassword = generateTempPassword()
    val adminUser = User(familyRecipeInstanceDTO.adminUserEmail, tempPassword.secureHash(secretKey))

    familyRecipeInstancesDAO.insert(newInstance, adminUser).map(FamilyRecipeInstanceDTO(_).copy(password = Some(tempPassword)))

  }

  def list(): Future[List[FamilyRecipeInstance]] = familyRecipeInstancesDAO.list()

}
