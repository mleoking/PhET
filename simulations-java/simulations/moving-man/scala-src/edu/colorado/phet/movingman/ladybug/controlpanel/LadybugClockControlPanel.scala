package edu.colorado.phet.movingman.ladybug.controlpanel

import _root_.edu.colorado.phet.common.piccolophet.event.CursorHandler
import _root_.edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.{Rectangle, Dimension, BasicStroke}
import _root_.edu.colorado.phet.common.piccolophet.event.ToolTipHandler
import _root_.edu.colorado.phet.common.piccolophet.nodes.mediabuttons.DefaultIconButton
import _root_.edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PlayPauseButton
import _root_.edu.colorado.phet.common.piccolophet.nodes.mediabuttons.RewindButton
import _root_.edu.colorado.phet.common.piccolophet.nodes.mediabuttons.StepButton
import _root_.edu.colorado.phet.common.piccolophet.PhetPCanvas
import _root_.scala.collection.mutable.ArrayBuffer
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources
import edu.colorado.phet.common.phetcommon.resources.PhetResources
import java.awt.geom.Ellipse2D

import javax.swing.{Icon, JButton, ImageIcon, JPanel}
import model.LadybugModel
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode
import java.awt.Color

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
    node.setOffset(offsetX, node.getOffset.getY+10)
    nodes += node
  }

  implicit def stringToIcon(string: String): Icon = new ImageIcon(PhetCommonResources.getImage("clock/" + string))

  implicit def functionToButtonListener(f: () => Unit): DefaultIconButton.Listener = new DefaultIconButton.Listener() {
    def buttonPressed = {f()}
  }

  val rewind = new RewindButton(50)
  rewind.addListener(() => {
    module.model.setPlayback(1)
    module.model.setPlaybackIndexFloat(0.0)
    module.model.setPaused(true)
  })
  module.model.addListener((m: LadybugModel) => {
    val disabled = module.model.isPlayback && module.model.getPlaybackIndex == 0
    rewind.setEnabled(!disabled)
  })
  rewind.addInputEventListener(new ToolTipHandler("Rewind", this))
  rewind.setOffset(0, 12)
  add(rewind)

  val playPause = new PlayPauseButton(75)
  playPause.addListener(new PlayPauseButton.Listener() {
    def playbackStateChanged = module.model.setPaused(!playPause.isPlaying)
  })
  val playPauseTooltipHandler = new ToolTipHandler("Pause", this)
  playPause.addInputEventListener(playPauseTooltipHandler)
  module.model.addListener((m: LadybugModel) => {
    playPause.setPlaying(!module.model.isPaused)
    playPauseTooltipHandler.setText(if (module.model.isPaused) "Play" else "Pause")
  })
  add(playPause)

  val stepButton = new StepButton(50)
  stepButton.setEnabled(false)
  stepButton.addInputEventListener(new ToolTipHandler("Step", this))
  module.model.addListener((m: LadybugModel) => {
    val isLastStep=module.model.getPlaybackIndex==module.model.getHistory.length
    stepButton.setEnabled(module.model.isPlayback && module.model.isPaused && !isLastStep)
  })
  stepButton.addListener(() => {module.model.stepPlayback()})
  stepButton.setOffset(0, 12)
  add(stepButton)

  val timeline = new Timeline(module.model)
  addScreenChild(timeline)
  module.model.addListener((m: LadybugModel) => {
    timeline.setVisible(m.isPlayback)
  })

  setPreferredSize(new Dimension(500, 100))
}

class Timeline(model: LadybugModel) extends PNode {
  val pathOffsetY = 4
  val pathHeight = 10
  val ellipseWidth = 8
  val ellipseHeight = 12
  val scale = 1.0
  val background = new PhetPPath(new Rectangle(0, pathOffsetY, 500, pathHeight), Color.lightGray)
  val shaded = new PhetPPath(Color.orange)
  val handle = new PhetPPath(Color.blue, new BasicStroke(1), Color.black)
  addChild(background)
  addChild(shaded)
  addChild(handle)

  handle.addInputEventListener(new CursorHandler)
  handle.addInputEventListener(new PBasicInputEventHandler() {
    override def mouseDragged(event: PInputEvent) = {
      model.setPaused(true)
      val dx = event.getCanvasDelta.width
      model.setPlaybackIndexFloat(((model.getPlaybackIndexFloat + dx * scale) max 0) min (model.getHistory.length - 1))
    }
  })

  model.addListener((model: LadybugModel) => {
    updateSelf()
  })
  updateSelf
  def updateSelf() = {
    shaded.setPathTo(new Rectangle(0, pathOffsetY, (model.getHistory.length / scale).toInt, pathHeight))
    handle.setVisible(model.isPlayback)
    handle.setPathTo(new Ellipse2D.Double(model.getPlaybackIndexFloat / scale - ellipseWidth / 2, pathOffsetY, ellipseWidth, ellipseHeight))
  }
}