package fadeddata

import scala.xml.Node
import java.util.Base64
import java.nio.charset.StandardCharsets

package object touchosc {
  val base64Decoder = Base64.getDecoder

  implicit class NodeMapping(node: Node) {
  	def attrMap[T](attr: String)(f: Node => T): Option[T] = {
      node.attribute(attr).flatMap(_.headOption.map(f))
  	}

    def attrFlatMap[T](attr: String)(f: Node => Option[T]): Option[T] = {
      node.attribute(attr).flatMap(_.headOption.flatMap(f))
    }

    def getName: Option[String] = 
      node.attrMap("name")(n => new String(base64Decoder.decode(n.text)))
  }
}