package code.maps

import code.maps.geom.{Box, FeatureCollection}
import sttp.client3.playJson._
import sttp.client3.quick._

import java.time._

case class DateRange(from: LocalDate, to: LocalDate)

object MapApi {
  implicit class LocalDateOps(date: LocalDate) {
    def stringify: String =
      List(
        date.getYear.formatted("%04d"),
        date.getMonth.getValue.formatted("%02d"),
        date.getDayOfMonth.formatted("%02d")
      ).mkString("-")
  }

  def query(
      layerId: LayerId,
      bounds: Option[Box] = None,
      dates: Option[DateRange] = None
  ): Either[String, FeatureCollection] = {
    val uri = uri"https://api.beta.cartographer.io/v1/map/${layerId.underlyingId}"
      .addParam("sw", bounds.map(_.sw.stringify))
      .addParam("ne", bounds.map(_.ne.stringify))
      .addParam("from", dates.map(_.from.stringify))
      .addParam("to", dates.map(_.to.stringify))

    val response = quickRequest
      .get(uri)
      .response(asJson[FeatureCollection])
      .send(backend)

    response.body.left.map(_.getMessage)
  }
}
