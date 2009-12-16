package edu.colorado.phet.motionseries.charts

import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.scalacommon.Predef._
import javax.swing.{JButton}
import edu.colorado.phet.motionseries.model.MotionSeriesModel

class GoButtonVisibilityModel(model: MotionSeriesModel) extends Observable {
  private var _visible = false

  //if the user modifies the applied force while the sim is paused, the go button should become visible
  //todo: only change visibility if the user drags the slider or types a value, not if they drag the object, since that starts the sim. 
  model.bead.parallelAppliedForceListeners += (() => {
    if (model.isPaused) {
      _visible = true
      notifyListeners()
    }
  })

  def visible = _visible
}

class GoButton(model: GoButtonVisibilityModel) extends JButton("Go!") {
  defineInvokeAndPass(model.addListenerByName) {
    setEnabled(model.visible) //todo: this should be invisible to start with, but that causes layout problems.
  }
}