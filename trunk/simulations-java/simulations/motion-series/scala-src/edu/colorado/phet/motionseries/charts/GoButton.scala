package edu.colorado.phet.motionseries.charts

import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.scalacommon.Predef._
import javax.swing.{Timer, JButton}
import java.awt.event.{ActionEvent, ActionListener}
import edu.colorado.phet.motionseries.model.MotionSeriesModel

class GoButtonVisibilityModel(model:MotionSeriesModel) extends Observable {
  private var _visible = false

  model.bead.parallelAppliedForceListeners += (()=>{
    if (model.isPaused) {
      _visible = true
      notifyListeners()
    }
  })

  def visible = _visible
}

class GoButton(model: GoButtonVisibilityModel) extends JButton("Go!") {
  defineInvokeAndPass(model.addListenerByName) {
    setEnabled(model.visible)//todo: this should be invisible to start with, but that causes layout problems. 
  }
}