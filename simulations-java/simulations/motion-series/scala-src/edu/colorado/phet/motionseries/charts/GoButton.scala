package edu.colorado.phet.motionseries.charts

import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.scalacommon.Predef._
import javax.swing.{Timer, JButton}
import java.awt.event.{ActionEvent, ActionListener}

class GoButtonVisibilityModel extends Observable {
  private var _visible = false

  def visible = _visible

  val timer = new Timer(10000, new ActionListener {
    def actionPerformed(e: ActionEvent) = {
      _visible = true
      println("changed to visible")
      notifyListeners()
    }
  })
  timer.start()
}
class GoButton(model: GoButtonVisibilityModel) extends JButton("Go!") {
  defineInvokeAndPass(model.addListenerByName) {
    setVisible(model.visible)
  }
}