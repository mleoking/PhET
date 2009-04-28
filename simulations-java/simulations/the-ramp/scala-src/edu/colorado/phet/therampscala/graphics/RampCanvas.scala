package edu.colorado.phet.therampscala.graphics


import common.phetcommon.resources.PhetCommonResources
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.SwingUtils
import common.phetcommon.view.VerticalLayoutPanel
import common.piccolophet.event.CursorHandler
import java.awt.{Dimension, Color}
import javax.swing.{Box, JButton, JFrame, JDialog}
import swing.ScalaButton
import umd.cs.piccolo.nodes.{PImage, PText}
import common.piccolophet.PhetPCanvas
import java.awt.event._

import java.awt.geom.{Point2D, Rectangle2D}
import model._
import scalacommon.math.Vector2D
import scalacommon.Predef._
import scalacommon.util.Observable
import theramp.model.ValueAccessor.ParallelForceAccessor
import umd.cs.piccolo.event.{PInputEvent, PBasicInputEventHandler}
import umd.cs.piccolo.PNode
import java.lang.Math._
import umd.cs.piccolox.pswing.PSwing

abstract class AbstractRampCanvas(model: RampModel, coordinateSystemModel: CoordinateSystemModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                                  vectorViewModel: VectorViewModel, frame: JFrame) extends DefaultCanvas(22, 20) {
  setBackground(RampDefaults.SKY_GRADIENT_BOTTOM)

  addNode(new SkyNode(transform))

  def createEarthNode: PNode
  addNode(createEarthNode)

  def createLeftSegmentNode: PNode
  addNode(createLeftSegmentNode)

  def createRightSegmentNode: PNode
  addNode(createRightSegmentNode)

  def addHeightAndAngleIndicators()
  addHeightAndAngleIndicators()

  def addWallsAndDecorations()
  addWallsAndDecorations()

  val beadNode = new DraggableBeadNode(model.bead, transform, "cabinet.gif")
  model.addListenerByName(beadNode.setImage(RampResources.getImage(model.selectedObject.imageFilename)))
  addNode(beadNode)

  def createPusherNode: PNode
  addNode(createPusherNode)

  addNode(new CoordinateFrameNode(model, coordinateSystemModel, transform))

  val fbdWidth = RampDefaults.freeBodyDiagramWidth
  val fbdNode = new FreeBodyDiagramNode(freeBodyDiagramModel, 200, 200, fbdWidth, fbdWidth, model.coordinateFrameModel, coordinateSystemModel.adjustable, PhetCommonResources.getImage("buttons/maximizeButton.png"))
  val fbdListener = (pt: Point2D) => {model.bead.parallelAppliedForce = pt.getX}
  fbdNode.addListener(fbdListener)
  fbdNode.setOffset(10, 10)
  addNode(fbdNode)
  defineInvokeAndPass(freeBodyDiagramModel.addListenerByName) {
    fbdNode.setVisible(freeBodyDiagramModel.visible && !freeBodyDiagramModel.windowed)
  }

  val fbdWindow = new JDialog(frame, "Free Body Diagram", false)
  fbdWindow.setSize(600, 600)
  val canvas = new PhetPCanvas
  val windowFBDNode = new FreeBodyDiagramNode(freeBodyDiagramModel, 600, 600, fbdWidth, fbdWidth, model.coordinateFrameModel, coordinateSystemModel.adjustable, PhetCommonResources.getImage("buttons/minimizeButton.png"))
  windowFBDNode.addListener(fbdListener)
  canvas.addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) = updateNodeSize()
  })
  updateNodeSize()
  def updateNodeSize() = {
    if (canvas.getWidth > 0 && canvas.getHeight > 0) {
      val w = Math.min(canvas.getWidth, canvas.getHeight)
      val inset = 40
      windowFBDNode.setSize(w - inset * 2, w - inset * 2)
      windowFBDNode.setOffset(inset, inset)
    }
  }
  canvas.addScreenChild(windowFBDNode)
  fbdWindow.setContentPane(canvas)
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
    override def windowClosing(e: WindowEvent) = freeBodyDiagramModel.visible = false
  })

  class VectorSetNode(transform: ModelViewTransform2D, bead: Bead) extends PNode {
    def addVector(a: Vector, offset: VectorValue) = {
      val node = new BodyVectorNode(transform, a, offset)
      addChild(node)
    }
  }

  class BodyVectorNode(transform: ModelViewTransform2D, vector: Vector, offset: VectorValue) extends VectorNode(transform, vector, offset) {
    model.bead.addListenerByName {
      setOffset(model.bead.position2D)
      update
    }
  }

  val vectorNode = new VectorSetNode(transform, model.bead)
  addNode(vectorNode)
  def addVectorAllComponents(beadVector: BeadVector with PointOfOriginVector, offsetFBD: VectorValue, offsetPlayArea: Double,
                             selectedVectorVisible: () => Boolean) = {
    addVector(beadVector, offsetFBD, offsetPlayArea)
    val parallelComponent = new ParallelComponent(beadVector, model.bead)
    val perpComponent = new PerpendicularComponent(beadVector, model.bead)
    val xComponent = new XComponent(beadVector, model.bead)
    val yComponent = new YComponent(beadVector, model.bead)
    def update() = {
      yComponent.visible = vectorViewModel.xyComponentsVisible && selectedVectorVisible()
      xComponent.visible = vectorViewModel.xyComponentsVisible && selectedVectorVisible()
      beadVector.visible = vectorViewModel.originalVectors && selectedVectorVisible()
      parallelComponent.visible = vectorViewModel.parallelComponents && selectedVectorVisible()
      perpComponent.visible = vectorViewModel.parallelComponents && selectedVectorVisible()
    }
    vectorViewModel.addListenerByName(update())
    update()

    addVector(xComponent, offsetFBD, offsetPlayArea)
    addVector(yComponent, offsetFBD, offsetPlayArea)
    addVector(parallelComponent, offsetFBD, offsetPlayArea)
    addVector(perpComponent, offsetFBD, offsetPlayArea)
  }

  def addVector(vector: Vector with PointOfOriginVector, offsetFBD: VectorValue, offsetPlayArea: Double) = {
    fbdNode.addVector(vector, offsetFBD)
    windowFBDNode.addVector(vector, offsetFBD)

    val tailLocationInPlayArea = new VectorValue() {
      def addListenerByName(listener: => Unit) = {
        model.bead.addListenerByName(listener)
        vectorViewModel.addListenerByName(listener)
      }

      def getValue = {
        val defaultCenter = model.bead.height / 2.0
        model.bead.position2D + new Vector2D(model.bead.getAngle + PI / 2) *
                (offsetPlayArea + (if (vectorViewModel.centered) defaultCenter else vector.getPointOfOriginOffset(defaultCenter)))
      }

    }
    //todo: make sure this adapter overrides other methods as well, such as getPaint
    val playAreaAdapter = new Vector(vector.color, vector.name, vector.abbreviation, () => vector.getValue * RampDefaults.PLAY_AREA_VECTOR_SCALE, vector.painter) {
      override def visible = vector.visible

      override def visible_=(vis: Boolean) = vector.visible = vis

      override def getPaint = vector.getPaint
    }
    vectorNode.addVector(playAreaAdapter, tailLocationInPlayArea)
  }

  def addVectorAllComponents(a: BeadVector): Unit = addVectorAllComponents(a, new ConstantVectorValue, 0, () => true)
  addVectorAllComponents(model.bead.appliedForceVector)
  addVectorAllComponents(model.bead.gravityForceVector)
  addVectorAllComponents(model.bead.normalForceVector)
  addVectorAllComponents(model.bead.frictionForceVector)
  addVectorAllComponents(model.bead.wallForceVector)
  addVectorAllComponents(model.bead.totalForceVector, new ConstantVectorValue(new Vector2D(0, fbdWidth / 4)), 2, () => vectorViewModel.sumOfForcesVector) //no need to add a separate listener, since it is already contained in vectorviewmodel
}

class RampCanvas(model: RampModel, coordinateSystemModel: CoordinateSystemModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                 vectorViewModel: VectorViewModel, frame: JFrame) extends AbstractRampCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel, frame) {
  addNode(new ObjectSelectionNode(transform, model))
  addNode(new AppliedForceSliderNode(model.bead, transform))

  override def addWallsAndDecorations() = {
    addNode(new BeadNode(model.leftWall, transform, "barrier2.jpg") with CloseButton {
      def model = RampCanvas.this.model
    })
    addNode(new BeadNode(model.rightWall, transform, "barrier2.jpg") with CloseButton {
      def model = RampCanvas.this.model
    })
  }

  def createLeftSegmentNode = new RampSegmentNode(model.rampSegments(0), transform)

  def createRightSegmentNode = new RotatableSegmentNode(model.rampSegments(1), transform)

  def addHeightAndAngleIndicators() = {
    addNode(new RampHeightIndicator(model.rampSegments(1), transform))
    addNode(new RampAngleIndicator(model.rampSegments(1), transform))
  }

  def createEarthNode = new EarthNode(transform)

  def createPusherNode = new PusherNode(transform, model.bead, model.manBead)
}

class RMCCanvas(model: RampModel, coordinateSystemModel: CoordinateSystemModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                vectorViewModel: VectorViewModel, frame: JFrame, airborneFloor: Double, gameModel: RobotMovingCompanyGameModel) extends AbstractRampCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel, frame) {
  val controlPanel = new VerticalLayoutPanel
  controlPanel.setFillNone()
  val robotGoButton = new ScalaButton("Robot Go!", () => {
    gameModel.launched = true
    model.bead.parallelAppliedForce = 0 //leave the pusher behind
    model.setPaused(false)
  })
  gameModel.addListener(() => {robotGoButton.setEnabled(!gameModel.launched)})
  controlPanel.add(robotGoButton)

  def changeY(dy: Double) = {
    val result = model.rampSegments(0).startPoint + new Vector2D(0, dy)
    if (result.y < 1E-8)
      new Vector2D(result.x, 1E-8)
    else
      result
  }

  def updatePosition(dy: Double) = {
    model.rampSegments(0).startPoint = changeY(dy)
    model.bead.setPosition(-model.rampSegments(0).length)
  }

  controlPanel.add(new ScalaButton("Raise Truck", () => updatePosition(0.2)))
  controlPanel.add(new ScalaButton("Lower Truck", () => updatePosition(-0.2)))
  controlPanel.add(Box.createRigidArea(new Dimension(10, 10)))
  controlPanel.add(new ScalaButton("Next Object", () => gameModel.nextObject()))

  val pswingControlPanel = new PSwing(controlPanel)
  addNode(pswingControlPanel)

  pswingControlPanel.setOffset(0, transform.modelToView(0, -1).y)
  fbdNode.setOffset(pswingControlPanel.getFullBounds.getMaxX + 10, pswingControlPanel.getFullBounds.getY)
  freeBodyDiagramModel.visible = true

  val house = model.createBead(8)

  addNode(new BeadNode(house, transform, "robotmovingcompany/house.gif"))
  model.bead.parallelAppliedForce = 1E-16 //to move the pusher to the right spot//todo: fix this with view, not model
  model.bead.notifyListeners() //todo: not sure why this call is necessary; should be handled in previous line

  override def addWallsAndDecorations() = {}

  def createLeftSegmentNode = new ReverseRotatableSegmentNode(model.rampSegments(0), transform)

  def createRightSegmentNode = new RampSegmentNode(model.rampSegments(1), transform)

  def addHeightAndAngleIndicators() = {
    addNode(new RampHeightIndicator(new Reverse(model.rampSegments(0)).reverse, transform))
    addNode(new RampAngleIndicator(new Reverse(model.rampSegments(0)).reverse, transform))
  }

  def createEarthNode = new EarthNodeWithCliff(transform, model.rampSegments(1).length, airborneFloor)

  def createPusherNode = new RobotPusherNode(transform, model.bead, model.manBead)
}
trait PointOfOriginVector {
  def getPointOfOriginOffset(defaultCenter: Double): Double
}