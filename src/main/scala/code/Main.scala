package code

import cats.implicits._
import java.time._
import play.api.libs.json._
import sttp.client3.quick._

object Main {
  def main(args: Array[String]): Unit =
    printOutput {
      args.toList match {
        case "search" :: layerId :: Nil             => search(layerId, None, None)
        case "search" :: layerId :: sw :: ne :: Nil => search(layerId, Some(sw), Some(ne))
        case "count" :: layerId :: Nil              => count(layerId, None, None)
        case "count" :: layerId :: sw :: ne :: Nil  => count(layerId, Some(sw), Some(ne))
        case _                                      => Left("Wrong number of parameters")
      }
    }

  def search(layerId: String): Either[String, String] =
    for {
      layerId <- parseLayerId(layerId)
      coll <- MapApi.query(layerId)
    } yield coll.show

  def count(layerId: String): Either[String, String] =
    for {
      layerId <- parseLayerId(layerId)
      coll <- MapApi.query(layerId)
    } yield coll.features.length.toString

  def parseLayerId(id: String): Either[String, LayerId] =
    LayerId.values.find(_.id == id).toRight("Layer ID not found")

  def printOutput(output: Either[String, String]): Unit =
    println {
      output match {
        case Right(output) =>
          output

        case Left(error) =>
          s"""
          |$error
          |
          |Usage:
          |
          |  sbt run search  <layerId>          [sw ne]
          |  sbt run count   <layerId>          [sw ne]
          |  sbt run total   <layerId> <propId> [sw ne]
          |  sbt run average <layerId> <propId> [sw ne]
          |
          |Where:
          |  layerId is a layer ID: ${LayerId.values.map(_.id).mkString(" or ")}
          |  propId is a property ID (an arbitrary string)
          |  sw,ne are GPS positions in the form "longitude,latitude"
          |
          |Examples:
          |
          |   sbt run riverfly
          |   sbt run morph -1.49 1,51
          """.trim.stripMargin
      }
    }
}
