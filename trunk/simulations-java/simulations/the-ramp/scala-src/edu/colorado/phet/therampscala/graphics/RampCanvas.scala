package edu.colorado.phet.therampscala.graphics

import common.phetcommon.resources.PhetCommonResources
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.nodes.GradientButtonNode
import java.awt.Color
import java.awt.geom.{Point2D}
import java.awt.event._

import javax.swing.{JFrame}
import model._
import scalacommon.math.Vector2D
import scalacommon.Predef._
import umd.cs.piccolo.PNode
import java.lang.Math._
import RampResources._

abstract class AbstractRampCanvas(model: RampModel,
                                  coordinateSystemModel: CoordinateSystemModel,
                                  freeBodyDiagramModel: FreeBodyDiagramModel,
                                  vectorViewModel: VectorViewModel,
                                  frame: JFrame,
                                  modelOffsetY: Double)
        extends DefaultCanvas(22, 22, RampDefaults.worldWidth, RampDefaults.worldHeight, modelOffsetY) {
  setBackground(RampDefaults.SKY_GRADIENT_BOTTOM)

  addNode(new SkyNode(transform))

  def useVectorNodeInPlayArea = true

  def createEarthNode: PNode

  val earthNode = createEarthNode
  addNode(earthNode)

  def createLeftSegmentNode: HasPaint

  val leftSegmentNode = createLeftSegmentNode
  addNode(leftSegmentNode)

  def createRightSegmentNode: HasPaint

  val rightSegmentNode = createRightSegmentNode
  addNode(rightSegmentNode)

  def addHeightAndAngleIndicators()
  addHeightAndAngleIndicators()

  def addWallsAndDecorations()
  addWallsAndDecorations()

  val beadNode = new DraggableBeadNode(model.bead, transform, "cabinet.gif".literal, () => model.setPaused(false))
  model.addListenerByName(beadNode.setImage(RampResources.getImage(model.selectedObject.imageFilename)))
  addNode(beadNode)

  val pusherNode = new PusherNode(transform, model.bead, model.manBead)
  addNode(pusherNode)

  addNode(new CoordinateFrameNode(model, coordinateSystemModel, transform))

  private def compositeListener(listener: () => Unit) = {
    model.rampSegments(0).addListener(listener)
    model.rampSegments(1).addListener(listener)
  }

  val tickMarkSet = new TickMarkSet(transform, model.positionMapper, compositeListener) //todo: listen to both segments for game
  addNode(tickMarkSet)

  val fbdWidth = RampDefaults.freeBodyDiagramWidth
  val fbdNode = new FreeBodyDiagramNode(freeBodyDiagramModel, 200, 200, fbdWidth, fbdWidth, model.coordinateFrameModel, coordinateSystemModel.adjustable, PhetCommonResources.getImage("buttons/maximizeButton.png".literal))

  def updateFBDLocation() = {
    fbdNode.setOffset(10, 10)
    if (getHeight > 0 && getWidth > 0 && !getVisibleModelBounds.contains(fbdNode.getFullBounds))
      fbdNode.setOffset(getVisibleModelBounds.getX + 200, getVisibleModelBounds.getY)
  }
  addComponentListener(new ComponentAdapter() {
    override def componentResized(e: ComponentEvent) = {
      updateFBDLocation()
    }
  })
  updateFBDLocation()

  val fbdListener = (pt: Point2D) => {model.bead.parallelAppliedForce = pt.getX}
  fbdNode.addListener(fbdListener)
  addNode(fbdNode)
  defineInvokeAndPass(freeBodyDiagramModel.addListenerByName) {
    fbdNode.setVisible(freeBodyDiagramModel.visible && !freeBodyDiagramModel.windowed)
  }

  val windowFBDNode = new FBDDialog(frame, freeBodyDiagramModel, fbdWidth, model.coordinateFrameModel, coordinateSystemModel.adjustable, coordinateSystemModel, fbdListener)

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
  addNode(vectorNode)
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
    //todo: make sure this adapter overrides other methods as well, such as getPaint and addListener
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
    //todo: switch to removalListeners paradigm
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

  addWorldChild(new RaindropView(model, this))
  addWorldChild(new FireDogView(model, this))
  addWorldChild(new ClearHeatButton(model))
  addWorldChild(new ReturnObjectButton(model))
}

class ReturnObjectButton(model: RampModel) extends GradientButtonNode("controls.return-object".translate, Color.orange) {
  setOffset(RampDefaults.worldWidth / 2.0 - getFullBounds.getWidth / 2, RampDefaults.worldHeight * 0.2)
  def updateVisibility() = setVisible(model.beadInModelViewportRange)
  updateVisibility()
  model.addListener(updateVisibility)

  addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) = model.returnBead()
  })
}

class ClearHeatButton(model: RampModel) extends GradientButtonNode("controls.clear-heat".translate, Color.yellow) {
  setOffset(RampDefaults.worldWidth / 2.0 - getFullBounds.getWidth / 2, RampDefaults.worldHeight * 0.25)
  def updateVisibility() = {
    setVisible(model.bead.getRampThermalEnergy > 2000)
  }
  updateVisibility()
  model.addListener(updateVisibility)

  addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) = model.clearHeat()
  })
}

class RampCanvas(model: RampModel, coordinateSystemModel: CoordinateSystemModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                 vectorViewModel: VectorViewModel, frame: JFrame, showObjectSelectionNode: Boolean,
                 rampAngleDraggable: Boolean, modelOffsetY: Double)
        extends AbstractRampCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel, frame, modelOffsetY) {
  if (showObjectSelectionNode) {
    addNode(new ObjectSelectionNode(transform, model))
    addNode(indexOfChild(earthNode) + 1, new AppliedForceSliderNode(model.bead, transform, () => model.setPaused(false)))
  }

  override def addWallsAndDecorations() = {
    addNode(new BeadNode(model.leftWall, transform, "wall.jpg".literal) with CloseButton {
      def model = RampCanvas.this.model
    })
    addNode(new BeadNode(model.rightWall, transform, "wall.jpg".literal) with CloseButton {
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
    addNode(new RampHeightIndicator(model.rampSegments(1), transform))
    addNode(new RampAngleIndicator(model.rampSegments(1), transform))
  }

  def createEarthNode = new EarthNode(transform)

}

trait PointOfOriginVector {
  def getPointOfOriginOffset(defaultCenter: Double): Double
}