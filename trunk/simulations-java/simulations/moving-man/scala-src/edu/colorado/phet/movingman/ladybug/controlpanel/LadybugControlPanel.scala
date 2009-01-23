package edu.colorado.phet.movingman.ladybug.controlpanel

import _root_.edu.colorado.phet.common.phetcommon.model.Resettable
import _root_.edu.colorado.phet.movingman.ladybug.model.LadybugMotionModel._
import edu.colorado.phet.common.phetcommon.view.ControlPanel
import edu.colorado.phet.common.phetcommon.view.ResetAllButton
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import model.{LadybugMotionModel, ObservableS, LadybugModel}
import scala.swing._
import scala.swing.event.ButtonClicked
import java.awt.Dimension
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{Box, JButton, JRadioButton, JLabel}
import LadybugUtil._

class LadybugControlPanel(module: LadybugModule) extends ControlPanel(module) {
  val myModule = module;
  def createBox = Box.createRigidArea(new Dimension(10, 4))

  class VectorControlPanel(m: VectorVisibilityModel) extends BoxPanel(Orientation.Vertical) {
    contents += new MyRadioButton("Show velocity vector", {
      m.velocityVectorVisible = true
      m.accelerationVectorVisible = false
    }
      , m.velocityVectorVisible && !m.accelerationVectorVisible,
      m)

    contents += new MyRadioButton("Show acceleration vector", {
      m.velocityVectorVisible = false
      m.accelerationVectorVisible = true
    }
      , !m.velocityVectorVisible && m.accelerationVectorVisible,
      m)

    contents += new MyRadioButton("Show both", {
      m.velocityVectorVisible = true
      m.accelerationVectorVisible = true
    }
      , m.velocityVectorVisible && m.accelerationVectorVisible,
      m)

    contents += new MyRadioButton("Hide Vectors", {
      m.velocityVectorVisible = false
      m.accelerationVectorVisible = false
    }
      , !m.velocityVectorVisible && !m.accelerationVectorVisible,
      m)
  }
  addControl(new VectorControlPanel(module.getVectorVisibilityModel))

  class MotionControlPanel(m: LadybugMotionModel) extends BoxPanel(Orientation.Vertical) {
    contents += new Label("Choose Motion          ") {font = new PhetFont(14, true)}

    class MyRadioButtonWithEnable(text: String, actionListener: => Unit, getter: => Boolean, observable: ObservableS, shouldBeEnabled: () => Boolean, enableObservable: ObservableS) extends MyRadioButton(text, actionListener, getter, observable) {
      enableObservable.addListener(() => {
        val beEnabled: Boolean = shouldBeEnabled()
        peer.setEnabled(beEnabled)
      })
    }

    contents += new MyRadioButtonWithEnable("Manual", m.motion = MANUAL, m.motion == MANUAL, m, () => module.model.readyForInteraction, module.model)
    contents += new MyRadioButtonWithEnable("Linear", m.motion = LINEAR, m.motion == LINEAR, m, () => module.model.readyForInteraction, module.model)
    contents += new MyRadioButtonWithEnable("Circular", m.motion = CIRCULAR, m.motion == CIRCULAR, m, () => module.model.readyForInteraction, module.model)
    contents += new MyRadioButtonWithEnable("Ellipse", m.motion = ELLIPSE, m.motion == ELLIPSE, m, () => module.model.readyForInteraction, module.model)
  }
  addControl(new MotionControlPanel(module.getLadybugMotionModel))
  addControl(createBox)

  class TraceControlPanel(m: PathVisibilityModel) extends BoxPanel(Orientation.Vertical) {
    contents += new Label("Trace") {font = new PhetFont(14, true)}
    contents += new MyRadioButton("Solid", {
      m.lineVisible = true
      m.dotsVisible = false
    }
      , m.lineVisible && !m.dotsVisible,
      m)

    contents += new MyRadioButton("Dots", {
      m.lineVisible = false
      m.dotsVisible = true
    }
      , !m.lineVisible && m.dotsVisible,
      m)

    contents += new MyRadioButton("Off", {
      m.lineVisible = false
      m.dotsVisible = false
    }
      , !m.lineVisible && !m.dotsVisible,
      m)
  }
  val f = new FlowPanel
  f.contents += new TraceControlPanel(module.getPathVisibilityModel)
  f.contents += new Button("Clear Trace") {reactions += {case ButtonClicked(_) => module.clearTrace}}
  addControl(f)
  addControl(createBox)

  val remoteControl = new RemoteControl(module.model, () => {module.model.startRecording()})
  addControl(remoteControl)
  addControl(createBox)
  val resetAllButton = new ResetAllButton(new Resettable() {
    def reset = {module.resetAll()}
  }, this)
  addControl(resetAllButton)

  def resetAll() = {
    remoteControl.resetAll()
  }
}