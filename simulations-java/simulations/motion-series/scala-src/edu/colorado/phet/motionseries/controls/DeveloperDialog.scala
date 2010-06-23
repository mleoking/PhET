package edu.colorado.phet.motionseries.controls

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils
import edu.colorado.phet.common.phetcommon.view.{VerticalLayoutPanel, PhetFrame}
import javax.swing.{JDialog}
import edu.colorado.phet.motionseries.Predef._
import edu.colorado.phet.motionseries.MotionSeriesConfig
import edu.colorado.phet.motionseries.swing.ScalaValueControl

class DeveloperDialog(phetFrame: PhetFrame) extends JDialog(phetFrame, false) {
  val layoutPanel = new VerticalLayoutPanel
  layoutPanel.add(new ScalaValueControl(1, 100, "tail width".literal, "0.0".literal, "px".literal,
    () => MotionSeriesConfig.VectorTailWidth.value, MotionSeriesConfig.VectorTailWidth.value_=, MotionSeriesConfig.VectorTailWidth.addListener))
  layoutPanel.add(new ScalaValueControl(1, 100, "head width".literal, "0.0".literal, "px".literal,
    () => MotionSeriesConfig.VectorHeadWidth.value, MotionSeriesConfig.VectorHeadWidth.value_=, MotionSeriesConfig.VectorHeadWidth.addListener))
  setContentPane(layoutPanel)
  pack()
  SwingUtils.centerWindowOnScreen(this)
}