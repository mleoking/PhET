package edu.colorado.phet.movingman.ladybug.controlpanel

import _root_.edu.colorado.phet.common.phetcommon.view.util.RectangleUtils
import _root_.edu.colorado.phet.common.piccolophet.event.CursorHandler
import _root_.edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloTimeControlPanel.BackgroundNode
import _root_.edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.event.{ComponentAdapter, ComponentEvent}
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

import java.util.{Hashtable, Dictionary}
import javax.swing._
import model.LadybugModel
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode
import java.awt.Color
import umd.cs.piccolo.util.PBounds
import umd.cs.piccolox.pswing.PSwing
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloTimeControlPanel.BackgroundNode

class MyButtonNode(text: String, icon: Icon, action: () => Unit) extends PText(text) {
  addInputEventListener(new PBasicInputEventHandler() {
    override def mousePressed(event: PInputEvent) = {action()}
  })
}

class LadybugClockControlPanel(module: LadybugModule) extends PhetPCanvas {
  private val nodes = new ArrayBuffer[PNode]
  private val prefSizeM = new Dimension(800, 100)
  setBorder(null)
    setBackground(new JPanel().getBackground)

  def addControl(node: PNode) = {
    addScreenChild(node)
    val offsetX: Double = if (nodes.length == 0) 0 else {nodes(nodes.length - 1).getFullBounds.getMaxX + 5}
    node.setOffset(offsetX, node.getOffset.getY + 10)
    nodes += node
  }

  implicit def stringToIcon(string: String): Icon = new ImageIcon(PhetCommonResources.getImage("clock/" + string))

  implicit def functionToButtonListener(f: () => Unit): DefaultIconButton.Listener = new DefaultIconButton.Listener() {
    def buttonPressed = {f()}
  }

  val backgroundNode = new BackgroundNode
  addScreenChild(backgroundNode)

  val playbackSpeedSlider = new PlaybackSpeedSlider(module.model)
  playbackSpeedSlider.setOffset(0, prefSizeM.getHeight / 2 - playbackSpeedSlider.getFullBounds.getHeight / 2)
  addControl(playbackSpeedSlider)

  val rewind = new RewindButton(50)
  rewind.addListener(() => {
    module.model.setRecord(false)
    module.model.setPlaybackIndexFloat(0.0)
    module.model.setPaused(true)
  })
  module.model.addListener(() => {
    updateRewindEnabled
  })
  updateRewindEnabled
  def updateRewindEnabled={
    val disabled = (module.model.isPlayback && module.model.getPlaybackIndex == 0) || module.model.getHistory.length==0
    rewind.setEnabled(!disabled)
  }
  rewind.addInputEventListener(new ToolTipHandler("Rewind", this))
  rewind.setOffset(0, 12)
  addControl(rewind)

  val playPause = new PlayPauseButton(75)
  playPause.addListener(new PlayPauseButton.Listener() {
    def playbackStateChanged = module.model.setPaused(!playPause.isPlaying)
  })
  val playPauseTooltipHandler = new ToolTipHandler("Pause", this)
  playPause.addInputEventListener(playPauseTooltipHandler)
  module.model.addListener(() => {
    playPause.setPlaying(!module.model.isPaused)
    playPauseTooltipHandler.setText(if (module.model.isPaused) "Play" else "Pause")
  })
  addControl(playPause)

  val stepButton = new StepButton(50)
  stepButton.setEnabled(false)
  stepButton.addInputEventListener(new ToolTipHandler("Step", this))
  module.model.addListener(() => {
    val isLastStep = module.model.getPlaybackIndex == module.model.getHistory.length
    stepButton.setEnabled(module.model.isPlayback && module.model.isPaused && !isLastStep)
  })
  stepButton.addListener(() => {module.model.stepPlayback()})
  stepButton.setOffset(0, 12)
  addControl(stepButton)

  val timeline = new Timeline(module.model, this)
  addScreenChild(timeline)
  module.model.addListener(() => {
    timeline.setVisible(module.model.isPlayback)
  })

  setPreferredSize(prefSizeM)
  def updateSize = {
    if (module.getSimulationPanel.getWidth > 0) {
      val pref = new Dimension(module.getSimulationPanel.getWidth(), prefSizeM.height)
      setPreferredSize(pref)
      updateLayout
    }
  }
  module.getSimulationPanel.addComponentListener(new ComponentAdapter() {
    override def componentResized(e: ComponentEvent) = {
      SwingUtilities.invokeLater(new Runnable() {
        def run = {updateSize}
      })

    }
  })
  updateSize
  addComponentListener(new ComponentAdapter() {
    override def componentResized(e: ComponentEvent) = {myUpdateLayout()}
  })

  myUpdateLayout
  def myUpdateLayout() = {
    val buttonDX = 2
    playPause.setOffset(getPreferredSize.width / 2 - playPause.getFullBounds.getWidth / 2, playPause.getOffset.getY)
    rewind.setOffset(playPause.getFullBounds.getX - rewind.getFullBounds.getWidth - buttonDX, rewind.getOffset.getY)
    stepButton.setOffset(playPause.getFullBounds.getMaxX + buttonDX, stepButton.getOffset.getY)
    playbackSpeedSlider.setOffset(rewind.getFullBounds.getX - playbackSpeedSlider.getFullBounds.getWidth, playbackSpeedSlider.getOffset.getY)

    val halfWidth=playPause.getFullBounds.getCenterX-playbackSpeedSlider.getOffset.getX
    val blist = for (n <- nodes) yield n.getFullBounds
    val b: PBounds = blist.foldLeft(blist(0))((a, b) => new PBounds(a.createUnion(b)))
    val expanded = RectangleUtils.expand(b, 0, 0)
    backgroundNode.setSize((halfWidth*2).toInt, expanded.getHeight.toInt)
    backgroundNode.setOffset(playPause.getFullBounds.getCenterX-halfWidth, expanded.getY)
  }
}

class Timeline(model: LadybugModel, canvas: PhetPCanvas) extends PNode {
  val pathOffsetY = 4
  val pathHeight = 6
  val ellipseWidth = 10
  val ellipseHeight = 8
  val insetX = 10
  val shaded = new PhetPPath(new Color(157, 215, 228))
  val handle = new PhetPPath(Color.blue, new BasicStroke(1), Color.darkGray)
  var scale = 1.0
  addChild(shaded)
  addChild(handle)

  canvas.addComponentListener(new ComponentAdapter() {
    override def componentResized(e: ComponentEvent) = {updateSelf()}
  })

  handle.addInputEventListener(new CursorHandler)
  handle.addInputEventListener(new PBasicInputEventHandler() {
    override def mouseDragged(event: PInputEvent) = {
      model.setPaused(true)
      val dx = event.getCanvasDelta.width
      val t = model.getTime + dx / scale
      model.setPlaybackTime(((model.getFloatTime + dx / scale) max model.getMinRecordedTime) min (model.getMaxRecordedTime))
    }
  })

  model.addListener(() => {
    updateSelf()
  })
  updateSelf
  def updateSelf() = {
    scale = (canvas.getWidth - insetX * 2) / LadybugDefaults.timelineLengthSeconds

    shaded.setPathTo(new Rectangle(insetX, pathOffsetY, (model.getTimeRange * scale).toInt, pathHeight))
    handle.setVisible(model.isPlayback)
    val elapsed = model.getTime - model.getMinRecordedTime
    //    println("t="+model.getTime+", elapsed="+elapsed+", scale="+scale+", e*s="+elapsed*scale)
    handle.setPathTo(new Ellipse2D.Double(elapsed * scale - ellipseWidth / 2 + insetX, pathOffsetY - 1, ellipseWidth, ellipseHeight))
  }
}