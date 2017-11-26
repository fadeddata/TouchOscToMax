package fadeddata
package touchosc

import scala.xml.Node
import scala.util.Try

sealed trait Control {
  val node: Node
  val name: String = node.getName.get
  val attributes: ControlAttributes = ControlAttributes(node)
  val _type: String
}

object Control {
  def apply(node: Node): Option[Control] =
    node.attribute("type").map(n => n.text).collect {
      case "rotaryh" => RotaryH(node)
      case "rotaryv" => RotaryV(node)
      case "faderh" => FaderH(node)
      case "faderv" => FaderV(node)
      case "multitoggle" => Multitoggle(node)
      case "labelv" => LabelV(node)
  }
}

case class ControlAttributes(node: Node) {
  val color = node.attrMap("color")(n => n.text).get
  val x = node.attrFlatMap("x")(n => Try(n.text.toLong).toOption).get
  val y = node.attrFlatMap("y")(n => Try(n.text.toLong).toOption).get
  val width = node.attrFlatMap("w")(n => Try(n.text.toLong).toOption).get
  val height = node.attrFlatMap("h")(n => Try(n.text.toLong).toOption).get
}

sealed trait Variable extends Control {
  val centered: Boolean = node.attrMap("centered")(n => n.text == "true").get
  val scaleF: Double = node.attrFlatMap("scalef")(n => Try(n.text.toDouble).toOption).get
  val scaleT: Double = node.attrFlatMap("scalet")(n => Try(n.text.toDouble).toOption).get
}

case class RotaryH(node: Node) extends Variable {
  val _type = "rotaryh"
}

case class RotaryV(node: Node) extends Variable {
  val _type = "rotaryv"
}

case class FaderH(node: Node) extends Variable {
  val _type = "faderh"
}

case class FaderV(node: Node) extends Variable {
  val _type = "faderv"
}

case class LabelV(node: Node) extends Control {
  val text: String = node.attrMap("text")(n => n.text).get
  val _type = "labelv"
}

case class LabelH(node: Node) extends Control {
  val text: String = node.attrMap("text")(n => n.text).get
  val _type = "labelh"
}

case class Multitoggle(node: Node) extends Control {
  val numberX: Int = node.attrFlatMap("number_x")(n => Try(n.text.toInt).toOption).get
  val numberY: Int = node.attrFlatMap("number_y")(n => Try(n.text.toInt).toOption).get
  val _type = "multitoggle"
}

