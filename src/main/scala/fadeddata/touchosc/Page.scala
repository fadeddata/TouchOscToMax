package fadeddata
package touchosc

import scala.xml.Node

case class Page(name: String, controls: List[Control])

object Page {
  def apply(node: Node): Option[Page] = {
    node.getName.map { name =>
      Page(name, node.child.flatMap(n => Control(n)).toList)
    }
  }
}