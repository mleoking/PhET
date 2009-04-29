package edu.colorado.phet.therampscala

import _root_.scala.swing.CheckBox
import collection.mutable.{HashMap, ArrayBuffer}
import common.phetcommon.application.{PhetApplicationConfig, PhetApplicationLauncher, Module}
import common.phetcommon.math.MathUtil
import common.phetcommon.model.BaseModel
import common.phetcommon.view.controls.valuecontrol.LinearValueControl
import common.phetcommon.view.graphics.transforms.{TransformListener, ModelViewTransform2D}
import common.phetcommon.view.util.{PhetFont, BufferedImageUtils}
import common.phetcommon.view.{VerticalLayoutPanel, ResetAllButton}
import common.piccolophet.event.CursorHandler
import common.piccolophet.nodes.PhetPPath
import common.piccolophet.{PiccoloPhetApplication, PhetPCanvas}
import controls.RampControlPanel
import edu.colorado.phet.scalacommon.Predef._
import graphics._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}

import java.awt.geom._
import java.awt.image.BufferedImage
import java.text.DecimalFormat
import javax.sound.sampled.AudioSystem
import javax.swing._
import javax.swing.event.{ChangeListener, ChangeEvent}

import model.{RampModel, BeadState, Bead}
import scalacommon.math.Vector2D
import scalacommon.record.{PlaybackSpeedSlider, RecordModel, RecordModelControlPanel}
import scalacommon.swing.MyRadioButton
import scalacommon.util.Observable
import swing.ScalaValueControl
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.{PPath, PImage, PText}
import umd.cs.piccolo.PNode
import scalacommon.{ScalaApplicationLauncher, ScalaClock}
import java.lang.Math._
import umd.cs.piccolo.util.PDimension
import umd.cs.piccolox.pswing.PSwing

class WordModel extends Observable {
  var _physicsWords = true
  var _everydayWords = false

  resetAll()
  def resetAll() = {
    physicsWords = true
    everydayWords = false
  }

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
  private var _windowed = false
  private var _visible = false
  private var _closable = true

  resetAll()
  def resetAll() = {
    windowed = false
    visible = false
    closable = true
  }

  def closable = _closable

  def closable_=(b: Boolean) = {
    _closable = b
    notifyListeners()
  }

  def visible = _visible

  def windowed = _windowed

  def visible_=(value: Boolean) = {
    _visible = value;
    notifyListeners()
  }

  def windowed_=(value: Boolean) = {
    _windowed = value
    notifyListeners()
  }

}
class CoordinateSystemModel extends Observable {
  private var _fixed = true

  resetAll()
  def resetAll() = {
    fixed = true
  }

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

class VectorViewModel extends Observable {
  private var _centered = true
  private var _originalVectors = true
  private var _parallelComponents = false
  private var _xyComponentsVisible = false
  private var _sumOfForcesVector = false

  resetAll()
  def resetAll() = {
    centered = true
    originalVectors = true
    parallelComponents = false
    xyComponentsVisible = false
    sumOfForcesVector = false
  }

  def centered = _centered

  def centered_=(__centered: Boolean) = {
    _centered = __centered;
    notifyListeners()
  }

  def originalVectors = _originalVectors

  def parallelComponents = _parallelComponents

  def xyComponentsVisible = _xyComponentsVisible

  def sumOfForcesVector = _sumOfForcesVector

  def originalVectors_=(b: Boolean) = {
    _originalVectors = b
    notifyListeners()
  }

  def parallelComponents_=(b: Boolean) = {
    _parallelComponents = b
    notifyListeners()
  }

  def xyComponentsVisible_=(b: Boolean) = {
    _xyComponentsVisible = b
    notifyListeners()
  }

  def sumOfForcesVector_=(b: Boolean) = {
    _sumOfForcesVector = b
    notifyListeners()
  }
}

class AbstractRampModule(frame: JFrame, clock: ScalaClock) extends Module("Ramp", clock) {
  val model = new RampModel
  val wordModel = new WordModel
  val fbdModel = new FreeBodyDiagramModel
  val coordinateSystemModel = new CoordinateSystemModel
  val vectorViewModel = new VectorViewModel
  coordinateSystemModel.addListenerByName(if (coordinateSystemModel.fixed) model.coordinateFrameModel.angle = 0)
  clock.addClockListener(model.update(_))
  def resetRampModule(): Unit = {
    model.resetAll()
    wordModel.resetAll()
    fbdModel.resetAll()
    coordinateSystemModel.resetAll()
    vectorViewModel.resetAll()
  }
}
class RampModule(frame: JFrame, clock: ScalaClock) extends AbstractRampModule(frame, clock) {
  val canvas = new RampCanvas(model, coordinateSystemModel, fbdModel, vectorViewModel, frame)
  setSimulationPanel(canvas)
  setControlPanel(new RampControlPanel(model, wordModel, fbdModel, coordinateSystemModel, vectorViewModel, resetRampModule))
  setClockControlPanel(new RecordModelControlPanel(model, canvas, () => new PlaybackSpeedSlider(model), Color.blue, 20))
}

class RampApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new RampModule(getPhetFrame, new ScalaClock(30, RampDefaults.DT_DEFAULT)))
}

case class Result(success: Boolean, cliff: Boolean, score: Int)
class SurfaceModel extends Observable {
  private var _friction = 0.2

  def friction_=(f: Double) = {
    _friction = f
    notifyListeners()
  }

  def friction = _friction
}
class RobotMovingCompanyGameModel(val model: RampModel, clock: ScalaClock) extends Observable {
  val surfaceModel = new SurfaceModel
  model.rampSegments(1).setAngle(0)
  model.walls = false
  model.rampSegments(0).startPoint = new Vector2D(-10, 0).rotate(-(30.0).toRadians)

  val airborneFloor = -9.0

  private var _launched = false
  private var _objectIndex = 0
  private val resultMap = new HashMap[ScalaRampObject, Result]
  val nextObjectListeners = new ArrayBuffer[(ScalaRampObject) => Unit]
  val itemFinishedListeners = new ArrayBuffer[(ScalaRampObject, Result) => Unit]
  val beadCreatedListeners = new ArrayBuffer[(Bead, ScalaRampObject) => Unit]
  val objectList = RampDefaults.objects
  val housePosition = 8
  val house = model.createBead(housePosition, RampDefaults.house.width, RampDefaults.house.height)
  private var _bead: Bead = null

  nextObjectListeners += (x => setupObject())

  setupObject()
  def bead = _bead

  def setupObject() = {
    val a = selectedObject
    model.setPaused(true)
    _bead = model.createBead(-model.rampSegments(0).length, a.width)
    val beadRef = _bead
    bead.mass = a.mass
    bead.staticFriction = a.staticFriction
    bead.kineticFriction = a.kineticFriction
    bead.crashListeners += (() => {
      RampResources.getAudioClip("smash0.wav").play()
      itemLostOffCliff(a)
    })
    bead.addListener(() => {
      //      println("houseMinX=" + gameModel.house.minX + ", particle: " + bead.position + ", maxX: " + gameModel.house.maxX)
      if (beadRef.position > 0 && abs(beadRef.velocity) < 1E-6 && !containsKey(a)) {
        if (beadRef.position >= house.minX && beadRef.position <= house.maxX) {
          RampResources.getAudioClip("tintagel/DIAMOND.WAV").play()
          itemMoved(a)
        }
        else {
          RampResources.getAudioClip("tintagel/PERSONAL.WAV").play()
          itemLost(a)
        }
      }
    })
    bead.height = a.height
    bead.airborneFloor_=(airborneFloor)

    clock.addClockListener(dt => if (!model.isPaused) beadRef.stepInTime(dt))
    beadCreatedListeners.foreach(_(bead, a))
  }

  def containsKey(a: ScalaRampObject) = resultMap.contains(a)

  def launched_=(b: Boolean) = {_launched = b; notifyListeners()}

  def launched = _launched

  def readyForNext = {
    //ready for next if all items have been scored
    val itemsPrepared = _objectIndex + 1
    val scored = movedItems + lostItems
    scored >= itemsPrepared
  }

  def nextObject() = {
    val lastObject = selectedObject
    _objectIndex = _objectIndex + 1
    nextObjectListeners.foreach(_(lastObject))

    launched = false //notifies listeners
  }

  def itemFinished(o: ScalaRampObject, r: Result) = {
    resultMap += o -> r
    itemFinishedListeners.foreach(_(o, r))

    notifyListeners()
  }

  def itemLostOffCliff(o: ScalaRampObject) = itemFinished(o, Result(false, true, 0))

  def itemLost(o: ScalaRampObject) = itemFinished(o, Result(false, false, 0))

  def itemMoved(o: ScalaRampObject) = itemFinished(o, Result(true, false, 100))

  def count(b: Boolean) = if (b) 1 else 0

  def movedItems = {
    val counts = for (v <- resultMap.values) yield count(v.success)
    counts.foldLeft(0)(_ + _)
  }

  def selectedObject = objectList(_objectIndex)

  def lostItems = {
    val counts = for (v <- resultMap.values) yield count(!v.success)
    counts.foldLeft(0)(_ + _)
  }

  def score = {
    val scores = for (v <- resultMap.values) yield v.score
    scores.foldLeft(0)(_ + _)
  }
}
class RobotMovingCompanyModule(frame: JFrame, clock: ScalaClock) extends AbstractRampModule(frame, clock) {
  val gameModel = new RobotMovingCompanyGameModel(model, clock)
  val canvas = new RMCCanvas(model, coordinateSystemModel, fbdModel, vectorViewModel, frame, gameModel)

  setSimulationPanel(canvas)
  setLogoPanelVisible(false)
}
class RobotMovingCompanyApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new RobotMovingCompanyModule(getPhetFrame, new ScalaClock(30, RampDefaults.DT_DEFAULT)))
}

//Current IntelliJ plugin has trouble finding main for classes with a companion object, so we use a different name 
object RampApplicationMain {
  def main(args: Array[String]) = {
    new PhetApplicationLauncher().launchSim(args, "the-ramp", classOf[RampApplication])
  }
}

object RobotMovingCompanyApplicationMain {
  def main(args: Array[String]) = {
    new PhetApplicationLauncher().launchSim(args, "the-ramp", "robot-moving-company", classOf[RobotMovingCompanyApplication])
  }
}