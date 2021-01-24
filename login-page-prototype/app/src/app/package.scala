package object app {
  case class ApiError(statusCode: Int, statusText: Option[String] = None, data: Option[Any] = None)
      extends Exception(statusText.orNull)
}
