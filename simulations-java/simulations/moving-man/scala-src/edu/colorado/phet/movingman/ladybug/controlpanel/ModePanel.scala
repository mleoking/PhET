package edu.colorado.phet.movingman.ladybug.controlpanel

import _root_.scala.swing.Panel
import javax.swing.{BoxLayout, JPanel}
import javax.swing.BoxLayout._
import model.LadybugModel
import edu.colorado.phet.movingman.ladybug.swing.MyRadioButton
import java.awt.Color._

class ModePanel(model: LadybugModel) extends JPanel {
    setLayout(new BoxLayout(this, Y_AXIS))
    def color(b: Boolean) = if (b) red else black

    val recordingButton = new MyRadioButton("Recording", model.setRecord(true), {model.isRecord}, model.addListener)
    add(recordingButton.peer)

    val playbackButton = new MyRadioButton("Playback", model.setRecord(false), {model.isPlayback}, model.addListener)
    add(playbackButton.peer)

    model.addListener(() => updateColors)
    def updateColors = {
        recordingButton.peer.setForeground(color(recordingButton.peer.isSelected && !model.isPaused))
        playbackButton.peer.setForeground(color(playbackButton.peer.isSelected && !model.isPaused))
    }
    updateColors
}