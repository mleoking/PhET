package edu.colorado.phet.movingman.ladybug.controlpanel

import _root_.edu.colorado.phet.common.piccolophet.event.CursorHandler
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
        node.setOffset(offsetX, node.getOffset.getY + 10)
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
    module.model.addListener(() => {
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
    module.model.addListener(() => {
        playPause.setPlaying(!module.model.isPaused)
        playPauseTooltipHandler.setText(if (module.model.isPaused) "Play" else "Pause")
    })
    add(playPause)

    val stepButton = new StepButton(50)
    stepButton.setEnabled(false)
    stepButton.addInputEventListener(new ToolTipHandler("Step", this))
    module.model.addListener(() => {
        val isLastStep = module.model.getPlaybackIndex == module.model.getHistory.length
        stepButton.setEnabled(module.model.isPlayback && module.model.isPaused && !isLastStep)
    })
    stepButton.addListener(() => {module.model.stepPlayback()})
    stepButton.setOffset(0, 12)
    add(stepButton)

    val timeline = new Timeline(module.model, this)
    addScreenChild(timeline)
    module.model.addListener(() => {
        timeline.setVisible(module.model.isPlayback)
    })

    setPreferredSize(new Dimension(600, 100))
    addComponentListener(new ComponentAdapter() {
        override def componentResized(e: ComponentEvent) = {myUpdateLayout()}
    })

    myUpdateLayout
    def myUpdateLayout() = {
        val buttonDX = 2
        playPause.setOffset(getWidth / 2 - playPause.getFullBounds.getWidth / 2, playPause.getOffset.getY)
        rewind.setOffset(playPause.getFullBounds.getX - rewind.getFullBounds.getWidth - buttonDX, rewind.getOffset.getY)
        stepButton.setOffset(playPause.getFullBounds.getMaxX + buttonDX, stepButton.getOffset.getY)
    }
}

class Timeline(model: LadybugModel, canvas: PhetPCanvas) extends PNode {
    val pathOffsetY = 4
    val pathHeight = 10
    val ellipseWidth = 8
    val ellipseHeight = 12
    val insetX = 10
    val shaded = new PhetPPath(Color.orange)
    val handle = new PhetPPath(Color.blue, new BasicStroke(1), Color.black)
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