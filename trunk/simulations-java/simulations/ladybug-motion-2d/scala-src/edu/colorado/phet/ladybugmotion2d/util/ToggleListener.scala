package edu.colorado.phet.ladybugmotion2d.util

import umd.cs.piccolo.event.{PInputEventListener, PInputEvent}

//decorator
class ToggleListener(listener: PInputEventListener, isInteractive: () => Boolean) extends PInputEventListener {
  def processEvent(aEvent: PInputEvent, t: Int) = {
    if (isInteractive()) {
      listener.processEvent(aEvent, t)
    }
  }
}