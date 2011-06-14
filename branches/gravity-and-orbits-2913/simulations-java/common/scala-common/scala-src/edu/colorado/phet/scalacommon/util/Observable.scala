package edu.colorado.phet.scalacommon.util

import scala.collection.mutable.ArrayBuffer

trait Observable {
  private val listeners = new ArrayBuffer[() => Unit]

  def notifyListeners() = listeners.foreach(_())

  //TODO: consider calling the callback function immediately, to avoid the need for things like defineAndInvoke
  //TODO: this would only work for state based (idempotent) notifications, not event based notifications, so maybe shouldn't add callbacks
  //TODO: or maybe should only use this class for idempotent notifications
  def addListener(listener: () => Unit): Unit = listeners += listener

  def addListenerByName(listener: => Unit): Unit = {
    addListener(() => {listener})
  }

  def removeListener(listener: () => Unit):Unit = listeners -= listener

  def removeAllListeners() = listeners.clear()
}