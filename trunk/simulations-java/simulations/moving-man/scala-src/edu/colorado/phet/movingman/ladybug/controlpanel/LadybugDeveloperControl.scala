package edu.colorado.phet.movingman.ladybug.controlpanel

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.event.{ChangeListener, ChangeEvent}
import javax.swing.JCheckBox
import model.LadybugModel

class LadybugDeveloperControl[M<:LadybugModel](module: LadybugModule[M]) extends VerticalLayoutPanel {
  setFillNone

  val v = new LinearValueControl(1, 31, LadybugDefaults.WINDOW_SIZE, "V,A window/manual", "0", "samples")
  v.addChangeListener(new ChangeListener() {
    def stateChanged(e: ChangeEvent) = {
      LadybugDefaults.WINDOW_SIZE = v.getValue.toInt
    }
  })

  val checkBox = new JCheckBox("Hide Mouse During Drag", LadybugDefaults.HIDE_MOUSE_DURING_DRAG)
  checkBox.addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) = LadybugDefaults.HIDE_MOUSE_DURING_DRAG = throwBox.isSelected
  })
  //  add(checkBox)


  val throwBox = new JCheckBox("Frictionless", module.model.isFrictionless)
  throwBox.addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) = module.model.setFrictionless(throwBox.isSelected)
  })
  module.model.addListenerByName(throwBox.setSelected(module.model.isFrictionless))
  add(throwBox)

}