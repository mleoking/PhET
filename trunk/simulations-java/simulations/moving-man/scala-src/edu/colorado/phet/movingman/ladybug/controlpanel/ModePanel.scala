package edu.colorado.phet.movingman.ladybug.controlpanel

import _root_.scala.swing.{Component, Panel}
import javax.swing.BoxLayout._
import javax.swing.{BoxLayout, JPanel, JComponent}
import model.LadybugModel
import edu.colorado.phet.movingman.ladybug.swing.MyRadioButton
import java.awt.Color._

class ModePanel(model: LadybugModel) extends JPanel {
    setLayout(new BoxLayout(this, Y_AXIS))

    val recordingButton = addComponent{new MyRadioButton("Record", model.setRecord(true), model.isRecord, model.addListener)}
    val playbackButton = addComponent{new MyRadioButton("Playback", {model.setRecord(false); model.setPlaybackIndexFloat(0.0)}, model.isPlayback, model.addListener)}

    addListener(model.addListenerByName){
        def color(b: Boolean) = if (b) red else black
        recordingButton.peer.setForeground(color(recordingButton.peer.isSelected && !model.isPaused))
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