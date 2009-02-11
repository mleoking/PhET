package edu.colorado.phet.movingman.ladybug.controlpanel

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader._
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloTimeControlPanel.BackgroundNode
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt._
import edu.colorado.phet.common.piccolophet.event.ToolTipHandler
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.DefaultIconButton
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PlayPauseButton
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.RewindButton
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.StepButton
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import scala.collection.mutable.ArrayBuffer
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources
import edu.colorado.phet.common.phetcommon.resources.PhetResources
import java.awt.event.{ActionEvent, ComponentAdapter, ComponentEvent, ActionListener}

import java.awt.geom.{Line2D, Ellipse2D}
import java.util.{Hashtable, Dictionary}
import javax.swing._
import model.LadybugModel
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.{PImage, PText}
import umd.cs.piccolo.PNode
import umd.cs.piccolo.util.PBounds
import umd.cs.piccolox.pswing.PSwing
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloTimeControlPanel.BackgroundNode
import edu.colorado.phet.movingman.ladybug.LadybugUtil._

class LadybugClockControlPanel(module: LadybugModule) extends PhetPCanvas {
  private class MyButtonNode(text: String, icon: Icon, action: () => Unit) extends PText(text) {
    addInputEventListener(new PBasicInputEventHandler() {
      override def mousePressed(event: PInputEvent) = {action()}
    })
  }

  private val nodes = new ArrayBuffer[PNode]
  private val prefSizeM = new Dimension(800, 100)
  setBorder(null)
  setBackground(new JPanel().getBackground)

  protected def addControl(node: PNode) = {
    addScreenChild(node)
    val offsetX: Double = if (nodes.length == 0) 0 else {nodes(nodes.length - 1).getFullBounds.getMaxX + 5}
    node.setOffset(offsetX, node.getOffset.getY + 10)
    nodes += node
  }

  implicit def stringToIcon(string: String): Icon = new ImageIcon(PhetCommonResources.getImage("clock/" + string))

  implicit def functionToButtonListener(f: () => Unit): DefaultIconButton.Listener = new DefaultIconButton.Listener() {
    def buttonPressed = {f()}
  }

  val backgroundNode = new BackgroundNode;
  addScreenChild(backgroundNode)

  val modePanel = new ModePanel(module.model)


  val clearButton = new JButton("Clear")

  clearButton.addActionListener(() => { //todo : couldn't figure out how to remove ()=> with by name using implicits
    module.model.clearHistory
    module.model.setPaused(true)
  })


  val playbackSpeedSlider = new PlaybackSpeedSlider(module.model)
  playbackSpeedSlider.setOffset(0, prefSizeM.getHeight / 2 - playbackSpeedSlider.getFullBounds.getHeight / 2)
  playbackSpeedSlider.addInputEventListener(new CursorHandler)


  val rewind = new RewindButton(50)
  rewind.addListener(() => {
    module.model.setRecord(false)
    module.model.setPlaybackIndexFloat(0.0)
    module.model.setPaused(true)
  })
  module.model.addListenerByName(updateRewindEnabled)
  updateRewindEnabled
  def updateRewindEnabled = {
    val enabled = module.model.isPlayback && module.model.getHistory.length > 0
    rewind.setEnabled(enabled)
  }
  rewind.addInputEventListener(new ToolTipHandler("Rewind", this))
  rewind.setOffset(0, 12)


  val clearButtonNode = new PSwing(clearButton)
  val modePanelNode = new PSwing(modePanel)


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



  val stepButton = new StepButton(50)
  stepButton.setEnabled(false)
  stepButton.addInputEventListener(new ToolTipHandler("Step", this))
  module.model.addListener(() => {
    val isLastStep = module.model.getPlaybackIndex == module.model.getHistory.length
    stepButton.setEnabled(module.model.isPlayback && module.model.isPaused && !isLastStep)
  })
  stepButton.addListener(() => {module.model.stepPlayback()})
  stepButton.setOffset(0, 12)


  addControl(clearButtonNode)
  addControl(modePanelNode)
  addControl(rewind)
  addControl(playPause)
  addControl(stepButton)
  addControl(playbackSpeedSlider)


  val timeline = new Timeline(module.model, this)
  addScreenChild(timeline)
  //  module.model.addListener(() => timeline.setVisible(module.model.isPlayback))

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
    playbackSpeedSlider.setOffset(stepButton.getFullBounds.getMaxX, playbackSpeedSlider.getOffset.getY)

    modePanelNode.setOffset(rewind.getFullBounds.getX - modePanelNode.getFullBounds.width, playPause.getFullBounds.getCenterY - modePanelNode.getFullBounds.getHeight / 2)
    clearButtonNode.setOffset(modePanelNode.getFullBounds.getX - clearButtonNode.getFullBounds.width, playPause.getFullBounds.getCenterY - clearButtonNode.getFullBounds.getHeight / 2)

    val halfWidth = playPause.getFullBounds.getCenterX - playbackSpeedSlider.getFullBounds.getMaxX
    val blist = for (n <- nodes) yield n.getFullBounds
    val b: PBounds = blist.foldLeft(blist(0))((a, b) => new PBounds(a.createUnion(b)))
    val expanded = RectangleUtils.expand(b, 0, 0)
    backgroundNode.setSize((halfWidth * 2).toInt, expanded.getHeight.toInt)
    backgroundNode.setOffset(playPause.getFullBounds.getCenterX - halfWidth, expanded.getY)
  }
}

class Timeline(model: LadybugModel, canvas: PhetPCanvas) extends PNode {
  val pathOffsetY = 4
  val pathHeight = 6
  val ellipseWidth = 10
  val ellipseHeight = 8
  val insetX = 10
  val shaded = new PhetPPath(LadybugColorSet.position)
  val backgroundColor = new Color(190, 195, 195)

  def darker(c: Color, del: Int) = {
    new Color(c.getRed - del, c.getGreen - del, c.getBlue - del)
  }

  val background = new PhetPPath(backgroundColor) {
    val topShade = new PhetPPath(new BasicStroke(2), darker(backgroundColor, 55))
    addChild(topShade)
    val bottomShade = new PhetPPath(new BasicStroke(1), darker(backgroundColor, 20))
    addChild(bottomShade)
    val leftShade = new PhetPPath(new BasicStroke(2), darker(backgroundColor, 50))
    addChild(leftShade)
    val rightShade = new PhetPPath(new BasicStroke(1), darker(backgroundColor, 20))
    addChild(rightShade)
    override def setPathTo(aShape: Shape) = {
      super.setPathTo(aShape)
      val b = aShape.getBounds2D
      topShade.setPathTo(new Line2D.Double(b.getX, b.getY, b.getMaxX, b.getY))
      bottomShade.setPathTo(new Line2D.Double(b.getX, b.getMaxY, b.getMaxX, b.getMaxY))
      leftShade.setPathTo(new Line2D.Double(b.getX, b.getY, b.getX, b.getMaxY))
      rightShade.setPathTo(new Line2D.Double(b.getMaxX, b.getY, b.getMaxX, b.getMaxY))
    }
  }
  //  val handle = new PhetPPath(Color.blue, new BasicStroke(1), Color.darkGray)
  val img = loadBufferedImage("piccolo-phet/images/button-template.png")
  val scaledImage = BufferedImageUtils.getScaledInstance(img, 20, 10, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true)
  val handle = new PImage(scaledImage)
  var scale = 1.0
  addChild(background)
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

  model.addListenerByName(updateSelf())
  updateSelf
  def updateSelf() = {
    scale = (canvas.getWidth - insetX * 2) / LadybugDefaults.timelineLengthSeconds

    shaded.setPathTo(new Rectangle(insetX, pathOffsetY + 1, (model.getTimeRange * scale).toInt, pathHeight - 1))
    background.setPathTo(new Rectangle(insetX, pathOffsetY, (LadybugDefaults.timelineLengthSeconds * scale).toInt, pathHeight))
    handle.setVisible(model.isPlayback)
    val elapsed = model.getTime - model.getMinRecordedTime
    //    handle.setPathTo(new Ellipse2D.Double(elapsed * scale - ellipseWidth / 2 + insetX, pathOffsetY - 1, ellipseWidth, ellipseHeight))
    handle.setOffset(elapsed * scale - handle.getFullBounds.getWidth / 2 + insetX, pathOffsetY - 2)
  }
}