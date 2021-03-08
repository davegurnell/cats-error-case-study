package code.maps.geom

import cats.Show
import cats.implicits._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import unindent._

case class Point(x: Double, y: Double) {
  def toPair: (Double, Double) =
    (x, y)

  def stringify: String =
    s"$y,$x"
}

//noinspection RedundantBlock
object Point {
  implicit val format: OFormat[Point] =
    (__ \ "coordinates").format[(Double, Double)].inmap((apply _).tupled, _.toPair)

  implicit val show: Show[Point] =
    Show.show { point =>
      import scala.io.AnsiColor._

      i"""
      ${BOLD}lng:${RESET} ${point.x}
      ${BOLD}lat:${RESET} ${point.y}
      """
    }
}