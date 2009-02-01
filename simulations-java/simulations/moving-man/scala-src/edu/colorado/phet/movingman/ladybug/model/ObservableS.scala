package edu.colorado.phet.movingman.ladybug.model

import _root_.scala.collection.mutable.ArrayBuffer

trait ObservableS {
  private val listeners = new ArrayBuffer[() => Unit]

  def notifyListeners() = listeners.foreach(_())

  def addListener(listener: () => Unit) = listeners += listener

  def removeListener(listener: () => Unit) = listeners -= listener
}