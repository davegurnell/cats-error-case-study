package code.intro

sealed trait ApiError

object ApiError {
  case class UserNotFound(username: String) extends ApiError
  case class AvatarNotFound(userId: Int) extends ApiError
  case object PasswordIncorrect extends ApiError
  case object AdminPrivilegesRequired extends ApiError
  case class BadRequest(field: String, error: String) extends ApiError
  case class DuplicateUser(userId: Int) extends ApiError
}