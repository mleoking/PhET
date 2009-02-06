package edu.colorado.phet.movingman.ladybug.controlpanel

import _root_.scala.swing.Panel
import javax.swing.{BoxLayout, JPanel}
import javax.swing.BoxLayout._
import model.LadybugModel
import edu.colorado.phet.movingman.ladybug.swing.MyRadioButton

class ModePanel(model: LadybugModel) extends JPanel {
    setLayout(new BoxLayout(this, Y_AXIS))
    val recordingButton = new MyRadioButton("Recording", model.setRecord(true), model.isRecord, model.addListener)
    add(recordingButton.peer)

    val playbackButton = new MyRadioButton("Playback", model.setRecord(false), model.isPlayback, model.addListener)
    add(playbackButton.peer)
}