package fadeddata

import fadeddata.touchosc._
import fadeddata.max._

object Convert {
  def pageToMax(page: Page): (List[MaxObject], List[MaxLine]) = {
    val pageName = page.name
    val routerName = s"router-$pageName"
    val prependerName = s"prepender-$pageName"

    val (maxObjects, maxLines) = page.controls.foldLeft((List.empty[MaxObject], List.empty[MaxLine])) { case ((objs, lines), control) => 
      val (newObjs, newLines) = Convert.controlToMax(routerName, prependerName)(control)
      (objs ++ newObjs, lines ++ newLines)
    }

    val pageObjects = List(
      NewObj(routerName, s"o.route /$pageName", 1, 2, Array("", "FullPacket"), Array(500, 0, 0, 0)),
      NewObj(prependerName, s"o.prepend /$pageName", 1, 1, Array("FullPacket"), Array(500, 900, 0, 0))
    )

    (maxObjects ++ pageObjects, maxLines)
  }

  def maxLines(pageSender: String, pageReceiver: String, controlName: String, routerName: String, packerName: String) =         
    List(
      MaxLine(pageSender, routerName),
      MaxLine(routerName, controlName),
      MaxLine(controlName, packerName),
      MaxLine(packerName, pageReceiver),
    )


  def controlToMax(pageSender: String, pageReceiver: String)(control: Control): (List[MaxObject], List[MaxLine]) = control match {
    case control: Variable => 
      val x = control.attributes.x.toDouble
      val y = control.attributes.y.toDouble
      val controlName = control.name
      val routerName = s"router-$controlName"
      val packerName = s"packer-$controlName"
      val initial = if(control.centered) 0.5 else 0.0
      
      (
        List(
          NewObj(routerName, s"o.route /$controlName", 1, 2, Array("", "FullPacket"), Array(y, x - 22, 0, 0)),
          LiveDial(controlName, control.scaleF, control.scaleT, Some(initial), Array(y, x, 0, 0)),
          NewObj(packerName, s"o.pack /$controlName", 1, 1, Array("FullPacket"), Array(y, x + 44, 0, 0))
        ), 
        maxLines(pageSender, pageReceiver, controlName, routerName, packerName)
      )

    case control: Push =>
      val x = control.attributes.x
      val y = control.attributes.y
      val width = control.attributes.width.toDouble
      val height = control.attributes.height.toDouble 
      val patchingRect = Array(y, x, width, height)
      val controlName = s"${control.name}"
      val routerName = s"router-$controlName"
      val packerName = s"packer-$controlName"

      (
        List(
          NewObj(routerName, s"o.route /$controlName", 1, 2, Array("", "FullPacket"), Array(y, x - 22, 0, 0)),
          LiveButton(controlName, Some(0.0), patchingRect),
          NewObj(packerName, s"o.pack /$controlName", 1, 1, Array("FullPacket"), Array(y, x + 44, 0, 0))
        ), 
        maxLines(pageSender, pageReceiver, controlName, routerName, packerName)
      )


    case control: Toggle =>
      val x = control.attributes.x
      val y = control.attributes.y
      val width = control.attributes.width.toDouble
      val height = control.attributes.height.toDouble 
      val patchingRect = Array(y, x, width, height)
      val controlName = s"${control.name}"
      val routerName = s"router-$controlName"
      val packerName = s"packer-$controlName"

      (
        List(
          NewObj(routerName, s"o.route /$controlName", 1, 2, Array("", "FullPacket"), Array(y, x - 22, 0, 0)),
          LiveToggle(controlName, Some(0.0), patchingRect),
          NewObj(packerName, s"o.pack /$controlName", 1, 1, Array("FullPacket"), Array(y, x + 44, 0, 0))
        ), 
        maxLines(pageSender, pageReceiver, controlName, routerName, packerName)
      )

    case control: Multitoggle =>
      val offsetX = control.attributes.x
      val offsetY = control.attributes.y
      val width = control.attributes.width.toDouble / control.numberX
      val height = control.attributes.height.toDouble / control.numberY

      val objs = (for {
        x <- 1l to control.numberX // rows
        y <- 1l to control.numberY // columns
      } yield {
        val patchingRect = Array(offsetY + (y * width), offsetX + (x * height), width, height)
        val controlName = s"${control.name}-$x-$y"
        val pathName = s"${control.name}/$x/$y"
        val routerName = s"router-$controlName"
        val packerName = s"packer-$controlName"

        List(
          NewObj(routerName, s"o.route /$pathName", 1, 2, Array("", "FullPacket"), patchingRect),
          LiveToggle(controlName, Some(0.0), patchingRect),
          NewObj(packerName, s"o.pack /$pathName", 1, 1, Array("FullPacket"), patchingRect)
        )
      }).toList.flatten

      val lines = (for {
        x <- 1l to control.numberX // rows
        y <- 1l to control.numberY // columns
      } yield {
        val controlName = s"${control.name}-$x-$y"
        val routerName = s"router-$controlName"
        val packerName = s"packer-$controlName"

        maxLines(pageSender, pageReceiver, controlName, routerName, packerName)
      }).toList.flatten

      (objs, lines)
 
    case _ => (List.empty, List.empty)
  }
}