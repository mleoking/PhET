package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources
import java.awt.event.{WindowEvent, ComponentAdapter, ComponentEvent, WindowAdapter}
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.geom.Point2D
import javax.swing.{JFrame, JDialog}
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.model.{Vector2DModel, AdjustableCoordinateModel, CoordinateFrameModel, FreeBodyDiagramModel, Vector}
import MotionSeriesDefaults._

class FreeBodyDiagramDialog(frame: JFrame,
                            freeBodyDiagramModel: FreeBodyDiagramModel,
                            fbdWidth: Double,
                            coordinateFrameModel: CoordinateFrameModel,
                            adjustable: Boolean,
                            adjustableCoordinateModel: AdjustableCoordinateModel,
                            fbdListener: Point2D => Unit,
                            rampAngle: () => Double) extends VectorDisplay {
  def addVector(vector: Vector, tailLocation: Vector2DModel, maxLabelDist: Int, offsetPlayArea: Double) = freeBodyDiagramNode.addVector(vector, tailLocation, maxLabelDist, offsetPlayArea)

  val dialog = new JDialog(frame, "display.free-body-diagram".translate, false)
  dialog.setSize(FREE_BODY_DIAGRAM_DIALOG_WIDTH, FREE_BODY_DIAGRAM_DIALOG_HEIGHT)

  val freeBodyDiagramNode = new FreeBodyDiagramNode(freeBodyDiagramModel, FBD_DIALOG_NODE_WIDTH, FBD_DIALOG_NODE_HEIGHT, fbdWidth, fbdWidth, coordinateFrameModel, adjustableCoordinateModel, PhetCommonResources.getImage("buttons/minimizeButton.png".literal), rampAngle)
  freeBodyDiagramNode.addListener(fbdListener)
  val canvas = new PhetPCanvas
  canvas.addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) = updateNodeSize()
  })
  updateNodeSize()
  def updateNodeSize() = {
    if (canvas.getWidth > 0 && canvas.getHeight > 0) {
      val w = Math.min(canvas.getWidth, canvas.getHeight)
      freeBodyDiagramNode.setSize(w - FBD_INSET * 2, w - FBD_INSET * 2)
      freeBodyDiagramNode.setOffset(FBD_INSET, FBD_INSET)
    }
  }
  canvas.addScreenChild(freeBodyDiagramNode)
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

  def clearVectors() = freeBodyDiagramNode.clearVectors()

  def removeVector(vector: Vector) = freeBodyDiagramNode.removeVector(vector)
}