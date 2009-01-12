package edu.colorado.phet.movingman.ladybug

import _root_.scala.collection.mutable.ArrayBuffer

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

class Ladybug extends Observable[Ladybug] {
  var position = new Vector2D
  var velocity = new Vector2D
  var acceleration = new Vector2D

  def translate(dx: Double, dy: Double) = {
    position = new Vector2D(position.x + dx, position.y + dy)
    notifyListeners(this)
  }

  def getPosition: Vector2D = position
}