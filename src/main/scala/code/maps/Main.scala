package code.maps

import cats.implicits._
import unindent._

object Main {
  def main(args: Array[String]): Unit =
    printOutput {
      args.toList match {
        case "search" :: layerId :: Nil => search(layerId)
        case "count" :: layerId :: Nil => count(layerId)
        case _ => Left("Wrong number of parameters")
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
          i"""
          Oh no! Something went wrong!

          $error

          Usage:

          sbt run search
          sbt run count

          Where:

          <layerId> is one of ${LayerId.values.map(_.id).mkString(" or ")}
          """.trim.stripMargin
      }
    }
}
