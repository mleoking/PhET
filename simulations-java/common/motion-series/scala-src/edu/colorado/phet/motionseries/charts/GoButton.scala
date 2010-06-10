package edu.colorado.phet.motionseries.charts

import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.motionseries.model.MotionSeriesModel
import java.awt.Graphics
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{ImageIcon, JButton}
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PlayPauseButton

class GoButtonVisibilityModel(model: MotionSeriesModel) extends Observable {
  private var _visible = false

  //if the user modifies the applied force while the sim is paused, the go button should become visible
  //only change visibility if the user drags the slider or types a value, not if they drag the object, since that starts the sim. 
  model.bead.parallelAppliedForceListeners += (() => {
    if (model.isPaused) {
      _visible = true
      notifyListeners()
    }
  })

  def visible = _visible
}

class GoButton(model: GoButtonVisibilityModel, goAction: () => Unit) extends JButton(new ImageIcon(new PlayPauseButton(25) {setPlaying(false)}.toImage)) {
  addActionListener(new ActionListener {
    def actionPerformed(e: ActionEvent) = goAction()
  })
  defineInvokeAndPass(model.addListenerByName) {
    setEnabled(model.visible)
  }

  //This unusual workaround makes it so the button is not painted if it is disabled.
  //This is because of:
  //a. the requirement that the go button be invisible when the sim starts (it only appears based on the logic above)
  //b. by using setVisible, the layout changes, making the button appear tiny instead of its normal size
  //c. by using add/remove component, it is necessary to make calls on the parent pswing, since the container is target of a pswing
  //and that has difficulties when adding/removing a component after the pswing has been created.
  //This solution (though unusual) seemed like the one requiring the least amount of coupling between the different components.
  override def paintComponent(g: Graphics) = {
    if (isEnabled) super.paintComponent(g)
  }
}