package service

import controller.ApiError
import dto.{ ResetUserPasswordDTO, UserDTO }
import model.UsersDAO
import play.api.Configuration
import play.api.http.Status._
import util._

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

class UsersService @Inject() (
    val usersDAO: UsersDAO,
    val config: Configuration,
    implicit val executionContext: ExecutionContext
) {

  private lazy val secretKey: String = config.get[String]("app.secretKey")

  def resetUserPassword(resetUserPasswordDTO: ResetUserPasswordDTO): Future[UserDTO] = {
    usersDAO.find(resetUserPasswordDTO.username).flatMap {
      case Some(user)
          if user.familyRecipeInstance.isDefined &&
            resetUserPasswordDTO.oldPassword.secureHash(secretKey) == user.password =>
        usersDAO.update(user.copy(password = resetUserPasswordDTO.newPassword.secureHash(secretKey))).map(_ =>
          UserDTO(user.username, resetUserPasswordDTO.newPassword, user.familyRecipeInstance.get.name))

      case Some(_) =>
        throw new ApiError(BAD_REQUEST, Some("Passwords don't match"))
      case None =>
        throw new ApiError(NOT_FOUND)
    }
  }

}
