package edu.colorado.phet.motionseries.graphics

import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.model.ForceBead
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.motion.charts.{PlayAreaSliderControl, TextBox, MotionSliderNode}

class AppliedForceSliderNode(bead: ForceBead, mousePressHandler: () => Unit) extends PNode {
  val positionSlider = new PlayAreaSliderControl(-MotionSeriesDefaults.MAX_APPLIED_FORCE, MotionSeriesDefaults.MAX_APPLIED_FORCE,
    bead.parallelAppliedForce, "controls.applied-force-x".translate, "units.abbr.newtons".translate, MotionSeriesDefaults.appliedForceColor, new TextBox(new PhetFont(18, true), 6) {
      addListener(new TextBox.Listener() {
        def changed = {
          bead.parallelAppliedForce = java.lang.Double.parseDouble(getText)
        }
      })
    })
  addChild(positionSlider)

  bead.parallelAppliedForceListeners += (() => {
    positionSlider.setValue(bead.parallelAppliedForce)
  })

  positionSlider.addListener(new MotionSliderNode.Adapter {
    override def sliderDragged(value: java.lang.Double) = {
      bead.parallelAppliedForce = value.doubleValue
    }
  })
}