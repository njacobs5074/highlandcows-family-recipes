package model

import java.util.Date

case class FamilyRecipeInstance(
    name: String,
    description: String,
    adminId: Option[Int],
    created: Date = new Date(),
    id: Int = 0
) {
  @SuppressWarnings(Array(
    "scalafix:DisableSyntax.var"
  ))
  var adminUser: Option[User] = None
}
