package edu.colorado.phet.motionseries.graphics

import common.piccolophet.nodes.{PhetPPath, GradientButtonNode}
import java.awt.geom.{Rectangle2D, Point2D}
import java.awt.{BasicStroke, Color}
import phet.common.phetcommon.resources.PhetCommonResources
import phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import java.awt.event._

import javax.swing.{JFrame}
import model._
import scalacommon.math.Vector2D
import scalacommon.Predef._
import motionseries.RampResources
import sims.theramp.{RampDefaults}

import tests.MyCanvas
import umd.cs.piccolo.PNode
import java.lang.Math._
import RampResources._

abstract class MotionSeriesCanvas(model: RampModel,
                                  adjustableCoordinateModel: AdjustableCoordinateModel,
                                  freeBodyDiagramModel: FreeBodyDiagramModel,
                                  vectorViewModel: VectorViewModel,
                                  frame: JFrame,
                                  modelViewport: Rectangle2D) extends MyCanvas(800, modelViewport) { //max y should be about 10 in default case
  setBackground(RampDefaults.SKY_GRADIENT_BOTTOM)

  val playAreaNode = new PNode
  addStageNode(playAreaNode)

  class LayoutStrut(modelRect: Rectangle2D) extends PhetPPath(transform.modelToViewDouble(modelRect)) {
    setStroke(new BasicStroke(2f))
    //    setStrokePaint(Color.blue)//enable this for debugging
    setStrokePaint(null)
  }

  playAreaNode.addChild(new SkyNode(transform))

  def useVectorNodeInPlayArea = true

  def createEarthNode: PNode

  val earthNode = createEarthNode
  playAreaNode.addChild(earthNode)

  def createLeftSegmentNode: HasPaint

  val leftSegmentNode = createLeftSegmentNode
  playAreaNode.addChild(leftSegmentNode)

  def createRightSegmentNode: HasPaint

  val rightSegmentNode = createRightSegmentNode
  playAreaNode.addChild(rightSegmentNode)

  def addHeightAndAngleIndicators()
  addHeightAndAngleIndicators()

  def addWallsAndDecorations()
  addWallsAndDecorations()

  val beadNode = new DraggableBeadNode(model.bead, transform, "cabinet.gif".literal, () => model.setPaused(false))
  model.addListenerByName(beadNode.setImage(RampResources.getImage(model.selectedObject.imageFilename)))
  playAreaNode.addChild(beadNode)

  val pusherNode = new PusherNode(transform, model.bead, model.manBead)
  playAreaNode.addChild(pusherNode)

  playAreaNode.addChild(new CoordinateFrameNode(model, adjustableCoordinateModel, transform))

  private def compositeListener(listener: () => Unit) = {
    model.rampSegments(0).addListener(listener)
    model.rampSegments(1).addListener(listener)
  }

  val tickMarkSet = new TickMarkSet(transform, model.positionMapper, compositeListener) //todo: listen to both segments for game
  playAreaNode.addChild(tickMarkSet)

  val fbdWidth = RampDefaults.freeBodyDiagramWidth
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

  class VectorSetNode(transform: ModelViewTransform2D, bead: Bead) extends PNode {
    def addVector(a: Vector, offset: VectorValue) = {
      val node = new BodyVectorNode(transform, a, offset)
      addChild(node)
    }
  }

  class BodyVectorNode(transform: ModelViewTransform2D, vector: Vector, offset: VectorValue) extends VectorNode(transform, vector, offset, RampDefaults.BODY_LABEL_MAX_OFFSET) {
    model.bead.addListenerByName {
      setOffset(model.bead.position2D)
      update()
    }
  }

  val vectorNode = new VectorSetNode(transform, model.bead)
  playAreaNode.addChild(vectorNode)
  def addVectorAllComponents(bead: Bead, beadVector: BeadVector with PointOfOriginVector, offsetFBD: VectorValue,
                             offsetPlayArea: Double, selectedVectorVisible: () => Boolean) = {
    addVector(bead, beadVector, offsetFBD, offsetPlayArea)
    val parallelComponent = new ParallelComponent(beadVector, bead)
    val perpComponent = new PerpendicularComponent(beadVector, bead)
    val xComponent = new XComponent(beadVector, bead, model.coordinateFrameModel)
    val yComponent = new YComponent(beadVector, bead, model.coordinateFrameModel)
    def update() = {
      yComponent.visible = vectorViewModel.xyComponentsVisible && selectedVectorVisible()
      xComponent.visible = vectorViewModel.xyComponentsVisible && selectedVectorVisible()
      beadVector.visible = vectorViewModel.originalVectors && selectedVectorVisible()
      parallelComponent.visible = vectorViewModel.parallelComponents && selectedVectorVisible()
      perpComponent.visible = vectorViewModel.parallelComponents && selectedVectorVisible()
    }
    vectorViewModel.addListener(update)
    update()

    addVector(bead, xComponent, offsetFBD, offsetPlayArea)
    addVector(bead, yComponent, offsetFBD, offsetPlayArea)
    addVector(bead, parallelComponent, offsetFBD, offsetPlayArea)
    addVector(bead, perpComponent, offsetFBD, offsetPlayArea)
  }

  def addVector(bead: Bead, vector: Vector with PointOfOriginVector, offsetFBD: VectorValue, offsetPlayArea: Double) = {
    fbdNode.addVector(vector, offsetFBD, RampDefaults.FBD_LABEL_MAX_OFFSET)
    windowFBDNode.addVector(vector, offsetFBD, RampDefaults.FBD_LABEL_MAX_OFFSET)

    val tailLocationInPlayArea = new VectorValue() {
      def addListener(listener: () => Unit) = {
        bead.addListener(listener)
        vectorViewModel.addListener(listener)
      }

      def getValue = {
        val defaultCenter = bead.height / 2.0
        bead.position2D + new Vector2D(bead.getAngle + PI / 2) *
                (offsetPlayArea + (if (vectorViewModel.centered) defaultCenter else vector.getPointOfOriginOffset(defaultCenter)))
      }


      def removeListener(listener: () => Unit) = {
        bead.removeListener(listener)
        vectorViewModel.removeListener(listener)
      }
    }
    //todo: make sure this adapter overrides other methods as well such as addListener
    val playAreaAdapter = new Vector(vector.color, vector.name, vector.abbreviation, () => vector.getValue * RampDefaults.PLAY_AREA_VECTOR_SCALE, vector.painter) {
      vector.addListenerByName {
        notifyListeners()
      }
      override def visible = vector.visible

      override def visible_=(vis: Boolean) = vector.visible = vis

      override def getPaint = vector.getPaint
    }

    if (useVectorNodeInPlayArea) {
      vectorNode.addVector(playAreaAdapter, tailLocationInPlayArea)
    }
    bead.removalListeners += (() => {
      fbdNode.removeVector(vector)
      windowFBDNode.removeVector(vector)
      //      vectorNode.removeVector(playAreaAdapter) //todo: don't use vectorNode for game module but remove it if non-game module
    })

  }

  def addVectorAllComponents(bead: Bead, a: BeadVector): Unit = addVectorAllComponents(bead, a, new ConstantVectorValue, 0, () => true)

  def addAllVectors(bead: Bead) = {
    addVectorAllComponents(bead, bead.appliedForceVector)
    addVectorAllComponents(bead, bead.gravityForceVector)
    addVectorAllComponents(bead, bead.normalForceVector)
    addVectorAllComponents(bead, bead.frictionForceVector)
    addVectorAllComponents(bead, bead.wallForceVector)
    addVectorAllComponents(bead, bead.totalForceVector, new ConstantVectorValue(new Vector2D(0, fbdWidth / 4)), 2, () => vectorViewModel.sumOfForcesVector) //no need to add a separate listener, since it is already contained in vectorviewmodel
  }
  addAllVectors(model.bead)

  playAreaNode.addChild(new RaindropView(model, this))
  playAreaNode.addChild(new FireDogView(model, this))

  val clearHeatButton = new ClearHeatButton(model)
  clearHeatButton.setOffset(stage.width/2-clearHeatButton.getFullBounds.getWidth/2,stage.height/4)
  addStageNode(clearHeatButton)

  val returnObjectButton = new ReturnObjectButton(model)
  returnObjectButton.setOffset(clearHeatButton.getFullBounds.getCenterX - returnObjectButton.getFullBounds.getWidth/2,clearHeatButton.getFullBounds.getMaxY + 10)
  addStageNode(returnObjectButton)
}

class ReturnObjectButton(model: RampModel) extends GradientButtonNode("controls.return-object".translate, Color.orange) {
  def updateVisibility() = setVisible(model.beadInModelViewportRange)
  updateVisibility()
  model.addListener(updateVisibility)

  addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) = model.returnBead()
  })
}

class ClearHeatButton(model: RampModel) extends GradientButtonNode("controls.clear-heat".translate, Color.yellow) {
  def updateVisibility() = {
    setVisible(model.bead.getRampThermalEnergy > 2000)
  }
  updateVisibility()
  model.addListener(updateVisibility)

  addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) = model.clearHeat()
  })
}

class RampCanvas(model: RampModel, coordinateSystemModel: AdjustableCoordinateModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                 vectorViewModel: VectorViewModel, frame: JFrame, showObjectSelectionNode: Boolean, showAppliedForceSlider: Boolean,
                 rampAngleDraggable: Boolean, modelViewport: Rectangle2D)
        extends MotionSeriesCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel, frame, modelViewport) {
  if (showAppliedForceSlider) {
    val appliedForceSliderNode = new AppliedForceSliderNode(model.bead, () => model.setPaused(false))
    appliedForceSliderNode.setOffset(stage.width / 2 - appliedForceSliderNode.getFullBounds.getWidth / 2, transform.modelToView(0, -1).getY)
    addStageNode(appliedForceSliderNode)
  }

  if (showObjectSelectionNode) {
    val objectSelectionNode = new ObjectSelectionNode(model)
    objectSelectionNode.setScale(stage.width / (objectSelectionNode.getFullBounds.getWidth + 150))
    objectSelectionNode.setOffset(stage.width / 2 - objectSelectionNode.getFullBounds.getWidth / 2, stage.height - objectSelectionNode.getFullBounds.getHeight - 2)
    addStageNode(objectSelectionNode)
  }

  override def addWallsAndDecorations() = {
    playAreaNode.addChild(new BeadNode(model.leftWall, transform, "wall.jpg".literal) with CloseButton {
      def model = RampCanvas.this.model
    })
    playAreaNode.addChild(new BeadNode(model.rightWall, transform, "wall.jpg".literal) with CloseButton {
      def model = RampCanvas.this.model
    })
  }

  def createLeftSegmentNode = new RampSegmentNode(model.rampSegments(0), transform)

  def createRightSegmentNode =
    if (rampAngleDraggable)
      new RotatableSegmentNode(model.rampSegments(1), transform)
    else
      new RampSegmentNode(model.rampSegments(1), transform)

  def addHeightAndAngleIndicators() = {
    playAreaNode.addChild(new RampHeightIndicator(model.rampSegments(1), transform))
    playAreaNode.addChild(new RampAngleIndicator(model.rampSegments(1), transform))
  }

  def createEarthNode = new EarthNode(transform)
}

trait PointOfOriginVector {
  def getPointOfOriginOffset(defaultCenter: Double): Double
}