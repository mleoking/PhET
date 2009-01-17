package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.Dimension
import java.text.DecimalFormat
import javax.swing.JPanel
import umd.cs.piccolo.nodes.PText
import LadybugUtil._
import java.awt.Color

class RecordingControl(model: LadybugModel) extends PhetPCanvas {
  val text = new PText("0.00")
  text.setFont(new PhetFont(30))
  addScreenChild(text)
  val _height = 100
  val _width = 250
  setPreferredSize(new Dimension(_width, _height))
  implicit def timeToString(time: Double) = new DecimalFormat("0.00").format(time) + " sec"
  model.addListener((model: LadybugModel) => {
    updateReadouts
  })

  val playbackIndicator = new PText("* Playback")
  playbackIndicator.setFont(new PhetFont(16, true))
  addScreenChild(playbackIndicator)

  val recordIndicator = new PText("* Record")
  recordIndicator.setFont(new PhetFont(16, true))
  addScreenChild(recordIndicator)

  playbackIndicator.setOffset(_width - playbackIndicator.getFullBounds.getWidth - 2, _height - playbackIndicator.getFullBounds.getHeight)
  recordIndicator.setOffset(playbackIndicator.getFullBounds.getX, playbackIndicator.getFullBounds.getY - recordIndicator.getFullBounds.getHeight)

  updateReadouts
  def updateReadouts = {
    text.setText(model.getTime())
    text.setOffset(2, _height - text.getFullBounds.getHeight)

    playbackIndicator.setTextPaint(if (model.isPlayback) Color.red else Color.lightGray)
    recordIndicator.setTextPaint(if (model.isRecord) Color.red else Color.lightGray)
  }
}