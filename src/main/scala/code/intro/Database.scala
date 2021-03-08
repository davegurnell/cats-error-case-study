package code.intro

case class User(id: Int, username: String, admin: Boolean)
case class Password(userId: Int, password: String)
case class Avatar(userId: Int, url: String)

//noinspection NameBooleanParameters
object Database {
  var users = Vector(
    User(1, "alice", true),
    User(2, "bob", false),
    User(3, "charlie", true),
    User(4, "dan", false),
  )

  var passwords = Vector(
    Password(1, "password"),
    Password(2, "secret"),
    // Password(3, "unbreakable"),
    Password(4, "donthackme"),
  )

  var avatars = Vector(
    Avatar(1, "https://en.wikipedia.org/wiki/Aardvark#/media/File:Porc_formiguer.JPG"),
    Avatar(2, "https://en.wikipedia.org/wiki/American_bison#/media/File:American_bison_k5680-1.jpg"),
    Avatar(3, "https://en.wikipedia.org/wiki/Chihuahua_(dog)#/media/File:Chihuahua1_bvdb.jpg"),
    //Avatar(4, "https://en.wikipedia.org/wiki/Dachshund#/media/File:Short-haired-Dachshund.jpg"),
  )

  def nextUserId: Int =
    users.map(_.id).foldLeft(0)(Math.max) + 1

  def listUsers: Vector[User] =
    users

  def findUser(username: String): Either[ApiError, User] =
    users
      .find(_.username == username)
      .toRight[ApiError](ApiError.UserNotFound(username))

  def insertUser(user: User): Either[ApiError, User] = {
    if(users.map(_.id).contains(user.id)) {
      Left(ApiError.DuplicateUser(user.id))
    } else {
      users = users :+ user
      Right(user)
    }
  }

  def checkPassword(userId: Int, password: String): Either[ApiError, Unit] =
    passwords.find(_.userId == userId) match {
      case None =>
        Left(ApiError.PasswordIncorrect)

      case Some(pwd) =>
        if (pwd.password == password) {
          Right(())
        } else {
          Left(ApiError.PasswordIncorrect)
        }
    }

  def findAvatar(userId: Int): Either[ApiError, Avatar] =
    avatars
      .find(_.userId == userId)
      .toRight[ApiError](ApiError.AvatarNotFound(userId))
}
