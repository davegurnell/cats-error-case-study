package code.maps.geom

object syntax {

  implicit class StringOps(str: String) {
    def indent: String =
      str.replaceAll("\n", "\n  ")
  }

}
