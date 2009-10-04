package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.piccolophet.nodes.{PhetPPath, GradientButtonNode}
import java.awt.geom.{Rectangle2D, Point2D}
import java.awt.{BasicStroke, Color}
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import java.awt.event._
import javax.swing.{JFrame}
import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.motionseries.MotionSeriesResources
import edu.colorado.phet.motionseries.sims.theramp.StageContainerArea
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.tests.MyCanvas

abstract class MotionSeriesCanvas(model: MotionSeriesModel,
                                  adjustableCoordinateModel: AdjustableCoordinateModel,
                                  freeBodyDiagramModel: FreeBodyDiagramModel,
                                  vectorViewModel: VectorViewModel,
                                  frame: JFrame,
                                  modelViewport: Rectangle2D,
                                  val stageContainerArea: StageContainerArea)
        extends MyCanvas(800, modelViewport) { //max y should be about 10 in default case
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


  val leftSegmentNode = createLeftSegmentNode
  playAreaNode.addChild(leftSegmentNode)

  def createLeftSegmentNode = new RampSegmentNode(model.rampSegments(0), transform, model)

  def createRightSegmentNode: HasPaint = new RotatableSegmentNode(model.rampSegments(1), transform, model)

  val rightSegmentNode = createRightSegmentNode
  playAreaNode.addChild(rightSegmentNode)

  def addHeightAndAngleIndicators()
  addHeightAndAngleIndicators()

  def addWallsAndDecorations() = {
    playAreaNode.addChild(new BeadNode(model.leftWall, transform, "wall.jpg".literal) with CloseButton {
      def model = MotionSeriesCanvas.this.model
    })
    playAreaNode.addChild(new BeadNode(model.rightWall, transform, "wall.jpg".literal) with CloseButton {
      def model = MotionSeriesCanvas.this.model
    })

    class SpringNode(bead: Bead) extends BeadNode(bead, transform, "spring.png".literal) {
      defineInvokeAndPass(model.addListenerByName) {
        setVisible(model.wallsBounce())
      }
    }
    playAreaNode.addChild(new SpringNode(model.leftWallRightEdge))
    playAreaNode.addChild(new SpringNode(model.rightWallLeftEdge))
  }
  addWallsAndDecorations()

  //todo: why is cabinet hard coded here?
  val beadNode = createBeadNode(model.bead, transform, "cabinet.gif".literal, () => model.setPaused(false))

  def createBeadNode(b: Bead, t: ModelViewTransform2D, s: String, listener: () => Unit): BeadNode = new ForceDragBeadNode(b, t, s, listener)

  model.addListenerByName(beadNode.setImage(MotionSeriesResources.getImage(model.selectedObject.imageFilename)))
  playAreaNode.addChild(beadNode)

  playAreaNode.addChild(new CoordinateFrameNode(model, adjustableCoordinateModel, transform))

  private def compositeListener(listener: () => Unit) = {
    model.rampSegments(0).addListener(listener)
    model.rampSegments(1).addListener(listener)
  }

  val tickMarkSet = new TickMarkSet(transform, model.positionMapper, compositeListener) //todo: listen to both segments for game
  playAreaNode.addChild(tickMarkSet)

  val fbdWidth = MotionSeriesDefaults.freeBodyDiagramWidth
  val fbdNode = new FreeBodyDiagramNode(freeBodyDiagramModel, 200, 200, fbdWidth, fbdWidth, model.coordinateFrameModel, adjustableCoordinateModel, PhetCommonResources.getImage("buttons/maximizeButton.png".literal))

  def updateFBDLocation() = {
    fbdNode.setOffset(50, 10)
  }

  val fbdListener = (pt: Point2D) => {model.bead.parallelAppliedForce = pt.getX}
  fbdNode.addListener(fbdListener)
  addStageNode(fbdNode)
  defineInvokeAndPass(freeBodyDiagramModel.addListenerByName) {
    fbdNode.setVisible(freeBodyDiagramModel.visible && !freeBodyDiagramModel.windowed)
  }

  val windowFBDNode = new FBDDialog(frame, freeBodyDiagramModel, fbdWidth, model.coordinateFrameModel, adjustableCoordinateModel.adjustable, adjustableCoordinateModel, fbdListener)

  addComponentListener(new ComponentAdapter() {override def componentResized(e: ComponentEvent) = {updateLayout()}})
  updateLayout()
  override def updateLayout() = {
    super.updateLayout()
    updateFBDLocation()
  }
  //  addStageAreaDisplay()

  val playAreaVectorNode = new PlayAreaVectorNode(transform, model.bead, vectorViewModel)
  playAreaNode.addChild(playAreaVectorNode)

  val vectorView = new VectorView(model.bead, vectorViewModel, model.coordinateFrameModel, fbdWidth)
  vectorView.addAllVectors(model.bead, fbdNode)
  vectorView.addAllVectors(model.bead, windowFBDNode)
  if (useVectorNodeInPlayArea) vectorView.addAllVectors(model.bead, playAreaVectorNode)

  playAreaNode.addChild(new RaindropView(model, this))
  playAreaNode.addChild(new FireDogView(model, this))

  val clearHeatButton = new ClearHeatButton(model)
  val viewPt = transform.modelToView(5, 2) //show near the right side of the ramp, just above it so that it is visible in every sim+module
  clearHeatButton.setOffset(viewPt.x - clearHeatButton.getFullBounds.getWidth / 2, viewPt.y)
  addStageNode(clearHeatButton)

  val returnObjectButton = new ReturnObjectButton(model)
  returnObjectButton.setOffset(clearHeatButton.getFullBounds.getCenterX - returnObjectButton.getFullBounds.getWidth / 2, clearHeatButton.getFullBounds.getMaxY + 10)
  addStageNode(returnObjectButton)
}

class ReturnObjectButton(model: MotionSeriesModel) extends GradientButtonNode("controls.return-object".translate, Color.orange) {
  def updateVisibility() = setVisible(model.beadInModelViewportRange)
  updateVisibility()
  model.addListener(updateVisibility)

  addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) = model.returnBead()
  })
}

class ClearHeatButton(model: MotionSeriesModel) extends GradientButtonNode("controls.clear-heat".translate, Color.yellow) {
  def updateVisibility() = setVisible(model.bead.getRampThermalEnergy > MotionSeriesDefaults.CLEAR_BUTTON_VISIBILITY_THRESHOLD_JOULES)
  updateVisibility()
  model.addListener(updateVisibility) //todo: perhaps this line is unnecessary
  model.bead.addListener(updateVisibility)

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
  val pusherNode = new PusherNode(transform, model.bead, model.manBead)
  playAreaNode.addChild(pusherNode)

  if (showAppliedForceSlider) {
    val appliedForceSliderNode = new AppliedForceSliderNode(model.bead, () => model.setPaused(false))
    appliedForceSliderNode.setOffset(stage.width / 2 - appliedForceSliderNode.getFullBounds.getWidth / 2, transform.modelToView(0, -1).getY)
    addStageNode(appliedForceSliderNode)
  }

  if (showObjectSelectionNode) {
    val objectSelectionNode = new ObjectSelectionNode(model)
    objectSelectionNode.setScale(stage.width / (objectSelectionNode.getFullBounds.getWidth + 150))
    objectSelectionNode.setOffset(stage.width / 2 - objectSelectionNode.getFullBounds.getWidth / 2,
      stage.height - objectSelectionNode.getFullBounds.getHeight + 2)
    addStageNode(objectSelectionNode)
  }

  override def containerBounds = stageContainerArea.getBounds(getWidth, getHeight)
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
}