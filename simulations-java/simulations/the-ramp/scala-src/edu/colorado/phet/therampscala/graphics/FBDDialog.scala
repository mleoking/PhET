package edu.colorado.phet.therampscala.graphics

import common.phetcommon.resources.PhetCommonResources
import java.awt.event.{WindowEvent, ComponentAdapter, ComponentEvent, WindowAdapter}
import common.phetcommon.view.util.SwingUtils
import common.piccolophet.PhetPCanvas
import java.awt.geom.Point2D
import javax.swing.{JFrame, JDialog}
import model.{CoordinateSystemModel, CoordinateFrameModel, FreeBodyDiagramModel}
import RampResources._
import scalacommon.Predef._

class FBDDialog(frame: JFrame, freeBodyDiagramModel: FreeBodyDiagramModel, fbdWidth: Double, coordinateFrameModel: CoordinateFrameModel, adjustable: Boolean, coordinateSystemModel: CoordinateSystemModel, fbdListener: Point2D => Unit) {
  val fbdWindow = new JDialog(frame, "display.free-body-diagram".translate, false)
  fbdWindow.setSize(600, 600)

  //create FBD canvas
  val windowFBDNode = new FreeBodyDiagramNode(freeBodyDiagramModel, 600, 600, fbdWidth, fbdWidth, coordinateFrameModel, coordinateSystemModel.adjustable, PhetCommonResources.getImage("buttons/minimizeButton.png".literal))
  windowFBDNode.addListener(fbdListener)
  val windowedFBDCanvas = new PhetPCanvas
  windowedFBDCanvas.addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) = updateNodeSize()
  })
  updateNodeSize()
  def updateNodeSize() = {
    if (windowedFBDCanvas.getWidth > 0 && windowedFBDCanvas.getHeight > 0) {
      val w = Math.min(windowedFBDCanvas.getWidth, windowedFBDCanvas.getHeight)
      val inset = 40
      windowFBDNode.setSize(w - inset * 2, w - inset * 2)
      windowFBDNode.setOffset(inset, inset)
    }
  }
  windowedFBDCanvas.addScreenChild(windowFBDNode)
  fbdWindow.setContentPane(windowedFBDCanvas)

  var initted = false
  defineInvokeAndPass(freeBodyDiagramModel.addListenerByName) {
    val wasVisible = fbdWindow.isVisible
    fbdWindow.setVisible(freeBodyDiagramModel.visible && freeBodyDiagramModel.windowed)
    if (fbdWindow.isVisible && !wasVisible && !initted) {
      initted = true
      SwingUtils.centerDialogInParent(fbdWindow)
    }
    updateNodeSize()
  }
  fbdWindow.addWindowListener(new WindowAdapter {
    override def windowClosing(e: WindowEvent) = {
      if (!freeBodyDiagramModel.closable) {
        freeBodyDiagramModel.windowed = false
      } else {
        freeBodyDiagramModel.visible = false
      }
    }
  })
  def addVector(vector: Vector, maxDistToLabel: Double): Unit = windowFBDNode.addVector(vector, maxDistToLabel)

  def addVector(vector: Vector, offset: VectorValue, maxDistToLabel: Double) = windowFBDNode.addVector(vector, offset, maxDistToLabel)

  def clearVectors() = windowFBDNode.clearVectors()

  def removeVector(vector: Vector) = windowFBDNode.removeVector(vector)
}