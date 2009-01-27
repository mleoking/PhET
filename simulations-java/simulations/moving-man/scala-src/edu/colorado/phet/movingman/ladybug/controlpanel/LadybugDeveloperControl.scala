package edu.colorado.phet.movingman.ladybug.controlpanel

import _root_.edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl
import _root_.edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.event.{ChangeListener, ChangeEvent}
import javax.swing.JCheckBox

class LadybugDeveloperControl(module: LadybugModule) extends VerticalLayoutPanel {
  setFillNone

  //public LinearValueControl( double min, double max, double value, String label, String textFieldPattern, String units ) {
  val v = new LinearValueControl(1, 31, LadybugDefaults.WINDOW_SIZE, "V,A window/manual", "0", "samples")
  v.addChangeListener(new ChangeListener() {
    def stateChanged(e: ChangeEvent) = {
      LadybugDefaults.WINDOW_SIZE = v.getValue.toInt
    }
  })

  add(v)

  val checkBox = new JCheckBox("Hide Mouse During Drag", LadybugDefaults.HIDE_MOUSE_DURING_DRAG)
  checkBox.addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) = LadybugDefaults.HIDE_MOUSE_DURING_DRAG = checkBox.isSelected
  })
  add(checkBox)
}