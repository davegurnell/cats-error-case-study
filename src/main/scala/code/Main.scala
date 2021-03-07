package code

import cats.data.{EitherNel, NonEmptyList}
import cats.implicits._
import java.time._
import play.api.libs.json._
import sttp.client3.quick._
import unindent._

object Main {
  def main(args: Array[String]): Unit =
    printOutput {
      args.toList match {
        case "search" :: layerId :: Nil                        => search(layerId, None, None)
        case "search" :: layerId :: sw :: ne :: Nil            => search(layerId, Some(sw), Some(ne))
        case "count" :: layerId :: Nil                         => count(layerId, None, None)
        case "count" :: layerId :: sw :: ne :: Nil             => count(layerId, Some(sw), Some(ne))
        case "total" :: layerId :: propId :: Nil               => total(layerId, propId, None, None)
        case "total" :: layerId :: propId :: sw :: ne :: Nil   => total(layerId, propId, Some(sw), Some(ne))
        case "average" :: layerId :: propId :: Nil             => average(layerId, propId, None, None)
        case "average" :: layerId :: propId :: sw :: ne :: Nil => average(layerId, propId, Some(sw), Some(ne))
        case _                                                 => Left(NonEmptyList.of("Command not found or wrong number of parameters"))
      }
    }

  def search(layerId: String, sw: Option[String], ne: Option[String]): EitherNel[String, String] =
    for {
      params <- parseParams(layerId, sw, ne)
      (layerId, bounds) = params
      coll <- MapApi.query(layerId, bounds)
    } yield coll.show

  def count(layerId: String, sw: Option[String], ne: Option[String]): EitherNel[String, String] =
    for {
      params <- parseParams(layerId, sw, ne)
      (layerId, bounds) = params
      coll <- MapApi.query(layerId, bounds)
    } yield coll.features.length.toString

  def total(layerId: String, propId: String, sw: Option[String], ne: Option[String]): EitherNel[String, String] =
    for {
      params <- parseParams(layerId, sw, ne)
      (layerId, bounds) = params
      coll <- MapApi.query(layerId, bounds)
      props <- coll.features.parTraverse(_.propAs[Double](propId))
    } yield props.sum.show

  def average(layerId: String, propId: String, sw: Option[String], ne: Option[String]): EitherNel[String, String] =
    for {
      params <- parseParams(layerId, sw, ne)
      (layerId, bounds) = params
      coll <- MapApi.query(layerId, bounds)
      props <- coll.features.parTraverse(_.propAs[Double](propId))
    } yield (props.sum / props.length).show

  def parseParams(layerId: String, sw: Option[String], ne: Option[String]): EitherNel[String, (LayerId, Option[Box])] =
    (parseLayerId(layerId), parseOptBounds(sw, ne)).parTupled

  def parseLayerId(layerId: String): EitherNel[String, LayerId] =
    LayerId.values.find(_.id == layerId).toRight(NonEmptyList.of(s"Layer not found: $layerId"))

  def parseOptBounds(sw: Option[String], ne: Option[String]): EitherNel[String, Option[Box]] =
    (sw, ne).mapN(parseBounds).sequence

  def parseBounds(sw: String, ne: String): EitherNel[String, Box] =
    (parsePoint(sw), parsePoint(ne)).parMapN(Box)

  def parsePoint(gps: String): EitherNel[String, Point] =
    gps.split(",").toList match {
      case x :: y :: Nil =>
        (
          x.toDoubleOption.toRight(NonEmptyList.of(s"Invalid longitude: $x")),
          y.toDoubleOption.toRight(NonEmptyList.of(s"Invalid latitude: $y"))
        ).mapN(Point(_, _))

      case _ =>
        Left(NonEmptyList.of(s"Invalid GPS: $gps"))
    }

  def printOutput(output: EitherNel[String, String]): Unit =
    println {
      output match {
        case Right(output) =>
          output

        case Left(errors) =>
          i"""
          Oh no! One or more things went wrong!

          ${errors.toList.mkString("\n")}

          Usage:

            sbt run search  <layerId>          [<sw> <ne>]
            sbt run count   <layerId>          [<sw> <ne>]
            sbt run total   <layerId> <propId> [<sw> <ne>]
            sbt run average <layerId> <propId> [<sw> <ne>]

          Where:

            <layerId> is a layer ID: ${LayerId.values.map(_.id).mkString(" or ")}
            <propId> is a property ID (an arbitrary string)
            <sw> and <ne> are GPS positions of the form "longitude,latitude"

          Examples:

             sbt run riverfly
             sbt count morph -1.49 1,51
             sbt average morph index8 -1.49 1,51
          """
      }
    }
}
