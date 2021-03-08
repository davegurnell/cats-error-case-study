package code.maps

sealed abstract class LayerId(val id: String, val underlyingId: String) extends Product with Serializable

object LayerId {
  case object Morph extends LayerId("morph", "mrsMorph")
  case object Riverfly extends LayerId("riverfly", "riverflyHistory")
  case object UrbanRiverfly extends LayerId("urban-riverfly", "urbanRiverflyHistory")

  val values = List(Morph, Riverfly, UrbanRiverfly)
}
