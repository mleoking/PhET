package edu.colorado.phet.scalacommon

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.Rectangle
import javax.swing.JComponent
import java.awt.geom.{AffineTransform, Rectangle2D}
import edu.umd.cs.piccolo.util.PDimension

//Trying a shorter name to see if it helps resolve this issue:
//http://lampsvn.epfl.ch/trac/scala/ticket/735
//now the error reads:
/*
Information:Compilation completed with 2 errors and 0 warnings
Information:2 errors
Information:0 warnings
Error: error while loading CenteredBoxStrategy, Missing dependency 'class edu.colorado.phet.common.piccolophet.PhetPCanvas$TransformStrategy', required by C:\workingcopy\phet\out-test\production\phet\edu\colorado\phet\scalacommon\CenteredBoxStrategy.class
C:\workingcopy\phet\svn\trunk\simulations-java\simulations\force-law-lab\scala-src\edu\colorado\phet\forcelawlab\DefaultCanvas.scala
    Error:Error:line (14)error: edu.colorado.phet.scalacommon.CenteredBoxStrategy does not have a constructor
val centeredBoxStrategy = new CenteredBoxStrategy(768, 768, this)

New error message with 2.8 preview:
Error: error while loading CenteredBoxStrategy, class file 'C:\workingcopy\phet\svn\trunk\out\production\phet\edu\colorado\phet\scalacommon\CenteredBoxStrategy.class' is broken
Error:(class edu.colorado.phet.common.piccolophet.PhetPCanvas$TransformStrategy not found.)
C:\workingcopy\phet\svn\trunk\simulations-java\simulations\motion-series\scala-src\edu\colorado\phet\motionseries\graphics\DefaultCanvas.scala
    Error:Error:line (18)error: edu.colorado.phet.scalacommon.CenteredBoxStrategy does not have a constructor
val centeredBoxStrategy = new CenteredBoxStrategy(canonicalBounds.width, canonicalBounds.height, this, modelOffsetY)
 */
class CenteredBoxStrategy(modelWidth: Double,
                          modelHeight: Double,
                          canvas: JComponent,
                          modelOffsetY: Double)
        extends PhetPCanvas.TransformStrategy {
  def this(dim: PDimension, canvas: JComponent) = this (dim.width, dim.height, canvas, 0)

  def this(modelWidth: Double, modelHeight: Double, canvas: JComponent) = this (modelWidth, modelHeight, canvas, 0.0)

  def getTransform(): AffineTransform = {
    if ( canvas.getWidth > 0 && canvas.getHeight > 0 ) {
      val mv2d = getModelViewTransform2D
      //println("model dim=" + modelWidth + "x" + modelHeight + ", visible=" + getVisibleModelBounds)
      mv2d.getAffineTransform
    }
    else {
      new AffineTransform
    }
  }

  def sx = canvas.getWidth / modelWidth

  def sy = canvas.getHeight / modelHeight

  def getScale = {
    val preferredScale = if ( sx < sy ) {
      sx
    }
    else {
      sy
    }
    if ( preferredScale <= 0 ) {
      1.0
    }
    else {
      preferredScale
    }
  }

  def getModelViewTransform2D: ModelViewTransform2D = {
    //use the smaller
    var scale = getScale
    scale = if ( scale <= 0 ) {
      sy
    }
    else {
      scale
    } //if scale is negative or zero, just use scale=sy as a default
    val outputBox =
      if ( scale == sx ) {
        new Rectangle2D.Double(0, ( canvas.getHeight - canvas.getWidth ) / 2.0, canvas.getWidth, canvas.getWidth)
      }
      else {
        new Rectangle2D.Double(( canvas.getWidth - canvas.getHeight ) / 2.0, 0, canvas.getHeight, canvas.getHeight)
      }
    new ModelViewTransform2D(new Rectangle2D.Double(0, modelOffsetY, modelWidth, modelHeight), outputBox, false)
  }

  def getVisibleModelBounds = getModelViewTransform2D.getAffineTransform.createInverse.createTransformedShape(new Rectangle(canvas.getWidth, canvas.getHeight)).getBounds2D
}