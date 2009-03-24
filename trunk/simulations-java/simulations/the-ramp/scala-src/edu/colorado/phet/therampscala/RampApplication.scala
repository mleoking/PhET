package edu.colorado.phet.therampscala

import _root_.scala.collection.mutable.ArrayBuffer
import common.phetcommon.application.Module
import common.phetcommon.model.BaseModel
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.PhetFont
import common.phetcommon.view.VerticalLayoutPanel
import common.piccolophet.event.CursorHandler
import common.piccolophet.nodes.PhetPPath
import common.piccolophet.PhetPCanvas
import edu.colorado.phet.scalacommon.Predef._
import java.awt.geom.{Line2D, Rectangle2D, Ellipse2D, Point2D}
import java.awt.{Rectangle, Dimension, BasicStroke, Color}
import javax.swing._
import scalacommon.math.Vector2D
import scalacommon.swing.MyRadioButton
import scalacommon.util.Observable
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode
import scalacommon.{CenteredBoxStrategy, ScalaApplicationLauncher, ScalaClock}

case class RampSegmentState(startPoint: Vector2D, endPoint: Vector2D) { //don't use Point2D since it's not immutable
  def setStartPoint(newStartPoint: Vector2D) = new RampSegmentState(newStartPoint, endPoint)

  def setEndPoint(newEndPoint: Vector2D) = new RampSegmentState(startPoint, newEndPoint)

  def getUnitVector = (endPoint - startPoint).normalize
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

  def getUnitVector = state.getUnitVector
}

class Circle(center: Vector2D, radius: Double) extends Ellipse2D.Double(center.x - radius, center.y - radius, radius * 2, radius * 2)

class RampSegmentNode(rampSegment: RampSegment, transform: ModelViewTransform2D) extends PNode {
  val line = new PhetPPath(new BasicStroke(1f), Color.black)
  addChild(line)

  defineInvokeAndPass(rampSegment.addListenerByName){
    line.setPathTo(rampSegment.toLine2D)
  }

  class SegmentPointNode(getPoint: () => Vector2D, setPoint: (Vector2D) => Unit) extends PNode {
    val node = new PhetPPath(Color.blue)

    def createPath = new Circle(getPoint(), 10)

    addChild(node)
    node.addInputEventListener(new PBasicInputEventHandler() {
      override def mouseDragged(event: PInputEvent) = {
        setPoint(getPoint() + event.getDeltaRelativeTo(node.getParent))
      }
    })
    node.addInputEventListener(new CursorHandler)
    defineInvokeAndPass(rampSegment.addListenerByName){
      node.setPathTo(createPath)
    }
  }

  addChild(new SegmentPointNode(() => rampSegment.startPoint, pt => rampSegment.startPoint = pt))
  addChild(new SegmentPointNode(() => rampSegment.endPoint, pt => rampSegment.endPoint = pt))
}

class BlockState(_position: Vector2D, _velocity: Vector2D) {
  val position = _position
  val velocity = _velocity

  def translate(delta: Vector2D) = new BlockState(position + delta, velocity)
}

object MyRandom extends scala.util.Random

class Block extends Observable {
  var state = new BlockState(new Vector2D(200, 200), new Vector2D(MyRandom.nextDouble() * 30 + 10, MyRandom.nextDouble() * 30 + 10))

  def translate(delta: Vector2D) = {
    state = state.translate(delta)
    notifyListeners()
  }

  def position = state.position

  def velocity = state.velocity
}

case class BeadState(position: Double, velocity: Double, mass: Double, staticFriction: Double, kineticFriction: Double) {
  def translate(dx: Double) = new BeadState(position + dx, velocity, mass, staticFriction, kineticFriction)

  def setVelocity(vel: Double) = new BeadState(position, vel, mass, staticFriction, kineticFriction)

  def thermalEnergy = 0
}
class Bead(_state: BeadState, _rampSegment: RampSegment) extends Observable {
  val gravity = 9.8
  var state = _state
  var rampSegment = _rampSegment

  def position2D = rampSegment.getUnitVector * state.position + rampSegment.startPoint

  _rampSegment.addListenerByName(notifyListeners)
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

  def getTotalEnergy = getPotentialEnergy + getKineticEnergy

  def getPotentialEnergy = mass * gravity * position2D.y

  def getKineticEnergy = 1 / 2 * mass * velocity * velocity
}
class RampModel {
  val rampSegments = new ArrayBuffer[RampSegment]
  val blocks = new ArrayBuffer[Block]
  val beads = new ArrayBuffer[Bead]

  blocks += new Block
  val blockListeners = new ArrayBuffer[Block => Unit]

  rampSegments += new RampSegment(new Point2D.Double(0, 0), new Point2D.Double(100, 100))
  beads += new Bead(new BeadState(50, 19, 5, 0, 0), rampSegments(0))

  def update(dt: Double) = {
    blocks.foreach(b => b.translate(b.velocity * dt))
    beads.foreach(b => newStepCode(b, dt))
  }

  def addRandomBlock() = {
    val block = new Block
    blocks += block
    blockListeners.foreach(_(block))
  }

  case class WorkEnergyState(appliedWork: Double, gravityWork: Double, frictionWork: Double,
                            potentialEnergy: Double, kineticEnergy: Double, totalEnergy: Double)

  def newStepCode(b: Bead, dt: Double) = {
    val origState = b.state
    val forces = getForces(b)
    val netForce = forces.foldLeft(new Vector2D)((a, b) => {a + b})
    //    println("step, net Force=" + netForce)
    val parallelForce = netForce.dot(b.rampSegment.getUnitVector)
    val parallelAccel = parallelForce / b.mass
    //    println("parallel force=" + parallelForce + ", paraccel=" + parallelAccel)
    b.setVelocity(b.velocity + parallelAccel * dt)
    b.translate(b.velocity * dt)
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
    getGravityForce(b) :: Nil
    //    getGravity :: getFriction(b) :: getWallForce(b) :: getNormalForce(b) :: Nil
    //    val netForce=getGravity+getFriction(b)+getNormal
  }

  def getGravityForce(b: Bead) = {
    new Vector2D(0, 9.8) * b.mass //todo: reorient y-axis
  }
}

class BeadNode(bead: Bead, transform: ModelViewTransform2D) extends PNode {
  val node = new PhetPPath(Color.green)
  addChild(node)
  defineInvokeAndPass(bead.addListenerByName){
    node.setPathTo(new Circle(bead.position2D, 10))
  }
}

class BlockNode(b: Block, transform: ModelViewTransform2D) extends PText("Block") {
  addInputEventListener(new CursorHandler)
  setFont(new PhetFont(24, true))
  defineInvokeAndPass(b.addListenerByName){
    setOffset(b.position)
  }
}

class RampCanvas(model: RampModel) extends DefaultCanvas(20, 20) {
  setBackground(new Color(200, 255, 240))
  val blockNode = new BlockNode(model.blocks(0), transform)
  addNode(blockNode)

  model.blockListeners += (b => addNode(new BlockNode(b, transform)))

  addNode(new RampSegmentNode(model.rampSegments(0), transform))
  addNode(new BeadNode(model.beads(0), transform))
}

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
class FreeBodyDiagramModel{
  
}
class RampControlPanel(model: RampModel, wordModel: WordModel,freeBodyDiagramModel:FreeBodyDiagramModel) extends JPanel {
  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))

  val physicsWordButton = new MyRadioButton("Physics words", wordModel.physicsWords = true, wordModel.physicsWords, wordModel.addListener)
  val everydayWordButton = new MyRadioButton("Everyday words", wordModel.everydayWords = true, wordModel.everydayWords, wordModel.addListener)

  add(physicsWordButton)
  add(everydayWordButton)

  add(new JLabel("Free Body Diagram"))
  val showFBD = new MyRadioButton("Show", freeBodyDiagramModel._physicsWords = true, wordModel._physicsWords, wordModel.addListener)
  val showFBD = new MyRadioButton("Hide", wordModel._physicsWords = true, wordModel._physicsWords, wordModel.addListener)
  add()
}

class RampModule(clock: ScalaClock) extends Module("Ramp", clock) {
  val model = new RampModel
  val wordModel = new WordModel
  setSimulationPanel(new RampCanvas(model))
  clock.addClockListener(model.update(_))
  setControlPanel(new RampControlPanel(model, wordModel))
}

object RampApplication {
  def main(args: Array[String]) = {
    ScalaApplicationLauncher.launchApplication(args, "the-ramp", "the-ramp", () => new RampModule(new ScalaClock(30, 30 / 1000.0)))
  }
}