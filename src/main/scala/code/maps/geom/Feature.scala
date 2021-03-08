package code.maps.geom

import cats.Show
import cats.implicits._
import code.maps.geom.syntax._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import unindent._

//noinspection RedundantBlock
case class Feature(id: Option[String], geometry: Point, properties: Map[String, JsValue]) {
  def prop(name: String): Either[String, JsValue] =
    properties.get(name).toRight(s"Property not found: ${name}")

  def propAs[A: Reads](name: String): Either[String, A] =
    prop(name).flatMap(_.validate[A].asOpt.toRight(s"Property was not the correct type: $name"))
}

//noinspection RedundantBlock
object Feature {
  implicit val format: OFormat[Feature] =
    (
      (__ \ "id").formatNullable[String] ~
        (__ \ "geometry").format[Point] ~
        (__ \ "properties").format[Map[String, JsValue]]
      ) (Feature.apply, unlift(Feature.unapply))

  implicit val show: Show[Feature] =
    Show.show { feat =>
      import scala.io.AnsiColor._

      i"""
      ${BOLD}Feature:${RESET}
        ${BOLD}id:${RESET} ${feat.id.getOrElse("<none>")}
        ${BOLD}geometry:${RESET}
          ${feat.geometry.show.indent.indent}
        ${BOLD}properties:${RESET}
          ${
        feat.properties.toList
          .sortBy(_._1)
          .map { case (name, value) => s"${BOLD}${name}:${RESET} ${Json.stringify(value)}" }
          .mkString("\n")
          .indent
          .indent
      }
      """
    }
}