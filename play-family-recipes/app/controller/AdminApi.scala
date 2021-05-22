package controller

import dto.{ FamilyRecipeInstanceDTO, ResetUserPasswordDTO }
import play.api.Logging
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc._
import service.{ FamilyRecipeInstanceService, UsersService }
import util._

import javax.inject._
import scala.concurrent.ExecutionContext
import scala.util.{ Failure, Success }

@Singleton
class AdminApi @Inject() (
    val familyRecipeInstanceService: FamilyRecipeInstanceService,
    val usersService: UsersService,
    cc: ControllerComponents,
    basicAuthAction: BasicAuthAction,
    implicit val executionContext: ExecutionContext
) extends AbstractController(cc)
    with Logging {

  def instance: Action[JsValue] = basicAuthAction.async(parse.json) { request =>
    val familyRecipeInstanceDTO = request.body.as[FamilyRecipeInstanceDTO]

    familyRecipeInstanceService.createFamilyRecipeInstance(familyRecipeInstanceDTO).asTry.map {
      case Success(response) =>
        Ok(Json.toJson(response))
      case Failure(apiError: ApiError) =>
        apiError.asStatus
      case Failure(t) =>
        logger.warn("Failed to create instance", t)
        InternalServerError(Option(t.getMessage).getOrElse(t.getClass.getSimpleName))
    }
  }

  def list = basicAuthAction.async { _ =>
    familyRecipeInstanceService.list().asTry.map {
      case Success(response) =>
        Ok(Json.toJson(response.map(FamilyRecipeInstanceDTO(_))))
      case Failure(apiError: ApiError) =>
        apiError.asStatus
      case Failure(t) =>
        logger.warn("Failed to list instances", t)
        InternalServerError(Option(t.getMessage).getOrElse(t.getClass.getSimpleName))
    }
  }

  def resetPassword: Action[JsValue] = basicAuthAction.async(parse.json) { request =>
    val dto = request.body.as[ResetUserPasswordDTO]

    usersService.resetUserPassword(dto).asTry.map {
      case Success(response) =>
        Ok(Json.toJson(response))
      case Failure(apiError: ApiError) =>
        apiError.asStatus
      case Failure(t) =>
        logger.warn("Failed to reset password", t)
        InternalServerError(Option(t.getMessage).getOrElse(t.getClass.getSimpleName))
    }
  }
}
