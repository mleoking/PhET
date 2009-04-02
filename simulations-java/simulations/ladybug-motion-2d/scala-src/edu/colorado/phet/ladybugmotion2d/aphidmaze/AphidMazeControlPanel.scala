package edu.colorado.phet.ladybugmotion2d.aphidmaze

import _root_.edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl
import controlpanel.LadybugControlPanel
import javax.swing.event.{ChangeListener, ChangeEvent}

import javax.swing.JButton

class AphidMazeControlPanel(module: LadybugModule[AphidMazeModel]) extends LadybugControlPanel[AphidMazeModel](module) {
  motionControlPanel.peer.setVisible(false)
  val slider = new LinearValueControl(1, 20, module.model.maze.getDim, "maze dim", "0", "cells")
  slider.addChangeListener(new ChangeListener() {
    def stateChanged(e: ChangeEvent) = {
      module.model.setMazeDim(slider.getValue.toInt)
    }
  })
  addControl(slider)
}