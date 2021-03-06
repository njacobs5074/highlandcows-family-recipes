package app

import cask.main.Routes

object MainApp extends cask.Main {

  override def allRoutes: Seq[Routes] = Seq(new api.FamilyRecipeInstanceController())

}
