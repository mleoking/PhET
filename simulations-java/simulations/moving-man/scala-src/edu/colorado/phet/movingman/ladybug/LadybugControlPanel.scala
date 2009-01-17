package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.view.ControlPanel
import _root_.edu.colorado.phet.common.phetcommon.view.ResetAllButton
import _root_.edu.colorado.phet.common.phetcommon.view.util.PhetFont
import _root_.scala.swing._
import java.awt.Dimension
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{Box, JButton, JRadioButton, JLabel}
import LadybugMotionModel._
import LadybugUtil._

class LadybugControlPanel(module: LadybugModule) extends ControlPanel(module) {
  val myModule = module;
  def createBox = Box.createRigidArea(new Dimension(10, 10))

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
    contents += new Label("Choose Motion") {font = new PhetFont(14, true)}

    contents += new MyRadioButton("Manual", m.motion = MANUAL, m.motion == MANUAL, m)
    contents += new MyRadioButton("Linear", m.motion = LINEAR, m.motion == LINEAR, m)
    contents += new MyRadioButton("Circular", m.motion = CIRCULAR, m.motion == CIRCULAR, m)
    contents += new MyRadioButton("Ellipse", m.motion = ELLIPSE, m.motion == ELLIPSE, m)
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
  addControl(new TraceControlPanel(module.getPathVisibilityModel))
  //  addControl(new JLabel("Trace"))
  //  addControl(new JRadioButton("Solid"))
  //  addControl(new JRadioButton("Dots"))
  //  addControl(new JRadioButton("Off"))
  //  addControl(new JButton("Clear Trace"))
  addControl(createBox)

  addControl(new RemoteControl(module.model))
  addControl(createBox)
  addControl(new ResetAllButton(this))
}