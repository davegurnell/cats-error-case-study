package code

import cats.Show
import cats.implicits._
import code.syntax._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import unindent._

case class Point(x: Double, y: Double) {
  def toPair: (Double, Double) =
    (x, y)

  def stringify: String =
    s"$y,$x"
}

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

case class Box(sw: Point, ne: Point)

case class Feature(id: Option[String], geometry: Point, properties: Map[String, JsValue]) {
  def prop(name: String): Either[String, JsValue] =
    properties.get(name).toRight(s"Property not found: ${name}")

  def propAs[A: Reads](name: String): Either[String, A] =
    prop(name).flatMap(_.validate[A].asOpt.toRight(s"Property was not the correct type: $name"))
}

object Feature {
  implicit val format: OFormat[Feature] =
    (
      (__ \ "id").formatNullable[String] ~
        (__ \ "geometry").format[Point] ~
        (__ \ "properties").format[Map[String, JsValue]]
    )(Feature.apply, unlift(Feature.unapply))

  implicit val show: Show[Feature] =
    Show.show { feat =>
      import scala.io.AnsiColor._

      i"""
      ${BOLD}Feature:${RESET}
        ${BOLD}id:${RESET} ${feat.id.getOrElse("<none>")}
        ${BOLD}geometry:${RESET}
          ${feat.geometry.show.indent.indent}
        ${BOLD}properties:${RESET}
          ${feat.properties.toList
        .sortBy(_._1)
        .map { case (name, value) => s"${BOLD}${name}:${RESET} ${Json.stringify(value)}" }
        .mkString("\n")
        .indent
        .indent}
      """
    }
}

case class FeatureCollection(features: Vector[Feature])

object FeatureCollection {
  implicit val format: OFormat[FeatureCollection] =
    (__ \ "features").format[Vector[Feature]].inmap(FeatureCollection.apply, _.features)

  implicit val show: Show[FeatureCollection] =
    Show.show { coll =>
      import scala.io.AnsiColor._

      i"""
      ${BOLD}Feature Collection:${RESET}
        ${coll.features.map(_.show).mkString("\n").indent}
      """
    }
}
