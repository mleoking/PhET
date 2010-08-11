package edu.colorado.phet.motionseries.graphics

import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.model.ForceMotionSeriesObject
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.motion.charts.{PlayAreaSliderControl, TextBox, MotionSliderNode}

class AppliedForceSliderNode(motionSeriesObject: ForceMotionSeriesObject, dragHandler: () => Unit) extends PNode {
  val positionSlider = new PlayAreaSliderControl(-MotionSeriesDefaults.MAX_APPLIED_FORCE, MotionSeriesDefaults.MAX_APPLIED_FORCE,
    motionSeriesObject.parallelAppliedForce, "controls.applied-force-x".translate, "units.abbr.newtons".translate, MotionSeriesDefaults.appliedForceColor, new TextBox(new PhetFont(18, true), 6) {
      addListener(new TextBox.Listener() {
        def changed = {
          //have to replace comma with decimal otherwise we obtain problems like:
          //Exception in thread "AWT-EventQueue-0" java.lang.NumberFormatException: For input string: "0,00"
          motionSeriesObject.parallelAppliedForce = java.lang.Double.parseDouble(getText.replace(',','.'))
        }
      })
    })
  addChild(positionSlider)

  motionSeriesObject.parallelAppliedForceListeners += (() => {
    positionSlider.setValue(motionSeriesObject.parallelAppliedForce)
  })

  positionSlider.addListener(new MotionSliderNode.Adapter {
    override def sliderDragged(value: java.lang.Double) = {
      motionSeriesObject.parallelAppliedForce = value.doubleValue
      dragHandler()
    }
  })
}