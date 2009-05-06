package edu.colorado.phet.therampscala.robotmovingcompany


import collection.mutable.{HashMap, ArrayBuffer}
import java.awt.Color
import model.{SurfaceFrictionStrategy, RampModel, Bead}
import scalacommon.ScalaClock
import scalacommon.util.Observable
import scalacommon.math.Vector2D
import java.lang.Math._

class RobotMovingCompanyGameModel(val model: RampModel, clock: ScalaClock) extends Observable {
  private val DEFAULT_ROBOT_ENERGY = 3000.0
  private var _robotEnergy = DEFAULT_ROBOT_ENERGY
  val surfaceModel = new SurfaceModel
  val airborneFloor = -9.0

  private var _launched = false
  private var _objectIndex = 0
  private val resultMap = new HashMap[ScalaRampObject, Result]

  //Event notifications
  val beadCreatedListeners = new ArrayBuffer[(Bead, ScalaRampObject) => Unit]
  val itemFinishedListeners = new ArrayBuffer[(ScalaRampObject, Result) => Unit]
  val gameFinishListeners = new ArrayBuffer[() => Unit]

  val objectList = RampDefaults.objects
  val housePosition = 6
  val house = model.createBead(housePosition, RampDefaults.house.width, RampDefaults.house.height)
  private var _bead: Bead = null

  clock.addClockListener(dt => if (!model.isPaused && _bead != null) _bead.stepInTime(dt))

  resetAll()

  def resetAll() = {
    model.rampSegments(1).setAngle(0)
    model.walls = false
    model.rampSegments(0).startPoint = new Vector2D(-10, 0).rotate(-(30.0).toRadians)
    model.rampSegments(0).startPoint = new Vector2D(-10, 0).rotate(-(30.0).toRadians)
    _launched = false
    resultMap.clear()
    setObjectIndex(0)
  }

  def bead = _bead

  def robotEnergy = _robotEnergy

  def setObjectIndex(newIndex: Int) = {
    if (_bead != null) {
      //todo: use remove listener paradigm
      //todo: remove applied force listener
      _bead.remove()
    }

    _objectIndex = newIndex
    _robotEnergy = DEFAULT_ROBOT_ENERGY
    notifyListeners()

    val sel = selectedObject
    model.setPaused(true)

    _bead = model.createBead(-model.rampSegments(0).length, sel.width)

    bead.mass = sel.mass
    bead.staticFriction = sel.staticFriction
    bead.kineticFriction = sel.kineticFriction
    bead.height = sel.height
    bead.airborneFloor = airborneFloor
    bead.surfaceFrictionStrategy = surfaceModel
    bead.crashListeners += (() => itemLostOffCliff(sel))
    val beadRef = _bead //use a reference for closures below
    bead.addListener(() => {
      //      println("houseMinX=" + gameModel.house.minX + ", particle: " + bead.position + ", maxX: " + gameModel.house.maxX)
      if (beadRef.position > 0 && abs(beadRef.velocity) < 1E-6 && !containsKey(sel)) {
        if (beadRef.position >= house.minX && beadRef.position <= house.maxX)
          itemMoved(sel)
        else
          itemLost(sel)
      }
    })

    _bead.workListeners += (work => {
      _robotEnergy = _robotEnergy - abs(work)
      if (_robotEnergy <= 0) {
        _robotEnergy = 0
        _bead.parallelAppliedForce = 0
      }
      notifyListeners()
    })

    launched = false
    beadCreatedListeners.foreach(_(bead, sel))
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

  def nextObject() = setObjectIndex(_objectIndex + 1)

  def itemFinished(o: ScalaRampObject, r: Result) = {
    resultMap += o -> r
    itemFinishedListeners.foreach(_(o, r))
    if (resultMap.size == objectList.length)
      gameFinishListeners.foreach(_())
    //    else //  automatically go to next object when you score or lose the object (instead of hitting "next object" button)
    //      nextObject()
    notifyListeners()
  }

  def itemLostOffCliff(o: ScalaRampObject) = itemFinished(o, Result(false, true, 0))

  def itemLost(o: ScalaRampObject) = itemFinished(o, Result(false, false, 0))

  def itemMoved(o: ScalaRampObject) = itemFinished(o, Result(true, false, 100 + (_robotEnergy / DEFAULT_ROBOT_ENERGY * 100).toInt))

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

case class Result(success: Boolean, cliff: Boolean, score: Int)

case class SurfaceType(name: String, imageFilename: String, strategy: Double => Double, color: Color) extends SurfaceFrictionStrategy {
  def getTotalFriction(objectFriction: Double) = strategy(objectFriction)
}

class SurfaceModel extends Observable with SurfaceFrictionStrategy {
  val surfaceTypes = SurfaceType("Ice", "robotmovingcompany/ice.gif", x => 0.0, new Color(154, 183, 205)) ::
          SurfaceType("Concrete", "robotmovingcompany/concrete.gif", x => x, new Color(146, 154, 160)) ::
          SurfaceType("Carpet", "robotmovingcompany/carpet.gif", x => x * 1.5, new Color(200, 50, 60)) :: Nil
  private var _friction = 0.2
  private var _surfaceType = surfaceTypes(1)

  def surfaceType = _surfaceType

  def surfaceType_=(x: SurfaceType) = {
    _surfaceType = x
    notifyListeners()
  }

  def friction_=(f: Double) = {
    _friction = f
    notifyListeners()
  }

  def friction = _friction

  def getTotalFriction(objectFriction: Double) = _surfaceType.getTotalFriction(objectFriction)
}