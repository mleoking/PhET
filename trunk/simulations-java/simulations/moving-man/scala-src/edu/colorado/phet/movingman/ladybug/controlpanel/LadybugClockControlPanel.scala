package edu.colorado.phet.movingman.ladybug.controlpanel

import _root_.edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PlayPauseButton
import _root_.edu.colorado.phet.common.piccolophet.nodes.mediabuttons.StepButton
import _root_.edu.colorado.phet.common.piccolophet.PhetPCanvas
import _root_.scala.collection.mutable.ArrayBuffer
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources
import edu.colorado.phet.common.phetcommon.resources.PhetResources
import java.awt.Dimension
import javax.swing.{Icon, JButton, ImageIcon, JPanel}
import model.LadybugModel
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode

class MyButtonNode(text: String, icon: Icon, action: () => Unit) extends PText(text) {
  addInputEventListener(new PBasicInputEventHandler() {
    override def mousePressed(event: PInputEvent) = {action()}
  })
}

class LadybugClockControlPanel(module: LadybugModule) extends PhetPCanvas {
  val nodes = new ArrayBuffer[PNode]

  def add(node: PNode) = {
    addScreenChild(node)
    val offsetX: Double = if (nodes.length == 0) 0 else {nodes(nodes.length - 1).getFullBounds.getMaxX + 5}
    node.setOffset(offsetX, node.getOffset.getY)
    nodes += node
  }

  implicit def stringToIcon(string: String): Icon = new ImageIcon(PhetCommonResources.getImage("clock/" + string))
  add(new RecordingControl(module.model))
  add(new MyButtonNode("Playback", "Play24.gif", () => {
    module.model.setPlayback(1.0)
    module.model.setPaused(false)
  }))
  add(new MyButtonNode("Slow Playback", "StepForward24.gif", () => {
    module.model.setPlayback(0.5)
    module.model.setPaused(false)
  }))

  val playPause = new PlayPauseButton(75)
  playPause.setOffset(200, 0)
  add(playPause)

  val stepButton = new StepButton(50)
  stepButton.setOffset(280, 0)
  add(stepButton)

  setPreferredSize(new Dimension(500, 100))

  val pauseButton = new MyButtonNode("Pause", "Pause24.gif", () => module.model.setPaused(!module.model.isPaused()))

  //  def updatePauseEnabled(a: LadybugModel) = pauseButton.setEnabled(true)
  //  updatePauseEnabled(module.model)
  //  module.model.addListener(updatePauseEnabled)
  add(pauseButton)
  add(new MyButtonNode("Rewind", "Rewind24.gif", () => module.model.rewind))
}