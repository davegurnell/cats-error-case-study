package code

object syntax {
  implicit class StringOps(str: String) {
    def indent: String =
      str.replaceAll("\n", "\n  ")
  }
}
