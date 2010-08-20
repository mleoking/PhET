package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import edu.colorado.phet.scalacommon.Predef._
import java.lang.Math._
import edu.colorado.phet.common.phetcommon.math.MathUtil
import edu.colorado.phet.motionseries.MotionSeriesResources
import edu.colorado.phet.motionseries.model.{ForceMotionSeriesObject, MotionSeriesObject}


class PusherNode(transform: ModelViewTransform2D, targetObject: ForceMotionSeriesObject, manObject: MotionSeriesObject)
        extends MotionSeriesObjectNode(manObject, transform, "standing-man.png".literal) {
  defineInvokeAndPass(targetObject.addListenerByName) {
    if (targetObject.appliedForce.magnitude > 0) {

      //todo: use actual object widths here
      val dx = 2.2 * (if (targetObject.appliedForce.x > 0) -1 else 1)
      manObject.setPosition(targetObject.position + dx)

      //images go 1 to 14
      val leanAmount = MathUtil.clamp(1,(abs(targetObject.appliedForce.x) * 13.0 / 50.0).toInt + 1,14).toInt
      
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

class RobotPusherNode(transform: ModelViewTransform2D, targetObject: ForceMotionSeriesObject, man: MotionSeriesObject)
        extends MotionSeriesObjectNode(man, transform, "robotmovingcompany/robot.gif".literal) {
  defineInvokeAndPass(targetObject.addListenerByName) {
    if (targetObject.appliedForce.magnitude > 0) {
      val dx = 1.3 * (if (targetObject.appliedForce.x > 0) -1 else 1)
      man.setPosition(targetObject.position + dx)

      val im = MotionSeriesResources.getImage("robotmovingcompany/robot.gif".literal)
      val realIm = if (dx > 0) BufferedImageUtils.flipX(im) else im //todo: cache instead of flipping each time
      setImages(realIm, realIm)
    }
  }
  setPickable(false)
  setChildrenPickable(false)
}