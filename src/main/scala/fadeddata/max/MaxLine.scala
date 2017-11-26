package fadeddata
package max

import io.circe._, io.circe.syntax._

case class MaxLine(source: String, destination: String, sourceOutlet: Int = 0, destinationInlet: Int = 0)

object MaxLine {
  implicit val encodeMaxLine: Encoder[MaxLine] = new Encoder[MaxLine] {
    final def apply(d: MaxLine): Json = 
   	  Json.obj("patchline" -> 
        Json.obj(
          "source" -> Json.arr(d.source.asJson, d.sourceOutlet.asJson),
          "destination" -> Json.arr(d.destination.asJson, d.destinationInlet.asJson)
        )
    )
  }
}

