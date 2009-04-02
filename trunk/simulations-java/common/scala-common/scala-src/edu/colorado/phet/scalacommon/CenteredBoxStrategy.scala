package edu.colorado.phet.scalacommon

import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import javax.swing.JComponent
import _root_.edu.colorado.phet.common.piccolophet.PhetPCanvas.TransformStrategy
import java.awt.geom.{AffineTransform, Rectangle2D}

//Sometimes causes "does not have a constructor" compiler errors; I'm not sure why.
//Renaming it and renaming it back seems to resolve the problem when it occurs.
//Probably a bug with the IntelliJ Plugin
//or maybe http://lampsvn.epfl.ch/trac/scala/ticket/735

class CenteredBoxStrategy(modelWidth: Double, modelHeight: Double, canvas: JComponent) extends TransformStrategy {
//  def this()=this(3,4,null)//workaround for no constructor found problem, toggle this line instead of renaming class
  def getTransform(): AffineTransform = {
    if (canvas.getWidth > 0 && canvas.getHeight > 0) {
      val sx = canvas.getWidth / modelWidth
      val sy = canvas.getHeight / modelHeight

      //use the smaller
      var scale = if (sx < sy) sx else sy
      scale = if (scale <= 0) sy else scale //if scale is negative or zero, just use scale=sy as a default
      val outputBox =
      if (scale == sx)
        new Rectangle2D.Double(0, (canvas.getHeight - canvas.getWidth) / 2.0, canvas.getWidth, canvas.getWidth)
      else
        new Rectangle2D.Double((canvas.getWidth - canvas.getHeight) / 2.0, 0, canvas.getHeight, canvas.getHeight)
      new ModelViewTransform2D(new Rectangle2D.Double(0, 0, modelWidth, modelHeight), outputBox, false).getAffineTransform
    } else {
      new AffineTransform
    }
  }
}