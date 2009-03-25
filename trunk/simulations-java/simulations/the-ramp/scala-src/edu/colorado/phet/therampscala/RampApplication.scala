package edu.colorado.phet.therampscala

import _root_.scala.collection.mutable.ArrayBuffer
import _root_.scala.swing.CheckBox
import common.phetcommon.application.Module
import common.phetcommon.math.MathUtil
import common.phetcommon.model.BaseModel
import common.phetcommon.view.controls.valuecontrol.LinearValueControl
import common.phetcommon.view.graphics.transforms.{TransformListener, ModelViewTransform2D}
import common.phetcommon.view.util.{PhetFont, BufferedImageUtils}
import common.phetcommon.view.{VerticalLayoutPanel, ResetAllButton}
import common.piccolophet.event.CursorHandler
import common.piccolophet.nodes.PhetPPath
import common.piccolophet.PhetPCanvas
import edu.colorado.phet.scalacommon.Predef._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}

import java.awt.geom._
import java.awt.image.BufferedImage
import javax.swing._
import javax.swing.event.{ChangeListener, ChangeEvent}

import scalacommon.math.Vector2D
import scalacommon.swing.MyRadioButton
import scalacommon.util.Observable
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.{PPath, PImage, PText}
import umd.cs.piccolo.PNode
import scalacommon.{CenteredBoxStrategy, ScalaApplicationLauncher, ScalaClock}
import java.lang.Math._
import umd.cs.piccolo.util.PDimension
import umd.cs.piccolox.pswing.PSwing

case class RampSegmentState(startPoint: Vector2D, endPoint: Vector2D) { //don't use Point2D since it's not immutable
  def setStartPoint(newStartPoint: Vector2D) = new RampSegmentState(newStartPoint, endPoint)

  def setEndPoint(newEndPoint: Vector2D) = new RampSegmentState(startPoint, newEndPoint)

  def getUnitVector = (endPoint - startPoint).normalize

  def setAngle(angle: Double) = new RampSegmentState(startPoint, new Vector2D(angle) * (endPoint - startPoint).magnitude)
}
class RampSegment(_state: RampSegmentState) extends Observable {
  var state = _state;
  def this(startPt: Point2D, endPt: Point2D) = this (new RampSegmentState(startPt, endPt))

  def toLine2D = new Line2D.Double(state.startPoint, state.endPoint)

  def startPoint = state.startPoint

  def endPoint = state.endPoint

  def startPoint_=(pt: Vector2D) = {
    state = state.setStartPoint(pt)
    notifyListeners()
  }

  def endPoint_=(pt: Vector2D) = {
    state = state.setEndPoint(pt)
    notifyListeners()
  }

  def length = (endPoint - startPoint).magnitude

  def getUnitVector = state.getUnitVector

  def setAngle(angle: Double) = {
    state = state.setAngle(angle)
    notifyListeners()
  }
}

class Circle(center: Vector2D, radius: Double) extends Ellipse2D.Double(center.x - radius, center.y - radius, radius * 2, radius * 2)

class RampSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D) extends PNode {
  val line = new PhetPPath(new Color(184, 131, 24), new BasicStroke(2f), new Color(91, 78, 49))
  addChild(line)
  defineInvokeAndPass(rampSegment.addListenerByName){
    line.setPathTo(mytransform.createTransformedShape(new BasicStroke(0.4f).createStrokedShape(rampSegment.toLine2D)))
  }
}

class RotatableSegmentNode(rampSegment: RampSegment, mytransform: ModelViewTransform2D) extends RampSegmentNode(rampSegment, mytransform) {
  line.addInputEventListener(new CursorHandler)
  line.addInputEventListener(new PBasicInputEventHandler {
    override def mouseDragged(event: PInputEvent) = {
      val modelPt = mytransform.viewToModel(event.getPositionRelativeTo(line.getParent))

      val deltaView = event.getDeltaRelativeTo(line.getParent)
      val deltaModel = mytransform.viewToModelDifferential(deltaView.width, deltaView.height)

      val oldPtModel = modelPt - deltaModel

      val oldAngle = (rampSegment.startPoint - oldPtModel).getAngle
      val newAngle = (rampSegment.startPoint - modelPt).getAngle

      val deltaAngle = newAngle - oldAngle

      //draw a ray from start point to new mouse point
      val newPt = new Vector2D(rampSegment.getUnitVector.getAngle + deltaAngle) * rampSegment.length
      val clamped =
      if (newPt.getAngle < 0) new Vector2D(0) * rampSegment.length
      else if (newPt.getAngle > PI / 2) new Vector2D(PI / 2) * rampSegment.length
      else newPt
      rampSegment.endPoint = clamped
    }
  })
}

object MyRandom extends scala.util.Random

case class BeadState(position: Double, velocity: Double, mass: Double, staticFriction: Double, kineticFriction: Double) {
  def translate(dx: Double) = setPosition(position + dx)

  def setPosition(pos: Double) = new BeadState(pos, velocity, mass, staticFriction, kineticFriction)

  def setVelocity(vel: Double) = new BeadState(position, vel, mass, staticFriction, kineticFriction)

  def setMass(m: Double) = new BeadState(position, velocity, m, staticFriction, kineticFriction)

  def thermalEnergy = 0
}
class Bead(_state: BeadState, positionMapper: Double => Vector2D, rampSegmentAccessor: Double => RampSegment, model: Observable) extends Observable {
  val gravity = -9.8
  var state = _state
  var _appliedForce = new Vector2D

  def appliedForce = _appliedForce

  def appliedForce_=(force: Vector2D) = {
    _appliedForce = force
    notifyListeners()
  }

  def position2D = positionMapper(position)

  def getRampUnitVector = rampSegmentAccessor(position).getUnitVector

  model.addListenerByName(notifyListeners)
  def mass = state.mass

  def position = state.position

  def velocity = state.velocity

  def translate(dx: Double) = {
    state = state.translate(dx)
    notifyListeners()
  }

  def getStaticFriction = state.staticFriction

  def getKineticFriction = state.kineticFriction

  def setVelocity(velocity: Double) = {
    state = state.setVelocity(velocity)
    notifyListeners()
  }

  def mass_=(mass: Double) = {
    state = state.setMass(mass)
    notifyListeners()
  }

  def setPosition(position: Double) = {
    state = state.setPosition(position)
    notifyListeners()
  }

  def getTotalEnergy = getPotentialEnergy + getKineticEnergy

  def getPotentialEnergy = mass * gravity * position2D.y

  def getKineticEnergy = 1 / 2 * mass * velocity * velocity

  def getAngleInvertY = {
    val vector = rampSegmentAccessor(position).getUnitVector
    val vectorInvertY = new Vector2D(vector.x, -vector.y)
    vectorInvertY.getAngle
  }
}
class RampModel extends Observable {
  val rampSegments = new ArrayBuffer[RampSegment]
  val beads = new ArrayBuffer[Bead]
  private var _walls = true
  private var _frictionless = false
  private var _selectedObject = RampDefaults.objects(0)

  def selectedObject = _selectedObject

  def selectedObject_=(obj: ScalaRampObject) = {
    _selectedObject = obj
    beads(0).mass = _selectedObject.mass
    notifyListeners()
  }

  def walls = _walls

  def frictionless = _frictionless

  def walls_=(b: Boolean) = {
    _walls = b
    notifyListeners()
  }

  def frictionless_=(b: Boolean) = {
    _frictionless = b
    notifyListeners()
  }

  rampSegments += new RampSegment(new Point2D.Double(-10, 0), new Point2D.Double(0, 0))
  rampSegments += new RampSegment(new Point2D.Double(0, 0), new Point2D.Double(10 * sin(PI / 4), 10 * sin(PI / 4)))

  def setRampAngle(angle: Double) = {
    rampSegments(1).setAngle(angle)
  }

  //TODO: this may need to be more general
  def positionMapper(particleLocation: Double) = {
    if (particleLocation <= 0) rampSegments(0).getUnitVector * (10 + particleLocation) + rampSegments(0).startPoint
    else rampSegments(1).getUnitVector * (particleLocation) + rampSegments(1).startPoint
  }

  def rampSegmentAccessor(particleLocation: Double) = {
    if (particleLocation <= 0) rampSegments(0) else rampSegments(1)
  }

  //Sends notification when any ramp segment changes
  object rampChangeAdapter extends Observable //todo: perhaps we should just pass the addListener method to the beads
  rampSegments(0).addListenerByName{rampChangeAdapter.notifyListeners}
  rampSegments(1).addListenerByName{rampChangeAdapter.notifyListeners}
  beads += new Bead(new BeadState(5, 0, 10, 0, 0), positionMapper, rampSegmentAccessor, rampChangeAdapter)
  val tree = new Bead(new BeadState(-9, 0, 10, 0, 0), positionMapper, rampSegmentAccessor, rampChangeAdapter)
  val leftWall = new Bead(new BeadState(-10, 0, 10, 0, 0), positionMapper, rampSegmentAccessor, rampChangeAdapter)
  val rightWall = new Bead(new BeadState(10, 0, 10, 0, 0), positionMapper, rampSegmentAccessor, rampChangeAdapter)

  val manBead = new Bead(new BeadState(2, 0, 10, 0, 0), positionMapper, rampSegmentAccessor, rampChangeAdapter)

  def update(dt: Double) = {
    beads.foreach(b => newStepCode(b, dt))
  }

  case class WorkEnergyState(appliedWork: Double, gravityWork: Double, frictionWork: Double,
                            potentialEnergy: Double, kineticEnergy: Double, totalEnergy: Double)

  def newStepCode(b: Bead, dt: Double) = {
    val origState = b.state
    val forces = getForces(b)
    val netForce = forces.foldLeft(new Vector2D)((a, b) => {a + b})
    val parallelForce = netForce.dot(b.getRampUnitVector)
    val parallelAccel = parallelForce / b.mass
    b.setVelocity(b.velocity + parallelAccel * dt)

    val requestedPosition = b.position + b.velocity * dt

    //TODO: generalize boundary code
    if (requestedPosition <= -10) {
      b.setVelocity(0)
      b.setPosition(-10)
    }
    else if (requestedPosition >= 10) {
      b.setVelocity(0)
      b.setPosition(10)
    }
    else {
      b.setPosition(requestedPosition)
    }
    val justCollided = false

    if (b.getStaticFriction == 0 && b.getKineticFriction == 0) {
      val appliedWork = b.getTotalEnergy
      val gravityWork = -b.getPotentialEnergy
      val thermalEnergy = origState.thermalEnergy
      if (justCollided) {
        //        thermalEnergy += origState.kineticEnergy
      }
      val frictionWork = -thermalEnergy
      frictionWork
      new WorkEnergyState(appliedWork, gravityWork, frictionWork,
        b.getPotentialEnergy, b.getKineticEnergy, b.getTotalEnergy)
    } else {
      //      val dW=getAppliedWorkDifferential
      //      val appliedWork=origState.appliedWork
      //      val gravityWork=-getPotentialEnergy
      //      val etot=appliedWork
      //      val thermalEnergy=etot-kineticEnergy-potentialEnergy
      //      val frictionWork=-thermalEnergy

    }
  }

  def getForces(b: Bead) = {
    getGravityForce(b) :: b.appliedForce :: Nil
    //    getGravity :: getFriction(b) :: getWallForce(b) :: getNormalForce(b) :: Nil
    //    val netForce=getGravity+getFriction(b)+getNormal
  }

  def getGravityForce(b: Bead) = {
    new Vector2D(0, -9.8) * b.mass
  }
}

class DraggableBeadNode(bead: Bead, transform: ModelViewTransform2D, imageName: String) extends BeadNode(bead, transform, imageName) {
  addInputEventListener(new CursorHandler)
  addInputEventListener(new PBasicInputEventHandler() {
    override def mouseDragged(event: PInputEvent) = {
      val delta = event.getCanvasDelta
      val modelDelta = transform.viewToModelDifferential(delta.width, delta.height)
      bead.appliedForce = (bead.appliedForce + modelDelta)
    }

    override def mouseReleased(event: PInputEvent) = {
      bead.appliedForce = new Vector2D
    }
  })
}

class BeadNode(bead: Bead, transform: ModelViewTransform2D, imageName: String) extends PNode {
  val shapeNode = new PhetPPath(Color.green)
  //  addChild(shapeNode)//TODO remove after debug done

  val cabinetImage = RampResources.getImage(imageName)
  val imageNode = new PImage(cabinetImage)

  def setImage(im: BufferedImage) = imageNode.setImage(im)
  addChild(imageNode)

  defineInvokeAndPass(bead.addListenerByName){
    shapeNode.setPathTo(transform.createTransformedShape(new Circle(bead.position2D, 0.3)))

    //TODO consolidate/refactor with BugNode, similar graphics transform code
    imageNode.setTransform(new AffineTransform)

    val modelPosition = bead.position2D
    val viewPosition = transform.modelToView(modelPosition)
    val delta = new Vector2D(imageNode.getImage.getWidth(null), imageNode.getImage.getHeight(null))

    //todo: why is scale factor 4 here?
    val scale = transform.modelToViewDifferentialXDouble(1 * 4) / cabinetImage.getWidth

    imageNode.translate(viewPosition.x - delta.x / 2 * scale, viewPosition.y - delta.y * scale)
    imageNode.scale(scale)
    imageNode.rotateAboutPoint(bead.getAngleInvertY,
      imageNode.getFullBounds.getCenter2D.getX - (viewPosition.x - delta.x / 2),
      imageNode.getFullBounds.getMaxY - (viewPosition.y - delta.y))

  }
}


//see scala duck typing
//maybe we should replace this with a named trait
class ObjectSelectionNode(transform: ModelViewTransform2D, model: {def selectedObject: ScalaRampObject; def selectedObject_=(ro: ScalaRampObject): Unit; def addListenerByName(listener: => Unit): Unit}) extends PNode {
  val objects = RampDefaults.objects
  val rows = new ArrayBuffer[ArrayBuffer[PNode]]

  class ObjectSelectionIcon(o: ScalaRampObject) extends PNode {
    val textNode = new PText(o.name)
    val imageNode = new PImage(BufferedImageUtils.multiScaleToHeight(RampResources.getImage(o.imageFilename), 100))
    imageNode.scale(0.5f)
    textNode.scale(1.4f)
    textNode.setOffset(imageNode.getFullBounds.getWidth, 0)

    val backgroundNode = new PhetPPath(new BasicStroke(1f), new Color(0, 0, 0, 0))

    addChild(backgroundNode)
    addChild(imageNode)
    addChild(textNode)

    def updateSelected() = {
      if (model.selectedObject == o) {
        backgroundNode.setPaint(new Color(0, 0, 255, 128))
      } else {
        backgroundNode.setPaint(new Color(0, 0, 0, 0))
      }
    }
    addInputEventListener(new PBasicInputEventHandler {
      override def mousePressed(event: PInputEvent) = {
        model.selectedObject = o
      }
    })
    updateSelected()
    model.addListenerByName{updateSelected()}
  }

  val nodes = for (o <- objects) yield {
    new ObjectSelectionIcon(o)
  }

  val cellDim = nodes.foldLeft(new PDimension)((a, b) => new PDimension(max(a.width, b.getFullBounds.width), max(a.height, b.getFullBounds.height)))

  val modelCellDimPt = transform.viewToModelDifferential(cellDim.width, cellDim.height)
  for (i <- 0 until nodes.length) {
    val row = i / RampDefaults.objectsPerRow
    val column = i % RampDefaults.objectsPerRow

    val n = nodes(i)
    n.backgroundNode.setPathTo(new Rectangle2D.Double(0, 0, cellDim.width, cellDim.height))
    n.setOffset(transform.modelToView(column * modelCellDimPt.x - 10, row * modelCellDimPt.y - 5))
    addChild(n)
  }

}

class RampHeightIndicator(rampSegment: RampSegment, transform: ModelViewTransform2D) extends PNode {
  val line = new PhetPPath(new BasicStroke(2f), Color.black)
  addChild(line)
  def getLine = new Line2D.Double(new Vector2D(rampSegment.endPoint.x, 0), rampSegment.endPoint)
  defineInvokeAndPass(rampSegment.addListenerByName){
    line.setPathTo(transform.createTransformedShape(getLine))
  }
}

//todo: consider coalescing with RampHeightIndicator
class RampAngleIndicator(rampSegment: RampSegment, transform: ModelViewTransform2D) extends PNode {
  val line = new PhetPPath(new BasicStroke(2f), Color.black)
  addChild(line)
  def getPath = {
    val arc = new Arc2D.Double(rampSegment.startPoint.x - 3, rampSegment.startPoint.y - 3, 6, 6, 0, -rampSegment.getUnitVector.getAngle.toDegrees, Arc2D.OPEN)
    arc
  }
  defineInvokeAndPass(rampSegment.addListenerByName){
    line.setPathTo(transform.createTransformedShape(getPath))
  }
}
class RampCanvas(model: RampModel) extends DefaultCanvas(22, 20) {
  setBackground(new Color(200, 255, 240))

  addNode(new SkyNode(transform))
  addNode(new EarthNode(transform))

  addNode(new RampSegmentNode(model.rampSegments(0), transform))
  addNode(new RotatableSegmentNode(model.rampSegments(1), transform))

  addNode(new RampHeightIndicator(model.rampSegments(1), transform))
  addNode(new RampAngleIndicator(model.rampSegments(1), transform))

  addNode(new BeadNode(model.leftWall, transform, "barrier2.jpg"))
  addNode(new BeadNode(model.rightWall, transform, "barrier2.jpg"))
  addNode(new BeadNode(model.tree, transform, "tree.gif"))

  val cabinetNode = new DraggableBeadNode(model.beads(0), transform, "cabinet.gif")
  model.addListenerByName(cabinetNode.setImage(RampResources.getImage(model.selectedObject.imageFilename)))
  addNode(cabinetNode)

  addNode(new PusherNode(transform, model.beads(0), model.manBead))
  addNode(new AppliedForceSliderNode(model.beads(0), transform))

  addNode(new ObjectSelectionNode(transform, model))
}

class PusherNode(transform: ModelViewTransform2D, targetBead: Bead, manBead: Bead) extends BeadNode(manBead, transform, "standing-man.png") {
  defineInvokeAndPass(targetBead.addListenerByName){
    if (targetBead.appliedForce.magnitude > 0) {
      val dx = if (targetBead.appliedForce.x > 0) -6 else 6
      manBead.setPosition(targetBead.position + dx)

      //images go 0 to 14
      val leanAmount = (abs(targetBead.appliedForce.x) * 13.0 / 50.0).toInt + 1
      var textStr = "" + leanAmount
      while (textStr.length < 2)
        textStr = "0" + textStr
      val im = RampResources.getImage("pusher-leaner-png/pusher-leaning-2_00" + textStr + ".png")
      val realIm = if (dx > 0) BufferedImageUtils.flipX(im) else im //todo: cache instead of flipping each time
      setImage(realIm)
    }
    else {
      setImage(RampResources.getImage("standing-man.png"))
    }
  }
  setPickable(false)
  setChildrenPickable(false)
}
class AppliedForceSliderNode(bead: Bead, transform: ModelViewTransform2D) extends PNode {
  val control = new ScalaValueControl(-50, 50, "Applied Force X", "0.0", "N",
    bead.appliedForce.x, value => bead.appliedForce = new Vector2D(value, 0), bead.addListener)

  val pswing = new PSwing(control)
  addChild(pswing)
  def updatePosition() = {
    val viewLoc = transform.modelToView(new Point2D.Double(0, -1))
    pswing.setOffset(viewLoc)
    pswing.setScale(1.2f)
  }
  updatePosition()
}

class AbstractBackgroundNode(getPaint: => Paint, getModelShape: => Shape, transform: ModelViewTransform2D) extends PNode {
  val node = new PhetPPath(getPaint)
  addChild(node)

  def updatePath() = {
    val viewPath = transform.createTransformedShape(getModelShape)
    node.setPathTo(viewPath)
  }
  updatePath()

  transform.addTransformListener(new TransformListener() {
    def transformChanged(mvt: ModelViewTransform2D) = updatePath()
  })
}
class SkyNode(transform: ModelViewTransform2D) extends AbstractBackgroundNode(new GradientPaint(transform.modelToView(0, 0), new Color(250, 250, 255), transform.modelToView(0, 10), new Color(202, 187, 255)), new Rectangle2D.Double(-100, 0, 200, 200), transform)
class EarthNode(transform: ModelViewTransform2D) extends AbstractBackgroundNode(new Color(200, 240, 200), new Rectangle2D.Double(-100, -200, 200, 200), transform)

class WordModel extends Observable {
  var _physicsWords = true
  var _everydayWords = false

  def physicsWords_=(v: Boolean) = {
    _physicsWords = v
    _everydayWords = !_physicsWords

    notifyListeners()
  }

  def physicsWords = _physicsWords

  def everydayWords = _everydayWords

  def everydayWords_=(v: Boolean) = {
    _everydayWords = v
    _physicsWords = !v
    notifyListeners()
  }
}
class FreeBodyDiagramModel extends Observable {
  private var _visible = false

  def visible = _visible

  def visible_=(value: Boolean) = {
    _visible = value;
    notifyListeners()
  }

}
class CoordinateSystemModel extends Observable {
  private var _fixed = true

  def fixed = _fixed

  def adjustable = !_fixed

  def fixed_=(b: Boolean) = {
    _fixed = b
    notifyListeners()
  }

  def adjustable_=(b: Boolean) = {
    _fixed = !b
    notifyListeners()
  }
}
class RampControlPanel(model: RampModel, wordModel: WordModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                      coordinateSystemModel: CoordinateSystemModel, vectorViewModel: VectorViewModel) extends VerticalLayoutPanel {
  //  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))

  add(new MyRadioButton("Physics words", wordModel.physicsWords = true, wordModel.physicsWords, wordModel.addListener))
  add(new MyRadioButton("Everyday words", wordModel.everydayWords = true, wordModel.everydayWords, wordModel.addListener))

  add(new JLabel("Free Body Diagram"))
  add(new MyRadioButton("Show", freeBodyDiagramModel.visible = true, freeBodyDiagramModel.visible, freeBodyDiagramModel.addListener))
  add(new MyRadioButton("Hide", freeBodyDiagramModel.visible = false, !freeBodyDiagramModel.visible, freeBodyDiagramModel.addListener))

  add(new JLabel("Coordinate System"))
  add(new MyRadioButton("Fixed", coordinateSystemModel.fixed = true, coordinateSystemModel.fixed, coordinateSystemModel.addListener))
  add(new MyRadioButton("Adjustable", coordinateSystemModel.adjustable = true, coordinateSystemModel.adjustable, coordinateSystemModel.addListener))

  class SubControlPanel(title: String) extends VerticalLayoutPanel {
    add(new JLabel(title))
    setBorder(BorderFactory.createRaisedBevelBorder)
  }

  val vectorPanel = new SubControlPanel("Vectors")
  vectorPanel.add(new MyCheckBox("Original Vectors", vectorViewModel.originalVectors_=, vectorViewModel.originalVectors, vectorViewModel.addListener))
  vectorPanel.add(new MyCheckBox("Parallel Components", vectorViewModel.parallelComponents_=, vectorViewModel.parallelComponents, vectorViewModel.addListener))
  vectorPanel.add(new MyCheckBox("X-Y Components", vectorViewModel.xyComponents_=, vectorViewModel.xyComponents, vectorViewModel.addListener))
  vectorPanel.add(new MyCheckBox("Sum of Forces Vector", vectorViewModel.sumOfForcesVector_=, vectorViewModel.sumOfForcesVector, vectorViewModel.addListener))

  add(vectorPanel)

  val rampPanel = new SubControlPanel("Ramp Controls")
  rampPanel.add(new MyCheckBox("Walls", model.walls_=, model.walls, model.addListener))
  rampPanel.add(new MyCheckBox("Frictionless", model.frictionless_=, model.frictionless, model.addListener))

  add(rampPanel)

  val positionSlider = new ScalaValueControl(RampDefaults.MIN_X, RampDefaults.MAX_X, "Object Position", "0.0", "meters",
    model.beads(0).position, model.beads(0).setPosition, model.beads(0).addListener)
  add(positionSlider)

  val angleSlider = new ScalaValueControl(0, 90, "Ramp Angle", "0.0", "degrees",
    model.rampSegments(1).getUnitVector.getAngle.toDegrees, value => model.setRampAngle(value.toRadians), model.rampSegments(1).addListener)

  add(angleSlider)

  val resetButton = new ResetAllButton(this)
  add(resetButton)
}
class MyCheckBox(text: String, setter: Boolean => Unit, getter: => Boolean, addListener: (() => Unit) => Unit) extends CheckBox(text) {
  addListener(update)
  update()
  peer.addActionListener(new ActionListener() {
    def actionPerformed(ae: ActionEvent) = setter(peer.isSelected)
  });
  def update() = peer.setSelected(getter)
}
class VectorViewModel extends Observable {
  private var _originalVectors = true
  private var _parallelComponents = false
  private var _xyComponents = false
  private var _sumOfForcesVector = false

  def originalVectors = _originalVectors

  def parallelComponents = _parallelComponents

  def xyComponents = _xyComponents

  def sumOfForcesVector = _sumOfForcesVector

  def originalVectors_=(b: Boolean) = {
    _originalVectors = b
    notifyListeners()
  }

  def parallelComponents_=(b: Boolean) = {
    _parallelComponents = b
    notifyListeners()
  }

  def xyComponents_=(b: Boolean) = {
    _xyComponents = b
    notifyListeners()
  }

  def sumOfForcesVector_=(b: Boolean) = {
    _sumOfForcesVector = b
    notifyListeners()
  }
}
class RampModule(clock: ScalaClock) extends Module("Ramp", clock) {
  val model = new RampModel
  val wordModel = new WordModel
  val fbdModel = new FreeBodyDiagramModel
  val coordinateSystemModel = new CoordinateSystemModel
  val vectorViewModel = new VectorViewModel
  setSimulationPanel(new RampCanvas(model))
  clock.addClockListener(model.update(_))
  setControlPanel(new RampControlPanel(model, wordModel, fbdModel, coordinateSystemModel, vectorViewModel))
}

class ScalaValueControl(min: Double, max: Double, name: String, decimalFormat: String, units: String,
                       getter: => Double, setter: Double => Unit, addListener: (() => Unit) => Unit) extends LinearValueControl(min, max, name, decimalFormat, units) {
  addListener(update)
  update()
  addChangeListener(new ChangeListener {
    def stateChanged(e: ChangeEvent) = setter(getValue)
  });
  def update() = setValue(getter)
}

object RampApplication {
  def main(args: Array[String]) = {
    ScalaApplicationLauncher.launchApplication(args, "the-ramp", "the-ramp", () => new RampModule(new ScalaClock(30, 30 / 1000.0)))
  }
}