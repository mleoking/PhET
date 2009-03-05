package edu.colorado.phet.scalacommon.util

import scala.collection.mutable.ArrayBuffer

trait Observable {
  private val listeners = new ArrayBuffer[() => Unit]

  def notifyListeners() = listeners.foreach(_())

  def addListener(listener: () => Unit): Unit = listeners += listener

  def addListenerByName(listener: => Unit): Unit = {
    addListener(() => {listener})
  }

  def removeListener(listener: () => Unit) = listeners -= listener
}