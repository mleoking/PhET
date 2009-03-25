package edu.colorado.phet.movingman.ladybug.controlpanel

import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import model.{RecordModel, LadybugState, LadybugModel}
import scala.swing.{Component, Panel}
import java.awt.Color
import javax.swing.BoxLayout._
import javax.swing.{BoxLayout, JPanel, JComponent}
import java.awt.Color._
import scalacommon.swing.MyRadioButton

class ModePanel(model: RecordModel[LadybugState]) extends JPanel {
  setLayout(new BoxLayout(this, Y_AXIS))
  setBackground(new Color(0, 0, 0, 0))

  val recordingButton = addComponent{
    new MyRadioButton("Record", model.setRecord(true), model.isRecord, model.addListener) {
      font = new PhetFont(15, true)
    }
  }
  recordingButton.peer.setBackground(new Color(0, 0, 0, 0))
  val playbackButton = addComponent{
    new MyRadioButton("Playback", {model.setRecord(false); model.setPlaybackIndexFloat(0.0); model.setPaused(true)}, model.isPlayback, model.addListener) {
      font = new PhetFont(15, true)
    }
  }
  playbackButton.peer.setBackground(new Color(0, 0, 0, 0))

  addListener(model.addListenerByName){
    def color(b: Boolean) = if (b) red else black
    recordingButton.peer.setForeground(color(recordingButton.peer.isSelected && !model.isPaused && !model.isRecordingFull))
    playbackButton.peer.setForeground(color(playbackButton.peer.isSelected && !model.isPaused))
  }

  //a control structure that (1) creates a swing component and (2) automatically adds it
  //a suitable replacement for something like
  //val button=createButton
  //add(button)
  def addComponent[T <: Component](m: => T): T = {
    val component = m
    add(component.peer)
    component
  }

  //adds a listener to some model, and also invokes the update implementation
  //a suitable replacement for something like:
  //model.addListener(update)
  //update
  def addListener(addListener: (=> Unit) => Unit)(updateFunction: => Unit) = {
    addListener(updateFunction)
    updateFunction
  }
}