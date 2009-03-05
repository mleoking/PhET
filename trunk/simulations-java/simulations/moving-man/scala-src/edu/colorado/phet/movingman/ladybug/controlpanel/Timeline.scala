package edu.colorado.phet.movingman.ladybug.controlpanel

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader._
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloTimeControlPanel.BackgroundNode
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt._
import edu.colorado.phet.common.piccolophet.event.ToolTipHandler
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.DefaultIconButton
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PlayPauseButton
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.RewindButton
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.StepButton
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import scala.collection.mutable.ArrayBuffer
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources
import edu.colorado.phet.common.phetcommon.resources.PhetResources
import java.awt.event.{ActionEvent, ComponentAdapter, ComponentEvent, ActionListener}

import java.awt.geom.{Line2D, Ellipse2D}
import java.util.{Hashtable, Dictionary}
import javax.swing._
import model.LadybugModel
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.{PImage, PText}
import umd.cs.piccolo.PNode
import umd.cs.piccolo.util.PBounds
import umd.cs.piccolox.pswing.PSwing
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloTimeControlPanel.BackgroundNode
import edu.colorado.phet.scalacommon.Predef._


import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}

class Timeline(model: LadybugModel, canvas: PhetPCanvas) extends PNode {
  val pathOffsetY = 4
  val pathHeight = 6
  val ellipseWidth = 10
  val ellipseHeight = 8
  val insetX = 10
  val shaded = new PhetPPath(LadybugColorSet.position)
  val backgroundColor = new Color(190, 195, 195)

  def darker(c: Color, del: Int) = {
    new Color(c.getRed - del, c.getGreen - del, c.getBlue - del)
  }

  val background = new PhetPPath(backgroundColor) {
    val topShade = new PhetPPath(new BasicStroke(2), darker(backgroundColor, 55))
    addChild(topShade)
    val bottomShade = new PhetPPath(new BasicStroke(1), darker(backgroundColor, 20))
    addChild(bottomShade)
    val leftShade = new PhetPPath(new BasicStroke(2), darker(backgroundColor, 50))
    addChild(leftShade)
    val rightShade = new PhetPPath(new BasicStroke(1), darker(backgroundColor, 20))
    addChild(rightShade)
    override def setPathTo(aShape: Shape) = {
      super.setPathTo(aShape)
      val b = aShape.getBounds2D
      topShade.setPathTo(new Line2D.Double(b.getX, b.getY, b.getMaxX, b.getY))
      bottomShade.setPathTo(new Line2D.Double(b.getX, b.getMaxY, b.getMaxX, b.getMaxY))
      leftShade.setPathTo(new Line2D.Double(b.getX, b.getY, b.getX, b.getMaxY))
      rightShade.setPathTo(new Line2D.Double(b.getMaxX, b.getY, b.getMaxX, b.getMaxY))
    }
  }
  //  val handle = new PhetPPath(Color.blue, new BasicStroke(1), Color.darkGray)
  val img = loadBufferedImage("piccolo-phet/images/button-template.png")
  val scaledImage = BufferedImageUtils.getScaledInstance(img, 20, 10, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true)
  val handle = new PImage(scaledImage)
  var scale = 1.0
  addChild(background)
  addChild(shaded)
  addChild(handle)

  canvas.addComponentListener(new ComponentAdapter() {
    override def componentResized(e: ComponentEvent) = {updateSelf()}
  })

  handle.addInputEventListener(new CursorHandler)
  handle.addInputEventListener(new PBasicInputEventHandler() {
    override def mouseDragged(event: PInputEvent) = {
      model.setPaused(true)
      val dx = event.getCanvasDelta.width
      val t = model.getTime + dx / scale
      model.setPlaybackTime(((model.getFloatTime + dx / scale) max model.getMinRecordedTime) min (model.getMaxRecordedTime))
    }
  })

  model.addListenerByName(updateSelf())
  updateSelf
  def updateSelf() = {
    scale = (canvas.getWidth - insetX * 2) / LadybugDefaults.timelineLengthSeconds

    shaded.setPathTo(new Rectangle(insetX, pathOffsetY + 1, (model.getRecordedTimeRange * scale).toInt, pathHeight - 1))
    background.setPathTo(new Rectangle(insetX, pathOffsetY, (LadybugDefaults.timelineLengthSeconds * scale).toInt, pathHeight))
    handle.setVisible(model.isPlayback)
    val elapsed = model.getTime - model.getMinRecordedTime
    //    handle.setPathTo(new Ellipse2D.Double(elapsed * scale - ellipseWidth / 2 + insetX, pathOffsetY - 1, ellipseWidth, ellipseHeight))
    handle.setOffset(elapsed * scale - handle.getFullBounds.getWidth / 2 + insetX, pathOffsetY - 2)
  }
}