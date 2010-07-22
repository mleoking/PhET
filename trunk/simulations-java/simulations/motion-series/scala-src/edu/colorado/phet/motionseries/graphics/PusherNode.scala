package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import edu.colorado.phet.scalacommon.Predef._
import java.lang.Math._
import edu.colorado.phet.motionseries.MotionSeriesResources
import edu.colorado.phet.motionseries.model.{ForceBead, Bead}

class PusherNode(transform: ModelViewTransform2D, targetBead: ForceBead, manBead: Bead)
        extends BeadNode(manBead, transform, "standing-man.png".literal) {
  defineInvokeAndPass(targetBead.addListenerByName) {
    if (targetBead.appliedForce.magnitude > 0) {

      //todo: use actual bead widths here
      val dx = 2.2 * (if (targetBead.appliedForce.x > 0) -1 else 1)
      manBead.setPosition(targetBead.position + dx)

      //images go 0 to 14
      var leanAmount = (abs(targetBead.appliedForce.x) * 13.0 / 50.0).toInt + 1
      if (leanAmount > 14) leanAmount = 14
      var textStr = leanAmount.toString
      while (textStr.length < 2)
        textStr = "0".literal + textStr
      val im = MotionSeriesResources.getImage("pusher-leaner-png/pusher-leaning-2_00".literal + textStr + ".png".literal)
      val realIm = if (dx > 0) BufferedImageUtils.flipX(im) else im //todo: cache instead of flipping each time
      setImages(realIm, realIm)
      super.update()
    }
    else {
      val image = MotionSeriesResources.getImage("standing-man.png".literal)
      setImages(image, image)
      super.update()
    }
  }
  setPickable(false)
  setChildrenPickable(false)
}

class RobotPusherNode(transform: ModelViewTransform2D, targetBead: ForceBead, manBead: Bead)
        extends BeadNode(manBead, transform, "robotmovingcompany/robot.gif".literal) {
  defineInvokeAndPass(targetBead.addListenerByName) {
    if (targetBead.appliedForce.magnitude > 0) {
      val dx = 1.3 * (if (targetBead.appliedForce.x > 0) -1 else 1)
      manBead.setPosition(targetBead.position + dx)

      val im = MotionSeriesResources.getImage("robotmovingcompany/robot.gif".literal)
      val realIm = if (dx > 0) BufferedImageUtils.flipX(im) else im //todo: cache instead of flipping each time
      setImages(realIm, realIm)
    }
  }
  setPickable(false)
  setChildrenPickable(false)
}