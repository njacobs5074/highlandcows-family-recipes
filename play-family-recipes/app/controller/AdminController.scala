package controller

import play.api.Configuration
import play.api.mvc._
import service.FamilyRecipeInstanceService

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class AdminController @Inject() (
    val familyRecipeInstanceService: FamilyRecipeInstanceService,
    val controllerComponents: ControllerComponents,
    val config: Configuration,
    implicit val executionContext: ExecutionContext
) extends BaseController {}
