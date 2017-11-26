package fadeddata

import io.circe._, syntax._, generic.JsonCodec, parser._
import touchosc._
import max._

object Main {
  val template = parse("""
{
  "appversion" :  {
    "major" : 7,
    "minor" : 3,
    "revision" : 4,
    "architecture" : "x86",
    "modernui" : 1
  }
}
  """).getOrElse(Json.obj())

  def main(args: Array[String]) = {

    val xmlDoc = scala.xml.XML.loadFile(args.lift(0).getOrElse(sys.error("need path")))

    val (maxObjects, maxLines) = Convert.pageToMax((xmlDoc \\ "tabpage").flatMap(n => Page(n)).head)

    val maxJson = Json.obj(
      "boxes" -> maxObjects.map(MaxObject.asJson).asJson,
      "lines" -> maxLines.map(_.asJson).asJson
    )
    
    println(maxJson.deepMerge(template))
  }
}