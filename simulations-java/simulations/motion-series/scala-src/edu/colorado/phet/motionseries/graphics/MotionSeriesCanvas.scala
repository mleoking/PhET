package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.piccolophet.nodes.{PhetPPath, GradientButtonNode}
import java.awt.geom.{Rectangle2D, Point2D}
import java.awt.{BasicStroke, Color}
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

  def createLeftSegmentNode = new RampSegmentNode(model.rampSegments(0), transform, model)

  def createRightSegmentNode: HasPaint = new RotatableSegmentNode(model.rampSegments(1), transform, model)

  val rightSegmentNode = createRightSegmentNode
  playAreaNode.addChild(rightSegmentNode)

  def addHeightAndAngleIndicators()
  addHeightAndAngleIndicators()

  def attachListenerToRightWall(node:PNode):Unit
  def addWallsAndDecorations() = {
    playAreaNode.addChild(new MotionSeriesObjectNode(model.leftWall, transform, "wall.jpg".literal) with CloseButton {
      def model = MotionSeriesCanvas.this.model
    })
    playAreaNode.addChild(new MotionSeriesObjectNode(model.rightWall, transform, "wall.jpg".literal) with CloseButton {
      attachListenerToRightWall(this)
      def model = MotionSeriesCanvas.this.model
    })

    class SpringNode(motionSeriesObject: MotionSeriesObject) extends MotionSeriesObjectNode(motionSeriesObject, transform, "spring.png".literal) {
      defineInvokeAndPass(model.addListenerByName) {
        setVisible(model.wallsBounce() && model.walls)
      }
    }
    playAreaNode.addChild(new SpringNode(model.leftWallRightEdge))
    playAreaNode.addChild(new SpringNode(model.rightWallLeftEdge))
  }
  addWallsAndDecorations()

  val motionSeriesObjectNode = createmotionSeriesObjectNode(model.motionSeriesObject, transform, model.selectedObject.imageFilename, model.selectedObject.crashImageFilename, () => {
    if (model.isPlayback) {
      model.clearHistoryRemainder()
      model.setRecord(true)
    }
    model.setPaused(false)
  })

  //todo: shouldn't assume ForcemotionSeriesObject subclass
  def createmotionSeriesObjectNode(b: MovingManMotionSeriesObject, t: ModelViewTransform2D, imageName: String, crashImageName: String, listener: () => Unit): MotionSeriesObjectNode = new ForceDragMotionSeriesObjectNode(b, t, imageName, crashImageName, listener)

  //todo: this line was continually calling setImage on the imageNode
  model.addListenerByName(motionSeriesObjectNode.setImages(MotionSeriesResources.getImage(model.selectedObject.imageFilename),
    MotionSeriesResources.getImage(model.selectedObject.crashImageFilename)))
  playAreaNode.addChild(motionSeriesObjectNode)

  playAreaNode.addChild(new CoordinateFrameNode(model, adjustableCoordinateModel, transform))

  private def compositeListener(listener: () => Unit) = {
    model.rampSegments(0).addListener(listener)
    model.rampSegments(1).addListener(listener)
  }

  val tickMarkSet = new TickMarkSet(transform, model.positionMapper, compositeListener) //todo: listen to both segments for game
  playAreaNode.addChild(tickMarkSet)

  val fbdWidth = MotionSeriesDefaults.freeBodyDiagramWidth
  val fbdNode = new FreeBodyDiagramNode(freeBodyDiagramModel, 200, 200, fbdWidth, fbdWidth, model.coordinateFrameModel, adjustableCoordinateModel, PhetCommonResources.getImage("buttons/maximizeButton.png".literal), () => model.rampAngle)

  def updateFBDLocation() = {
    fbdNode.setOffset(50, 10)
  }

  val fbdListener = (pt: Point2D) => {model.motionSeriesObject.parallelAppliedForce = pt.getX}
  fbdNode.addListener(fbdListener)
  addStageNode(fbdNode)
  defineInvokeAndPass(freeBodyDiagramModel.addListenerByName) {
    fbdNode.setVisible(freeBodyDiagramModel.visible && !freeBodyDiagramModel.windowed)
  }

  val windowFBDNode = new FBDDialog(frame, freeBodyDiagramModel, fbdWidth, model.coordinateFrameModel, adjustableCoordinateModel.adjustable, adjustableCoordinateModel, fbdListener, () => model.rampAngle)

  addComponentListener(new ComponentAdapter() {override def componentResized(e: ComponentEvent) = {updateLayout()}})
  updateLayout()
  override def updateLayout() = {
    super.updateLayout()
    updateFBDLocation()
  }

  val playAreaVectorNode = new PlayAreaVectorNode(transform, model.motionSeriesObject, vectorViewModel)
  playAreaNode.addChild(playAreaVectorNode)

  val vectorView = new VectorView(model.motionSeriesObject, vectorViewModel, model.coordinateFrameModel, fbdWidth)
  vectorView.addAllVectors(model.motionSeriesObject, fbdNode)
  vectorView.addAllVectors(model.motionSeriesObject, windowFBDNode)
  if (useVectorNodeInPlayArea) vectorView.addAllVectors(model.motionSeriesObject, playAreaVectorNode)

  playAreaNode.addChild(new RaindropView(model, this))
  playAreaNode.addChild(new FireDogView(model, this))

  val clearHeatButton = new ClearHeatButton(model)
  val viewPt = transform.modelToView(5, 2) //show near the right side of the ramp, just above it so that it is visible in every sim+module
  clearHeatButton.setOffset(viewPt.x - clearHeatButton.getFullBounds.getWidth / 2, viewPt.y)
  addStageNode(clearHeatButton)

  val returnObjectButton = new ReturnObjectButton(model)
  returnObjectButton.setOffset(clearHeatButton.getFullBounds.getCenterX - returnObjectButton.getFullBounds.getWidth / 2, clearHeatButton.getFullBounds.getMaxY + 10)
  addStageNode(returnObjectButton)

  //Low quality rendering doesn't seem to significantly impact performance in this sim
  //  setInteractingRenderQuality(PPaintContext.LOW_QUALITY_RENDERING)
  //  setDefaultRenderQuality(PPaintContext.LOW_QUALITY_RENDERING)
  //  setAnimatingRenderQuality(PPaintContext.LOW_QUALITY_RENDERING)
}

class ReturnObjectButton(model: MotionSeriesModel) extends GradientButtonNode("controls.return-object".translate, Color.orange) {
  def updateVisibility() = setVisible(model.motionSeriesObjectInModelViewportRange || model.motionSeriesObject.isCrashed)
  updateVisibility()
  model.addListener(updateVisibility)

  addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) = model.returnMotionSeriesObject()
  })
}

class ClearHeatButton(model: MotionSeriesModel) extends GradientButtonNode("controls.clear-heat".translate, Color.yellow) {
  def updateVisibility() = setVisible(model.motionSeriesObject.rampThermalEnergy > MotionSeriesDefaults.CLEAR_BUTTON_VISIBILITY_THRESHOLD_JOULES)
  updateVisibility()
  model.addListener(updateVisibility) //todo: perhaps this line is unnecessary
  model.motionSeriesObject.addListener(updateVisibility)

  addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) = model.clearHeat()
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
    val appliedForceSliderNode = new AppliedForceSliderNode(model.motionSeriesObject, () => model.setPaused(false))
    addBehindVectorNodes(appliedForceSliderNode)

    var relayout: ComponentAdapter = new ComponentAdapter {
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
    playAreaNode.addChild(new RampHeightIndicator(model.rampSegments(1), transform))
    playAreaNode.addChild(new RampAngleIndicator(model.rampSegments(1), transform))
  }

  def attachListenerToRightWall(wall:PNode) = {
    wall.addInputEventListener(new CursorHandler)
    wall.addInputEventListener(new RotationHandler(transform, wall, model.rampSegments(1), 0, MotionSeriesDefaults.MAX_ANGLE))
  }
}