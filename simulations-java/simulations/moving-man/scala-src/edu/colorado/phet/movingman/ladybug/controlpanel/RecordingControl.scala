package edu.colorado.phet.movingman.ladybug.controlpanel

import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.geom.Ellipse2D
import java.awt.{Rectangle, Dimension, BasicStroke, Color}
import java.text.DecimalFormat
import javax.swing.JPanel
import LadybugUtil._
import model.LadybugModel
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.{PPath, PText}
import umd.cs.piccolo.PNode

class RecordingControl(model: LadybugModel) extends PNode {
  val text = new PText("0.00")
//  text.setFont(new PhetFont(30))
//  addChild(text)

  val _width = 100
  val _height = 60
//  setPreferredSize(new Dimension(_width, _height))
  implicit def timeToString(time: Double) = new DecimalFormat("0.00").format(time) + " sec"
  model.addListener((model: LadybugModel) => {
    updateReadouts
  })

  val playbackIndicator = new PText("* Playback")
  playbackIndicator.setFont(new PhetFont(16, true))
  addChild(playbackIndicator)

  val recordIndicator = new PText("* Record")
  recordIndicator.setFont(new PhetFont(16, true))
  addChild(recordIndicator)

  playbackIndicator.setOffset(_width - playbackIndicator.getFullBounds.getWidth - 2, _height - playbackIndicator.getFullBounds.getHeight)
  recordIndicator.setOffset(playbackIndicator.getFullBounds.getX, playbackIndicator.getFullBounds.getY - recordIndicator.getFullBounds.getHeight)

  val timeline = new Timeline()
  addChild(timeline)

  updateReadouts
  def updateReadouts = {
    text.setText(model.getTime())
    text.setOffset(2, _height - text.getFullBounds.getHeight)

    playbackIndicator.setTextPaint(if (model.isPlayback) Color.red else Color.lightGray)
    recordIndicator.setTextPaint(if (model.isRecord) Color.red else Color.lightGray)

    timeline.setVisible(model.isPlayback)
  }

  class Timeline extends PNode {
    val pathOffsetY = 4
    val pathHeight = 10
    val ellipseWidth = 8
    val ellipseHeight = 12
    val scale = 2.0
    val background = new PhetPPath(new Rectangle(0, pathOffsetY, _width, pathHeight), Color.lightGray)
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
}