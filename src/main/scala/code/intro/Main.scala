package code.intro

import cats.implicits._

//noinspection NameBooleanParameters
object Main {
  def getAvatarUrl(username: String): Either[ApiError, String] =
    ???

  def login(username: String, password: String): Either[ApiError, User] =
    ???

  // Return a vector of avatar URLs:
  def listAvatarUrls: Either[ApiError, Vector[String]] =
    ???

  def insertUser(data: Map[String, String]): Either[ApiError, User] =
    ???

  def main(args: Array[String]): Unit = {
    println("getAvatarUrl")
//    println("alice " + getAvatarUrl("alice"))
//    println("bob " + getAvatarUrl("bob"))
//    println("charlie " + getAvatarUrl("charlie"))
//    println("dan " + getAvatarUrl("dan"))
    println()

    println("login")
//    println("alice " +  login("alice", "password"))
//    println("bob " +  login("bob", "password"))
    println()

    println("insertUser")
//    println("happy path " + insertUser(Map("username" -> "edmund", "admin" -> "false")))
//    println("unhappy path 1 " + insertUser(Map("username" -> "francis", "admin" -> "")))
//    println("unhappy path 2 " + insertUser(Map()))
    println()
  }
}
