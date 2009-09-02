package edu.colorado.phet.scalacommon

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.PhetPCanvas
import java.awt.Rectangle
import javax.swing.JComponent
import java.awt.geom.{AffineTransform, Rectangle2D}
import umd.cs.piccolo.util.PDimension

//Trying a shorter name to see if it helps resolve this issue:
//http://lampsvn.epfl.ch/trac/scala/ticket/735
class CBS(modelWidth: Double,
          modelHeight: Double,
          canvas: JComponent,
          modelOffsetY: Double)
        extends PhetPCanvas.TransformStrategy() {
  def this(dim: PDimension, canvas: JComponent) = this (dim.width, dim.height, canvas, 0)

  def this(modelWidth: Double, modelHeight: Double, canvas: JComponent) = this (modelWidth, modelHeight, canvas, 0.0)

  def getTransform(): AffineTransform = {
    if (canvas.getWidth > 0 && canvas.getHeight > 0) {
      val mv2d = getModelViewTransform2D
      //println("model dim=" + modelWidth + "x" + modelHeight + ", visible=" + getVisibleModelBounds)
      mv2d.getAffineTransform
    } else {
      new AffineTransform
    }
  }

  def sx = canvas.getWidth / modelWidth

  def sy = canvas.getHeight / modelHeight

  def getScale = {
    val preferredScale = if (sx < sy) sx else sy
    if (preferredScale <= 0) 1.0 else preferredScale
  }

  def getModelViewTransform2D: ModelViewTransform2D = {
    //use the smaller
    var scale = getScale
    scale = if (scale <= 0) sy else scale //if scale is negative or zero, just use scale=sy as a default
    val outputBox =
    if (scale == sx)
      new Rectangle2D.Double(0, (canvas.getHeight - canvas.getWidth) / 2.0, canvas.getWidth, canvas.getWidth)
    else
      new Rectangle2D.Double((canvas.getWidth - canvas.getHeight) / 2.0, 0, canvas.getHeight, canvas.getHeight)
    new ModelViewTransform2D(new Rectangle2D.Double(0, modelOffsetY, modelWidth, modelHeight), outputBox, false)
  }

  def getVisibleModelBounds = getModelViewTransform2D.getAffineTransform.createInverse.createTransformedShape(new Rectangle(canvas.getWidth, canvas.getHeight)).getBounds2D
}