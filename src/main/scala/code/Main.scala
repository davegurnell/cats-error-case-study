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
          |Oh no! Something went wrong!
          |
          |$error
          |
          |Usage:
          |
          |sbt run search <layerId>
          |sbt run count <layerId>
          |
          |Where:
          |
          |<layerId> is one of ${LayerId.values.map(_.id).mkString(" or ")}
          """.trim.stripMargin
      }
    }
}
