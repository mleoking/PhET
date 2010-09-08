package edu.colorado.phet.motionseries.sims.rampforcesandmotion.robotmovingcompany

import collection.mutable.{HashMap, ArrayBuffer}
import edu.colorado.phet.common.phetcommon.math.MathUtil
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.scalacommon.math.Vector2D
import java.lang.Math._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.model._

/**
 * The main model class for the game mode of the Ramps II and Forces and Motion sims.
 * This contains the game logic, not the physical model.
 * @author Sam Reid 
 */
class RobotMovingCompanyGameModel(val model: MotionSeriesModel,
                                  clock: ScalaClock,
                                  initAngle: Double,
                                  val appliedForceAmount: Double,
                                  val objectList: List[MotionSeriesObjectType])
        extends Observable {

  //The robot energy and applied force amounts have to be chosen so that the robot has enough energy
  //to push the heavy objects, and doesn't have so much energy that the small objects fly offscreen too fast
  val DEFAULT_ROBOT_ENERGY = appliedForceAmount * 6
  val energyScale = MotionSeriesDefaults.rampRobotForce / appliedForceAmount / 10.0 //the scale at which to display remaining energy
  private var _robotEnergy = DEFAULT_ROBOT_ENERGY
  val airborneFloor = -9.0 //how far objects fall off the cliff before hitting ground

  def hasUserAppliedForce = {
    robotEnergy > DEFAULT_ROBOT_ENERGY * 0.92
  }

  private var _launched = false
  private var _objectIndex = 0
  val resultMap = new HashMap[MotionSeriesObjectType, Result]

  //Event notifications
  val objectCreatedListeners = new ArrayBuffer[(MotionSeriesObject, MotionSeriesObjectType) => Unit]
  val itemFinishedListeners = new ArrayBuffer[(MotionSeriesObjectType, Result) => Unit]
  val gameFinishListeners = new ArrayBuffer[() => Unit]

  val housePosition = 6
  val house = new MotionSeriesObject(model, housePosition, MotionSeriesDefaults.house.width, MotionSeriesDefaults.house.height)
  val door = new MotionSeriesObject(model, housePosition, MotionSeriesDefaults.door.width, MotionSeriesDefaults.door.height)
  private var _doorOpenAmount = 0.0

  def doorOpenAmount = _doorOpenAmount

  val doorListeners = new ArrayBuffer[() => Unit]
  val doorBackground = new MotionSeriesObject(model, housePosition, MotionSeriesDefaults.doorBackground.width, MotionSeriesDefaults.doorBackground.height)
  private var _motionSeriesObject: MotionSeriesObject = null

  clock.addClockListener(dt => if (!model.isPaused && _motionSeriesObject != null) _motionSeriesObject.stepInTime(dt))
  resetAll()

  def motionSeriesObject = _motionSeriesObject

  def robotEnergy = _robotEnergy

  def _inFrontOfDoor(b: MotionSeriesObject) = {
    val range = 1.15 //NP: I would simply make the grabbing area smaller - maybe 10-15% beyond the door border.
    val leftSide = door.position - door.width / 2.0 * range
    val rightSide = door.position + door.width / 2.0 * range
    b.position >= leftSide && b.position <= rightSide
  }

  val doorHandler = () => {
    val inFrontOfDoor = _inFrontOfDoor(motionSeriesObject)
    if (inFrontOfDoor)
      _doorOpenAmount = _doorOpenAmount + 0.1
    else
      _doorOpenAmount = _doorOpenAmount - 0.1
    _doorOpenAmount = MathUtil.clamp(0, _doorOpenAmount, 1.0)
    doorListeners.foreach(_())
  }
  model.stepListeners += doorHandler

  def resetAll() = {
    model.rampSegments(1).setAngle(0)
    model.walls = false
    model.rampSegments(0).startPoint = new Vector2D(-10, 0).rotate(-initAngle)
    model.rampSegments(0).startPoint = new Vector2D(-10, 0).rotate(-initAngle)
    _launched = false
    resultMap.clear()
    setObjectIndex(0)
  }

  /**
   * Switches to the specified object.
   */
  def setObjectIndex(newIndex: Int) = {
    if (_motionSeriesObject != null) {
      //todo: use remove listener paradigm
      //todo: remove applied force listener
      _motionSeriesObject.remove()
    }

    _objectIndex = newIndex
    _robotEnergy = DEFAULT_ROBOT_ENERGY
    notifyListeners()

    val sel = selectedObject
    model.setPaused(true)

    _motionSeriesObject = new MotionSeriesObject(model, -model.rampSegments(0).length + sel.width / 2.0 + model.leftWall.width / 2.0, sel.width, 3)

    motionSeriesObject.mass = sel.mass
    motionSeriesObject.staticFriction = sel.staticFriction
    motionSeriesObject.kineticFriction = sel.kineticFriction
    motionSeriesObject.height = sel.height
    motionSeriesObject.airborneFloor = airborneFloor
    motionSeriesObject.crashListeners += (() => itemLostOffCliff(sel))

    var lastPushTime = 0L //flag to indicate push + at rest hasn't started yet

    object listener extends Function0[Unit] {
      def apply() = {
        val pushing = motionSeriesObject.parallelAppliedForce.abs > 0
        val atRest = motionSeriesObject.velocity.abs < 1E-6

        val totalForce = motionSeriesObject.totalForce
        //        println("net force mag = "+netForce.magnitude)
        if (pushing && totalForce.magnitude < 1E-8 && lastPushTime == 0) {
          lastPushTime = System.currentTimeMillis
          println("started timer")
        }
        val inFrontOfDoor = _inFrontOfDoor(motionSeriesObject)
        val stoppedAtHouse = inFrontOfDoor && atRest //okay to be pushing
        val stoppedAndOutOfEnergy = atRest && _robotEnergy == 0
        val crashed = atRest && motionSeriesObject.position2D.y < 0 //todo: won't this be wrong if the object falls off slowly?  What about checking for Crashed strategy?
        if (stoppedAtHouse) {
          motionSeriesObject.removeListener(this) //remove listener first, in case itemDelivered causes any notifications (it currently doesn't)
          itemDelivered(sel, motionSeriesObject)
        }
        //TODO: need to make sure object is not about to start sliding back down the ramp
        else if ((stoppedAndOutOfEnergy || crashed) &&
                motionSeriesObject.acceleration <= 1E-6) { //make sure object isn't about to start sliding back down the ramp
          println("item lost, acceleration = " + motionSeriesObject.acceleration)
          motionSeriesObject.removeListener(this) //see note above on ordering
          itemLost(sel)
        }
        //if pushing for 1 sec and still have energy, then should be NotEnoughEnergyToPush
        if (lastPushTime != 0 && System.currentTimeMillis - lastPushTime >= 1000) {
          motionSeriesObject.removeListener(this)
          itemStuck(sel)
        }
      }
    }

    motionSeriesObject.addListener(listener)

    _motionSeriesObject.workListeners += (work => {
      _robotEnergy = _robotEnergy - abs(work)
      if (_robotEnergy <= 0) {
        _robotEnergy = 0
        _motionSeriesObject.parallelAppliedForce = 0
      }
      notifyListeners()
    })

    launched = false
    objectCreatedListeners.foreach(_(motionSeriesObject, sel))
    motionSeriesObject.parallelAppliedForce = 0 //make sure applied force slider sets to zero, have to do this after listeners are attached
  }

  def launched_=(b: Boolean) = {_launched = b; notifyListeners()}

  def launched = _launched

  def readyForNext = {
    //ready for next if all items have been scored
    val itemsPrepared = _objectIndex + 1
    val scored = movedItems + lostItems
    scored >= itemsPrepared
  }

  def nextObject() = setObjectIndex(_objectIndex + 1)

  def itemFinished(o: MotionSeriesObjectType, r: Result) = {
    resultMap += o -> r
    itemFinishedListeners.foreach(_(o, r))
    if (resultMap.size == objectList.length) {
      gameFinishListeners.foreach(_())
    }
    notifyListeners()
  }

  def isLastObject(o: MotionSeriesObjectType) = objectList(objectList.length - 1) eq o

  def itemLostOffCliff(o: MotionSeriesObjectType) = itemFinished(o, Cliff(o.points, robotEnergy.toInt))

  def itemLost(o: MotionSeriesObjectType) = itemFinished(o, OutOfEnergy(o.points, robotEnergy.toInt))

  def itemStuck(o: MotionSeriesObjectType) = itemFinished(o, NotEnoughEnergyToPush(o.points, robotEnergy.toInt))

  val deliverList = new ArrayBuffer[MotionSeriesObject]

  private var _inputAllowed = true

  def inputAllowed = _inputAllowed

  def itemDelivered(o: MotionSeriesObjectType, motionSeriesObjectRef: MotionSeriesObject) = {
    if (!deliverList.contains(motionSeriesObjectRef)) {
      deliverList += motionSeriesObjectRef
      object listener extends Function0[Unit] { //it's an object so we can refer to it as this below
        def apply() = {
          _inputAllowed = false
          motionSeriesObjectRef.parallelAppliedForce = 0.0
          val x = motionSeriesObjectRef.position
          val xf = house.position
          val vel = 0.2 * (if (xf - x > 0) 1 else -1)
          motionSeriesObjectRef.setPosition(motionSeriesObjectRef.position + vel)
          if ((motionSeriesObjectRef.position - house.position).abs <= vel.abs) {
            model.stepListeners -= this
            itemFinished(o, Success(o.points, robotEnergy.toInt))
            _inputAllowed = true
          }
        }
      }
      model.stepListeners += listener
    }
  }

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

/**
 * This class represents the results from a single run, including points and success/failure.
 */
abstract class Result(val objectPoints: Int, val robotEnergy: Int) {
  val pointsPerJoule = scoreMultiplier * 0.1
  val totalObjectPoints = objectPoints * scoreMultiplier
  val totalEnergyPoints = (robotEnergy * pointsPerJoule).toInt

  def score = totalObjectPoints + totalEnergyPoints

  def scoreMultiplier = if (success) 1 else 0

  def success: Boolean
}

case class Success(_objectPoints: Int, _robotEnergy: Int) extends Result(_objectPoints, _robotEnergy) {def success = true}
case class Cliff(_objectPoints: Int, _robotEnergy: Int) extends Result(_objectPoints, _robotEnergy) {def success = false}

//This case occurs when the object missed the door, but the robot is out of energy
case class OutOfEnergy(_objectPoints: Int, _robotEnergy: Int) extends Result(_objectPoints, _robotEnergy) {def success = false} //object can't be moved any further

//This case occurs when the robot has some energy, but not enough to move the object
case class NotEnoughEnergyToPush(_objectPoints: Int, _robotEnergy: Int) extends Result(_objectPoints, _robotEnergy) {def success = false} //object can't be moved any further