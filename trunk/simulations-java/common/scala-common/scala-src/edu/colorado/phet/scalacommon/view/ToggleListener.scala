package edu.colorado.phet.scalacommon.view

import edu.umd.cs.piccolo.event.{PInputEventListener, PInputEvent}

//decorator
class ToggleListener(listener: PInputEventListener, isInteractive: () => Boolean) extends PInputEventListener {
  def processEvent(aEvent: PInputEvent, t: Int) = {
    if ( isInteractive() ) {
      listener.processEvent(aEvent, t)
    }
  }
}