package testing

import scala.collection.mutable.ArrayBuffer

class Observable[T] {
  type ListenerType = T => Unit
  val listeners = new ArrayBuffer[ListenerType]

  def notifyListeners(obj: T): Unit = listeners.foreach(_(obj))

  //the following 3 lines are equivalent:
  //1. for (a <- listeners) a(obj)
  //2. listeners.foreach((in: ListenerType) => in(obj))
  //3. listeners.foreach( _(obj))

  def addListener(listener: ListenerType) = listeners += listener
}

class RampObject extends Observable[RampObject] {
  private var mass = 1
  private var position = new Vector2D
  private var v = new Vector2D
  private var a = new Vector2D
  private var staticFrictionCoefficient = 0
  private var kineticFrictionCoefficient = 0
  private var interacting = true
  private var track: RampTrack = null;

  def translate(dx: Double, dy: Double): Unit = {
    position = position + new Vector2D(dx, dy);
    notifyListeners(this)
  }

  override def toString = "position=" + position + ", v=" + v + ", a=" + a + ", mass=" + mass +
          ", staticCoefficient=" + staticFrictionCoefficient + ", kineticCoef=" +
          kineticFrictionCoefficient + ", interacting=" + interacting;
  def getPosition() = position
}

class RampTrack(_start: Vector2D, _end: Vector2D) extends Observable[RampTrack] {
  var start: Vector2D = _start;
  var end: Vector2D = _end;
  def getEndLocation = end;
  def getStartLocation = start;
  def setEndLocation(endLoc: Vector2D) = {
    end = endLoc;
    notifyListeners(this)
  }
  //  var end=end;
}

class RampModel {
  private val objects = new ArrayBuffer[RampObject]
  private val tracks = new ArrayBuffer[RampTrack]

  tracks += new RampTrack(new Vector2D(0, 50), new Vector2D(100, 50))
  objects += new RampObject
  def update(dt: Double): Unit = {
    objects.foreach(_.translate(1, 0.5))
  }

  def getObject(index: Int) = objects(index)

  def getTrack(index: Int) = tracks(index)

  override def toString = "objects=" + objects + ", tracks=" + tracks;
}