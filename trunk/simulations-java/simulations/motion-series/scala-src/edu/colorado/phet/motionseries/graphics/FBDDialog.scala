package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources
import java.awt.event.{WindowEvent, ComponentAdapter, ComponentEvent, WindowAdapter}
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.geom.Point2D
import javax.swing.{JFrame, JDialog}
import edu.colorado.phet.motionseries.model.{AdjustableCoordinateModel, CoordinateFrameModel, FreeBodyDiagramModel}
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.motionseries.MotionSeriesDefaults

class FBDDialog(frame: JFrame,
                freeBodyDiagramModel: FreeBodyDiagramModel,
                fbdWidth: Double,
                coordinateFrameModel: CoordinateFrameModel,
                adjustable: Boolean,
                adjustableCoordinateModel: AdjustableCoordinateModel,
                fbdListener: Point2D => Unit,
                rampAngle: () => Double) extends VectorDisplay {
  def addVector(vector: Vector with PointOfOriginVector, offsetFBD: VectorValue, maxOffset: Int, offsetPlayArea: Double): Unit =
    addVector(vector, offsetFBD, maxOffset)

  val dialog = new JDialog(frame, "display.free-body-diagram".translate, false)
  dialog.setSize(MotionSeriesDefaults.FBD_DIALOG_WIDTH, MotionSeriesDefaults.FBD_DIALOG_HEIGHT)

  val fbdNode = new FreeBodyDiagramNode(freeBodyDiagramModel, MotionSeriesDefaults.FBD_DIALOG_NODE_WIDTH, MotionSeriesDefaults.FBD_DIALOG_NODE_HEIGHT, fbdWidth, fbdWidth, coordinateFrameModel, adjustableCoordinateModel, PhetCommonResources.getImage("buttons/minimizeButton.png".literal), rampAngle)
  fbdNode.addListener(fbdListener)
  val canvas = new PhetPCanvas
  canvas.addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) = updateNodeSize()
  })
  updateNodeSize()
  def updateNodeSize() = {
    if (canvas.getWidth > 0 && canvas.getHeight > 0) {
      val w = Math.min(canvas.getWidth, canvas.getHeight)
      val inset = MotionSeriesDefaults.FBD_INSET
      fbdNode.setSize(w - inset * 2, w - inset * 2)
      fbdNode.setOffset(inset, inset)
    }
  }
  canvas.addScreenChild(fbdNode)
  dialog.setContentPane(canvas)

  private var initted = false
  defineInvokeAndPass(freeBodyDiagramModel.addListenerByName) {
    val wasVisible = dialog.isVisible
    dialog.setVisible(freeBodyDiagramModel.visible && freeBodyDiagramModel.windowed)
    if (dialog.isVisible && !wasVisible && !initted) {
      initted = true
      SwingUtils.centerDialogInParent(dialog)
    }
    updateNodeSize()
  }
  dialog.addWindowListener(new WindowAdapter {
    override def windowClosing(e: WindowEvent) = {
      if (!freeBodyDiagramModel.closable) {
        freeBodyDiagramModel.windowed = false
      } else {
        freeBodyDiagramModel.visible = false
      }
    }
  })
  def addVector(vector: Vector, maxDistToLabel: Double): Unit = fbdNode.addVector(vector, maxDistToLabel)

  def addVector(vector: Vector, offset: VectorValue, maxDistToLabel: Double) = fbdNode.addVector(vector, offset, maxDistToLabel)

  def clearVectors() = fbdNode.clearVectors()

  def removeVector(vector: Vector) = fbdNode.removeVector(vector)
}