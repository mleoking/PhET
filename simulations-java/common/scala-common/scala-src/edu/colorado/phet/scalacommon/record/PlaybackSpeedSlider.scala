package edu.colorado.phet.scalacommon.record

import _root_.edu.colorado.phet.common.piccolophet.event.CursorHandler
import common.phetcommon.resources.PhetCommonResources
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl
import java.awt.Color
import java.awt.Cursor
import java.util.Hashtable
import javax.swing.event.{ChangeListener, ChangeEvent}
import javax.swing.{JSlider, JLabel}

import umd.cs.piccolo.PNode
import umd.cs.piccolox.pswing.PSwing
import common.phetcommon.resources.PhetCommonResources._

class PlaybackSpeedSlider[T](model: RecordModel[T]) extends PNode {
  addInputEventListener(new CursorHandler)
  val slider = new JSlider
  slider.setBackground(new Color(0, 0, 0, 0))
  val transform = new LinearFunction(slider.getMinimum, slider.getMaximum, 0.25, 2.0)

  val dict = new Hashtable[Integer, JLabel]

  dict.put(slider.getMinimum, new JLabel(getString("Common.time.slow")))
  dict.put(slider.getMaximum, new JLabel(getString("Common.time.fast")))

  slider.setLabelTable(dict)
  slider.setPaintLabels(true)
  val playbackSpeedSlider = new PSwing(slider)
  addChild(playbackSpeedSlider)
  slider.addChangeListener(new ChangeListener() {
    def stateChanged(e: ChangeEvent) = {
      model.setPlayback(transform.evaluate(slider.getValue))
    }
  })

  def updatePlaybackSliderVisible = playbackSpeedSlider.setVisible(model.isPlayback)
  model.addListener(() => {updatePlaybackSliderVisible})
  updatePlaybackSliderVisible
}