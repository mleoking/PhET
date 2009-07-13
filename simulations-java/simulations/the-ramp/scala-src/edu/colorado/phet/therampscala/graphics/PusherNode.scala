package edu.colorado.phet.therampscala.graphics


import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.BufferedImageUtils
import model.{Bead}
import scalacommon.Predef._
import java.lang.Math._

class PusherNode(transform: ModelViewTransform2D, targetBead: Bead, manBead: Bead)
        extends BeadNode(manBead, transform, "standing-man.png") {
  defineInvokeAndPass(targetBead.addListenerByName) {
    if (targetBead.appliedForce.magnitude > 0) {

      //todo: use actual bead widths here
      val dx = 2.2 * (if (targetBead.appliedForce.x > 0) -1 else 1)
      manBead.setPosition(targetBead.position + dx)

      //images go 0 to 14
      var leanAmount = (abs(targetBead.appliedForce.x) * 13.0 / 50.0).toInt + 1
      if (leanAmount > 14) leanAmount = 14
      var textStr = "" + leanAmount
      while (textStr.length < 2)
        textStr = "0" + textStr
      val im = RampResources.getImage("pusher-leaner-png/pusher-leaning-2_00" + textStr + ".png")
      val realIm = if (dx > 0) BufferedImageUtils.flipX(im) else im //todo: cache instead of flipping each time
      setImage(realIm)
      super.update()
    }
    else {
      val image = RampResources.getImage("standing-man.png")
      setImage(image)
      super.update()
    }
  }
  setPickable(false)
  setChildrenPickable(false)
}

class RobotPusherNode(transform: ModelViewTransform2D, targetBead: Bead, manBead: Bead)
        extends BeadNode(manBead, transform, "robotmovingcompany/robot.gif") {
  defineInvokeAndPass(targetBead.addListenerByName) {
    if (targetBead.appliedForce.magnitude > 0) {
      val dx = 1.3 * (if (targetBead.appliedForce.x > 0) -1 else 1)
      manBead.setPosition(targetBead.position + dx)

      val im = RampResources.getImage("robotmovingcompany/robot.gif")
      val realIm = if (dx > 0) BufferedImageUtils.flipX(im) else im //todo: cache instead of flipping each time
      setImage(realIm)
    }
  }
  setPickable(false)
  setChildrenPickable(false)
}