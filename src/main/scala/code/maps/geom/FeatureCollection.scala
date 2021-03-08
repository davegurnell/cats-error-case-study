package code.maps.geom

import cats.Show
import cats.implicits._
import code.maps.geom.syntax._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import unindent._

case class FeatureCollection(features: Vector[Feature])

//noinspection RedundantBlock
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