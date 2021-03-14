package service

import model.Database

class UserService(database: Database) {

  def authenticate(username: String, password: String): model.User = database.Users().find(username) match {
    case Some(user) if util.secureHash(password) == user.password => user
    case _                                                        => throw api.ApiError(403)

  }
}
