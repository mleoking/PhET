package edu.colorado.phet.movingman.ladybug.controlpanel

import _root_.scala.swing.Panel
import javax.swing.BoxLayout._
import javax.swing.{BoxLayout, JPanel, JComponent}
import model.LadybugModel
import edu.colorado.phet.movingman.ladybug.swing.MyRadioButton
import java.awt.Color._

class ModePanel(model: LadybugModel) extends JPanel {
    setLayout(new BoxLayout(this, Y_AXIS))
    def color(b: Boolean) = if (b) red else black

    val recordingButton = addNewControl{new MyRadioButton("Recording", model.setRecord(true), {model.isRecord}, model.addListener)}
    val playbackButton = addNewControl{new MyRadioButton("Playback", model.setRecord(false), {model.isPlayback}, model.addListener)}

    def addAndInvoke(addListener: (() => Unit) => Unit)(updateFunction: () => Unit) = {
        addListener(updateFunction)
        updateFunction
    }

    addAndInvoke(model.addListener){
        () => {
            recordingButton.peer.setForeground(color(recordingButton.peer.isSelected && !model.isPaused))
            playbackButton.peer.setForeground(color(playbackButton.peer.isSelected && !model.isPaused))
        }
    }

    //a control structure that (1) creates a swing component and (2) automatically adds it
    def addNewControl(m: => MyRadioButton): MyRadioButton = {
        val component = m
        add(component.peer)
        component
    }
}