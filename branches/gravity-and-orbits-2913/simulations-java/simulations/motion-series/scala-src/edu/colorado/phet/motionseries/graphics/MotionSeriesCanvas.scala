package edu.colorado.phet.motionseries.graphics

import java.awt.geom.{Rectangle2D, Point2D}
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import java.awt.event._
import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.scalacommon.Predef._
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.motionseries.MotionSeriesResources._
import javax.swing.JFrame
import edu.colorado.phet.motionseries.controls.ObjectSelectionComboBoxNode
import edu.colorado.phet.motionseries.javastage.stage.PlayArea
import edu.colorado.phet.motionseries.{StageContainerArea, MotionSeriesResources, MotionSeriesDefaults}
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import java.awt.{BasicStroke, Color}
import MotionSeriesDefaults.freeBodyDiagramWidth
import edu.colorado.phet.common.piccolophet.nodes.{HTMLImageButtonNode, PhetPPath}

/**
 * This is the base canvas for the "Ramp" and "Forces and Motion" sims
 * @author Sam Reid
 */
abstract class MotionSeriesCanvas(model: MotionSeriesModel,
                                  adjustableCoordinateModel: AdjustableCoordinateModel,
                                  freeBodyDiagramModel: FreeBodyDiagramModel,
                                  vectorViewModel: VectorViewModel,
                                  frame: JFrame,
                                  modelViewport: Rectangle2D,
                                  val stageContainerArea: StageContainerArea)
        extends PlayArea(800, modelViewport) { //max y should be about 10 in default case
  val transform = super.getModelStageTransform
  val stage = super.getStage
  setBackground(MotionSeriesDefaults.SKY_GRADIENT_BOTTOM)

  val playAreaNode = new PNode
  addStageNode(playAreaNode)

  class LayoutStrut(modelRect: Rectangle2D) extends PhetPPath(transform.modelToViewDouble(modelRect)) {
    setStroke(new BasicStroke(2f))
    //    setStrokePaint(Color.blue)//enable this for debugging
    setStrokePaint(null)
  }

  playAreaNode.addChild(new SkyNode(transform))

  //  override def paintImmediately = {}
  //
  //  override def paintImmediately(x: Int, y: Int, w: Int, h: Int) = {}
  //
  //  override def paintImmediately(r: Rectangle) = {}

  //For support of active rendering
  def doPaintImmediately() = {
    super.paintImmediately(0, 0, getWidth, getHeight)
  }

  def useVectorNodeInPlayArea = true

  def createEarthNode: AbstractEarthNode = new EarthNode(transform)

  val earthNode = createEarthNode
  playAreaNode.addChild(earthNode)

  //nodes added to this will appear behind vector nodes but in front of earth node
  private val behindVectorNode = new PNode

  def addBehindVectorNodes(node: PNode) = behindVectorNode.addChild(node)
  playAreaNode.addChild(behindVectorNode)

  val leftSegmentNode = createLeftSegmentNode
  playAreaNode.addChild(leftSegmentNode)

  def createLeftSegmentNode = new RampSegmentNode(model.leftRampSegment, transform, model, model.motionSeriesObject)

  def createRightSegmentNode: HasPaint = new RotatableSegmentNode(model.rightRampSegment, transform, model, model.motionSeriesObject)

  val rightSegmentNode = createRightSegmentNode
  playAreaNode.addChild(rightSegmentNode)

  def addHeightAndAngleIndicators()
  addHeightAndAngleIndicators()

  def attachListenerToRightWall(node: PNode): Unit

  def addWallsAndDecorations() = {
    playAreaNode.addChild(new MotionSeriesObjectNode(model.leftWall, transform, "wall.jpg".literal) with CloseButton {
      def model = MotionSeriesCanvas.this.model
    })
    playAreaNode.addChild(new MotionSeriesObjectNode(model.rightWall, transform, "wall.jpg".literal) with CloseButton {
      attachListenerToRightWall(this)
      def model = MotionSeriesCanvas.this.model
    })

    class SpringNode(motionSeriesObject: MotionSeriesObject) extends MotionSeriesObjectNode(motionSeriesObject, transform, "spring.png".literal) {
      def updateVisibility() = setVisible(model.wallsBounce.booleanValue && model.walls.booleanValue)
      model.wallsBounce.addListener(updateVisibility)
      model.walls.addListener(updateVisibility)
      updateVisibility()
    }
    playAreaNode.addChild(new SpringNode(model.leftWallRightEdge))
    playAreaNode.addChild(new SpringNode(model.rightWallLeftEdge))
  }
  addWallsAndDecorations()

  val motionSeriesObjectNode = createMotionSeriesObjectNode(model.motionSeriesObject, transform, model.selectedObject.imageFilename, model.selectedObject.crashImageFilename, () => model.resume())

  //todo: shouldn't assume MotionSeriesObject subclass
  def createMotionSeriesObjectNode(b: MotionSeriesObject, t: ModelViewTransform2D, imageName: String, crashImageName: String, listener: () => Unit): MotionSeriesObjectNode = new ForceDragMotionSeriesObjectNode(b, t, imageName, crashImageName, listener)

  //todo: this line was continually calling setImage on the imageNode
  model.addListenerByName(motionSeriesObjectNode.setImages(MotionSeriesResources.getImage(model.selectedObject.imageFilename),
    MotionSeriesResources.getImage(model.selectedObject.crashImageFilename)))
  playAreaNode.addChild(motionSeriesObjectNode)

  playAreaNode.addChild(new CoordinateFrameNode(model, adjustableCoordinateModel, transform))

  private def compositeListener(listener: () => Unit) = {
    model.leftRampSegment.addListener(listener)
    model.rightRampSegment.addListener(listener)
  }

  val tickMarkSet = new TickMarkSet(transform, model.positionMapper, compositeListener)
  playAreaNode.addChild(tickMarkSet)

  val freeBodyDiagramNode = new FreeBodyDiagramNode(freeBodyDiagramModel, 200, 200, freeBodyDiagramWidth, freeBodyDiagramWidth,
    model.coordinateFrameModel, adjustableCoordinateModel, PhetCommonResources.getImage("buttons/maximizeButton.png".literal), () => model.rampAngle, () => model.resume())

  def updateFreeBodyDiagramLocation() = freeBodyDiagramNode.setOffset(50, 10)

  val freeBodyDiagramListener = (pt: Point2D) => {model.motionSeriesObject.parallelAppliedForce = pt.getX}
  freeBodyDiagramNode.addListener(freeBodyDiagramListener)
  addStageNode(freeBodyDiagramNode)
  defineInvokeAndPass(freeBodyDiagramModel.addListenerByName) {
    freeBodyDiagramNode.setVisible(freeBodyDiagramModel.visible && !freeBodyDiagramModel.windowed)
  }

  val freeBodyDiagramDialog = new FreeBodyDiagramDialog(frame, freeBodyDiagramModel, freeBodyDiagramWidth, model.coordinateFrameModel,
    adjustableCoordinateModel.adjustable, adjustableCoordinateModel, freeBodyDiagramListener, () => model.rampAngle, () => model.resume())

  addComponentListener(new ComponentAdapter() {override def componentResized(e: ComponentEvent) = {updateLayout()}})
  updateLayout()
  override def updateLayout() = {
    super.updateLayout()
    updateFreeBodyDiagramLocation()
  }

  val playAreaVectorNode = new PlayAreaVectorDisplay(transform, model.motionSeriesObject)
  playAreaNode.addChild(playAreaVectorNode)

  val vectorView = new VectorView(model.motionSeriesObject, vectorViewModel, model.coordinateFrameModel, freeBodyDiagramWidth)
  vectorView.addAllVectorsAllComponents(model.motionSeriesObject, freeBodyDiagramNode)
  vectorView.addAllVectorsAllComponents(model.motionSeriesObject, freeBodyDiagramDialog)
  if (useVectorNodeInPlayArea) vectorView.addAllVectorsAllComponents(model.motionSeriesObject, playAreaVectorNode)

  val returnObjectButton = new ReturnObjectButton(model)
  val viewPt = transform.modelToView(5, 2) //show near the right side of the ramp, just above it so that it is visible in every sim+module
  returnObjectButton.setOffset(viewPt.x - returnObjectButton.getFullBounds.getWidth / 2, viewPt.y)
  addStageNode(returnObjectButton)
}

class ReturnObjectButton(model: MotionSeriesModel) extends HTMLImageButtonNode("controls.return-object".translate, Color.orange) {
  def updateVisibility() {
    setVisible(model.motionSeriesObjectInModelViewportRange || model.motionSeriesObject.isCrashed)
  }

  updateVisibility()
  model.addListener(updateVisibility)

  addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) {
      model.returnMotionSeriesObject()
    }
  })
}

abstract class MotionSeriesCanvasDecorator(model: MotionSeriesModel,
                                           coordinateSystemModel: AdjustableCoordinateModel,
                                           freeBodyDiagramModel: FreeBodyDiagramModel,
                                           vectorViewModel: VectorViewModel,
                                           frame: JFrame,
                                           showObjectSelectionNode: Boolean,
                                           showAppliedForceSlider: Boolean,
                                           rampAngleDraggable: Boolean,
                                           modelViewport: Rectangle2D,
                                           stageContainerArea: StageContainerArea)
        extends MotionSeriesCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel, frame, modelViewport, stageContainerArea) {
  val pusherNode = new PusherNode(transform, model.motionSeriesObject, model.manMotionSeriesObject)
  playAreaNode.addChild(playAreaNode.indexOfChild(playAreaVectorNode) - 1, pusherNode) //put the pusher behind the vector nodes because otherwise he gets in the way

  if (showAppliedForceSlider) {
    val appliedForceSliderNode = new AppliedForceSliderNode(model.motionSeriesObject, () => model.resume())
    addBehindVectorNodes(appliedForceSliderNode)

    val relayout: ComponentAdapter = new ComponentAdapter {
      override def componentResized(e: ComponentEvent) = {
        appliedForceSliderNode.setOffset(stage.getWidth / 2 - appliedForceSliderNode.getFullBounds.getWidth / 2, transform.modelToView(0, -1).getY + 10)
      }
    }
    relayout.componentResized(null)

    addComponentListener(relayout)
  }

  if (showObjectSelectionNode) {
    addStageNode(new ObjectSelectionComboBoxNode(model, this) {
      setOffset(stage.getWidth / 2 - getFullBounds.getWidth / 2, stage.getHeight - getFullBounds.getHeight - 2)
    })
  }

  override def getContainerBounds = stageContainerArea.getBounds(getWidth, getHeight)
}

class RampCanvas(model: MotionSeriesModel, coordinateSystemModel: AdjustableCoordinateModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                 vectorViewModel: VectorViewModel, frame: JFrame, showObjectSelectionNode: Boolean, showAppliedForceSlider: Boolean,
                 rampAngleDraggable: Boolean, modelViewport: Rectangle2D, stageContainerArea: StageContainerArea)
        extends MotionSeriesCanvasDecorator(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel,
          frame, showObjectSelectionNode, showAppliedForceSlider, rampAngleDraggable, modelViewport, stageContainerArea) {
  def addHeightAndAngleIndicators() = {
    playAreaNode.addChild(new RampHeightIndicator(model.rightRampSegment, transform))
    playAreaNode.addChild(new RampAngleIndicator(model.rightRampSegment, transform))
  }

  def attachListenerToRightWall(wall: PNode) = {
    wall.addInputEventListener(new CursorHandler)
    wall.addInputEventListener(new RotationHandler(transform, wall, model.rightRampSegment, 0, MotionSeriesDefaults.MAX_ANGLE))
  }
}