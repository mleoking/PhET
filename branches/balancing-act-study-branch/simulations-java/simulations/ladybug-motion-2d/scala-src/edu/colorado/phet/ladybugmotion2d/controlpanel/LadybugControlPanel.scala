package edu.colorado.phet.ladybugmotion2d.controlpanel

import _root_.edu.colorado.phet.ladybugmotion2d.model.{LadybugMotionModel, LadybugModel}
import edu.colorado.phet.common.phetcommon.model.Resettable
import edu.colorado.phet.ladybugmotion2d.model.LadybugMotionModel._
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import scala.swing._
import java.awt.Dimension
import javax.swing.Box
import edu.colorado.phet.scalacommon.Predef._
import java.awt.GridBagConstraints._
import edu.colorado.phet.ladybugmotion2d.LadybugModule
import edu.colorado.phet.ladybugmotion2d.LadybugMotion2DResources._
import edu.colorado.phet.scalacommon.swing.MyRadioButton
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton
import edu.colorado.phet.common.phetcommon.view.{VerticalLayoutPanel, ControlPanel, ResetAllButton}

class LadybugControlPanel[M <: LadybugModel](module: LadybugModule[M]) extends ControlPanel(module) {
  val myModule = module;

  def createBox = Box.createRigidArea(new Dimension(10, 4))

  class VectorControlPanel(m: VectorVisibilityModel) extends BoxPanel(Orientation.Vertical) {
    contents += new Label(getLocalizedString("model.vectors")) {font = new PhetFont(14, true)}
    contents += new MyRadioButton(getLocalizedString("show.velocity.vector"), {
      m.velocityVectorVisible = true
      m.accelerationVectorVisible = false
    }, m.velocityVectorVisible && !m.accelerationVectorVisible, m.addListener)

    contents += new MyRadioButton(getLocalizedString("show.acceleration.vector"), {
      m.velocityVectorVisible = false
      m.accelerationVectorVisible = true
    }, !m.velocityVectorVisible && m.accelerationVectorVisible, m.addListener)

    contents += new MyRadioButton(getLocalizedString("show.both"), {
      m.velocityVectorVisible = true
      m.accelerationVectorVisible = true
    }, m.velocityVectorVisible && m.accelerationVectorVisible, m.addListener)

    contents += new MyRadioButton(getLocalizedString("hide.vectors"), {
      m.velocityVectorVisible = false
      m.accelerationVectorVisible = false
    }, !m.velocityVectorVisible && !m.accelerationVectorVisible, m.addListener)
  }

  getContentPanel.setAnchor(WEST)
  getContentPanel.setFillNone()

  addControl(new VectorControlPanel(module.vectorVisibilityModel))

  class MotionControlPanel(m: LadybugMotionModel) extends BoxPanel(Orientation.Vertical) {
    contents += new Label(getLocalizedString("choose.motion")) {font = new PhetFont(14, true)}

    class MyRadioButtonWithEnable(text: String, actionListener: => Unit, getter: => Boolean, addListener: ( () => Unit ) => Unit, shouldBeEnabled: () => Boolean, enableObservable: Observable) extends MyRadioButton(text, actionListener, getter, addListener) {
      enableObservable.addListener(() => peer.setEnabled(shouldBeEnabled()))
    }

    def rec() {
      module.model.setPaused(false)
      module.model.setRecord(true)
    }

    contents += new MyRadioButtonWithEnable(getLocalizedString("motion.manual"), {m.motion = MANUAL; rec()}, m.motion == MANUAL, m.addListener, () => module.model.readyForInteraction, module.model)
    contents += new MyRadioButtonWithEnable(getLocalizedString("motion.linear"), {m.motion = LINEAR; rec()}, m.motion == LINEAR, m.addListener, () => module.model.readyForInteraction, module.model)
    contents += new MyRadioButtonWithEnable(getLocalizedString("motion.circular"), {m.motion = CIRCULAR; rec()}, m.motion == CIRCULAR, m.addListener, () => module.model.readyForInteraction, module.model)
    contents += new MyRadioButtonWithEnable(getLocalizedString("motion.ellipse"), {m.motion = ELLIPSE; rec()}, m.motion == ELLIPSE, m.addListener, () => module.model.readyForInteraction, module.model)
  }

  val motionControlPanel = new MotionControlPanel(module.getLadybugMotionModel)
  addControl(motionControlPanel)
  addControl(createBox)

  class TraceControlPanel(m: PathVisibilityModel) extends VerticalLayoutPanel {
    add(new Label(getLocalizedString("controls.trace")) {font = new PhetFont(14, true)})
    add(new PropertyRadioButton(getLocalizedString("trace.line"), m.pathType, Line))
    add(new PropertyRadioButton(getLocalizedString("trace.dots"), m.pathType, Dots))
    add(new PropertyRadioButton(getLocalizedString("trace.off"), m.pathType, None))
  }

  addControl(new TraceControlPanel(module.pathVisibilityModel))
  addControl(createBox)

  addControl(new LadybugDeveloperControl(module))

  val remoteControl = new RemoteControl(module.model, () => {module.model.startRecording()})
  addControl(remoteControl)
  addControl(createBox)
  val resetAllButton = new ResetAllButton(new Resettable() {
    def reset() {
      module.resetAll()
    }
  }, this)

  getContentPanel.setAnchor(CENTER)
  addControl(resetAllButton)

  def resetAll() {
    remoteControl.resetAll()
  }
}