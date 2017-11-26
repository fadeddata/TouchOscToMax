package fadeddata
package max

import io.circe._, io.circe.syntax._

object MaxObject {
  def asJson(maxObject: MaxObject): Json = maxObject match {
    case dial: LiveDial => dial.asJson
    case grid: LiveGrid => grid.asJson
    case toggle: LiveToggle => toggle.asJson
    case newObj: NewObj => newObj.asJson
    case _ => Json.Null
  }

  def savedAttributes(obj: MaxObject, merge: Json): Json = {
    val json = Json.obj(
        "parameter_longname" -> obj.id.asJson,
        "parameter_shortname" -> obj.id.asJson,
      ).deepMerge(merge)
    
    Json.obj(
      "saved_attribute_attributes" -> Json.obj("valueof" -> json)
    )
  }

  def boxProperties(obj: MaxObject, merge: Json = Json.obj()): Json =
    Json.obj("box" ->
      Json.obj(
          "id" -> obj.id.asJson,
          "varname" -> obj.id.asJson,
          "maxclass" -> obj.maxClass.asJson,
          "numinlets" -> obj.numInlets.asJson,
          "numoutlets" -> obj.numOutlets.asJson,
          "outlettype" -> obj.outletType.asJson,
          "presentation" -> 0.asJson,
          "presentation_rect" -> obj.patchingRect.asJson,
          "patching_rect" -> obj.patchingRect.asJson
        ).deepMerge(merge)
    )
}

sealed trait MaxObject {
  val id: String
  val maxClass: String
  val numInlets: Int
  val numOutlets: Int
  val outletType: Array[String]
  val patchingRect: Array[Double]
}

case class NewObj(
  id: String, 
  text: String, 
  numInlets: Int, 
  numOutlets: Int, 
  outletType: Array[String], 
  patchingRect: Array[Double]
) extends MaxObject {
  val maxClass: String = "newobj" 
}

object NewObj {
  implicit val encodeNewObj: Encoder[NewObj] = new Encoder[NewObj] {
    final def apply(d: NewObj): Json = {

      MaxObject.boxProperties(d, Json.obj(
        "text" -> d.text.asJson
      ))
    }
  }
}

case class LiveToggle(id: String, initial: Option[Double], patchingRect: Array[Double]) extends MaxObject {
  val maxClass: String = "live.toggle"
  val numInlets: Int = 1
  val numOutlets: Int = 1
  val outletType: Array[String] = Array("")
}

object LiveToggle {
  implicit val encodeLiveToggle: Encoder[LiveToggle] = new Encoder[LiveToggle] {
    final def apply(d: LiveToggle): Json = {
      val attr = MaxObject.savedAttributes(d, Json.obj(
        "parameter_type" -> 2.asJson,
        "parameter_mmax" -> 1.0.asJson,
        "parameter_enum" -> Array("off", "on").asJson,
        "parameter_initial_enable" -> d.initial.isDefined.asJson,
        "parameter_initial" -> d.initial.toArray.asJson
      ))

      MaxObject.boxProperties(d, Json.obj(
        "parameter_enable" -> 1.asJson
      ).deepMerge(attr))
    }
  }
}

case class LiveDial(id: String, mmin: Double, mmax: Double, initial: Option[Double], patchingRect: Array[Double]) extends MaxObject {
  val maxClass: String = "live.dial"
  val numInlets: Int = 1
  val numOutlets: Int = 2
  val outletType: Array[String] = Array("", "float")
}

object LiveDial {
  implicit val encodeLiveDial: Encoder[LiveDial] = new Encoder[LiveDial] {
    final def apply(d: LiveDial): Json = {
      val attr = MaxObject.savedAttributes(d, Json.obj(
        "parameter_type" -> 0.asJson,
        "parameter_unitstyle" -> 0.asJson,
        "parameter_mmin" -> d.mmin.asJson,
        "parameter_mmax" -> d.mmax.asJson,
        "parameter_initial_enable" -> d.initial.isDefined.asJson,
        "parameter_initial" -> d.initial.toArray.asJson
      ))

      MaxObject.boxProperties(d, attr)
    }
  }
}

case class LiveGrid(id: String, rows: Int, columns: Int, patchingRect: Array[Double]) extends MaxObject {
  val maxClass: String = "live.grid"
  val numInlets: Int = 2
  val numOutlets: Int = 6
  val outletType: Array[String] = Array("", "", "", "", "", "")
}

object LiveGrid {
  implicit val encodeLiveGrid: Encoder[LiveGrid] = new Encoder[LiveGrid] {
    final def apply(d: LiveGrid): Json = {
      val attr = MaxObject.savedAttributes(d, Json.obj(
        "parameter_type" -> 3.asJson,
        "parameter_invisible" -> 1.asJson
      ))

      MaxObject.boxProperties(d, Json.obj(
        "mode" -> 1.asJson,
        "rows" -> d.rows.asJson,
        "columns" -> d.columns.asJson,
        "direction" -> 0.asJson
      ).deepMerge(attr))
    }
  }
}

