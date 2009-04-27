package edu.colorado.phet.therampscala.graphics


import common.phetcommon.resources.PhetCommonResources
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.SwingUtils
import common.piccolophet.event.CursorHandler
import umd.cs.piccolo.nodes.{PImage, PText}
import common.piccolophet.PhetPCanvas
import java.awt.Color
import java.awt.event._

import java.awt.geom.{Point2D, Rectangle2D}
import javax.swing.{JFrame, JDialog}
import model._
import scalacommon.math.Vector2D
import scalacommon.Predef._
import scalacommon.util.Observable
import theramp.model.ValueAccessor.ParallelForceAccessor
import umd.cs.piccolo.event.{PInputEvent, PBasicInputEventHandler}
import umd.cs.piccolo.PNode
import java.lang.Math._

abstract class AbstractRampCanvas(model: RampModel, coordinateSystemModel: CoordinateSystemModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                                  vectorViewModel: VectorViewModel, frame: JFrame) extends DefaultCanvas(22, 20) {
  setBackground(new Color(200, 255, 240))

  addNode(new SkyNode(transform))
  addNode(new EarthNode(transform))

  def getLeftSegmentNode: PNode
  addNode(getLeftSegmentNode)

  def getRightSegmentNode: PNode
  addNode(getRightSegmentNode)

  def addHeightAndAngleIndicators()
  addHeightAndAngleIndicators()

  trait CloseButton extends BeadNode {
    val closeButton = new PImage(PhetCommonResources.getImage("buttons/closeButton.png"))
    closeButton.addInputEventListener(new CursorHandler)

    val openButton = new PImage(PhetCommonResources.getImage("buttons/maximizeButton.png"))
    openButton.addInputEventListener(new CursorHandler)

    addChild(closeButton)
    addChild(openButton)
    update()

    override def update() = {
      super.update()
      if (closeButton != null) {
        closeButton.setOffset(imageNode.getFullBounds.getX, imageNode.getFullBounds.getY)
        openButton.setOffset(imageNode.getFullBounds.getX, imageNode.getFullBounds.getY)
      }
    }
    closeButton.addInputEventListener(new PBasicInputEventHandler {
      override def mousePressed(event: PInputEvent) = model.walls = false
    })
    openButton.addInputEventListener(new PBasicInputEventHandler {
      override def mousePressed(event: PInputEvent) = model.walls = true
    })
    defineInvokeAndPass(model.addListenerByName) {
      imageNode.setVisible(model.walls)
      imageNode.setPickable(model.walls)
      imageNode.setChildrenPickable(model.walls)

      closeButton.setVisible(model.walls)
      closeButton.setPickable(model.walls)
      closeButton.setChildrenPickable(model.walls)

      openButton.setVisible(!model.walls)
      openButton.setPickable(!model.walls)
      openButton.setChildrenPickable(!model.walls)
    }
  }

  def addWalls()
  addWalls()
  addNode(new BeadNode(model.tree, transform, "tree.gif"))

  val cabinetNode = new DraggableBeadNode(model.bead, transform, "cabinet.gif")
  model.addListenerByName(cabinetNode.setImage(RampResources.getImage(model.selectedObject.imageFilename)))
  addNode(cabinetNode)

  addNode(new PusherNode(transform, model.bead, model.manBead))

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

  override def addWalls() = {
    addNode(new BeadNode(model.leftWall, transform, "barrier2.jpg") with CloseButton)
    addNode(new BeadNode(model.rightWall, transform, "barrier2.jpg") with CloseButton)
  }

  def getLeftSegmentNode = new RampSegmentNode(model.rampSegments(0), transform)

  def getRightSegmentNode = new RotatableSegmentNode(model.rampSegments(1), transform)

  def addHeightAndAngleIndicators() = {
    addNode(new RampHeightIndicator(model.rampSegments(1), transform))
    addNode(new RampAngleIndicator(model.rampSegments(1), transform))
  }
}

class RMCCanvas(model: RampModel, coordinateSystemModel: CoordinateSystemModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                vectorViewModel: VectorViewModel, frame: JFrame) extends AbstractRampCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel, frame) {
  override def addWalls() = {}

  def getLeftSegmentNode = new ReverseRotatableSegmentNode(model.rampSegments(0), transform)

  def getRightSegmentNode = new RampSegmentNode(model.rampSegments(1), transform)
  model.bead.parallelAppliedForce = 1E-16 //to move the pusher to the right spot//todo: fix this with view, not model

  def addHeightAndAngleIndicators() = {
    addNode(new RampHeightIndicator(model.rampSegments(0), transform))
    addNode(new RampAngleIndicator(model.rampSegments(0), transform))
  }
}
trait PointOfOriginVector {
  def getPointOfOriginOffset(defaultCenter: Double): Double
}