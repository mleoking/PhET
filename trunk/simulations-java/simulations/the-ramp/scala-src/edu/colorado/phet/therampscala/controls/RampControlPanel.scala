package edu.colorado.phet.therampscala.controls

import common.phetcommon.model.Resettable
import common.phetcommon.util.IProguardKeepClass
import common.phetcommon.view.util.{PhetFont}
import common.phetcommon.view.{ControlPanel, VerticalLayoutPanel, ResetAllButton}
import graphics.ObjectModel
import java.awt._
import javax.swing._
import model._


import scalacommon.swing.MyRadioButton
import swing.{MyCheckBox, ScalaValueControl}
import edu.colorado.phet.scalacommon.Predef._

class RampControlPanel(model: RampModel, wordModel: WordModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                       coordinateSystemModel: CoordinateSystemModel, vectorViewModel: VectorViewModel, resetHandler: () => Unit,
                       coordinateSystemFeaturesEnabled: Boolean, useObjectComboBox: Boolean, objectModel: ObjectModel) extends ControlPanel {
  getContentPanel.setAnchor(GridBagConstraints.WEST)
  getContentPanel.setFill(GridBagConstraints.HORIZONTAL)
  override def add(comp: Component) = {
    addControl(comp)
    comp
  }

  def boxLayout(a: JComponent*) = {
    val panel = new JPanel
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS))
    a.foreach(panel.add(_))
    panel
  }

  add(new TitleLabel("Free Body Diagram"))
  add(boxLayout(
    new MyRadioButton("Show", freeBodyDiagramModel.visible = true, freeBodyDiagramModel.visible, freeBodyDiagramModel.addListener),
    new MyRadioButton("Hide", freeBodyDiagramModel.visible = false, !freeBodyDiagramModel.visible, freeBodyDiagramModel.addListener)
    ))

  if (coordinateSystemFeaturesEnabled) {
    add(new TitleLabel("Coordinate System"))
    add(boxLayout(
      new MyRadioButton("Fixed", coordinateSystemModel.fixed = true, coordinateSystemModel.fixed, coordinateSystemModel.addListener),
      new MyRadioButton("Adjustable", coordinateSystemModel.adjustable = true, coordinateSystemModel.adjustable, coordinateSystemModel.addListener)
      ))
  }

  class IconPanel(component: JComponent, iconFilename: String) extends JPanel {
    setLayout(new BorderLayout)
    add(component, BorderLayout.WEST)
    add(new JLabel(new ImageIcon(RampResources.getImage(iconFilename))), BorderLayout.EAST)
  }

  val vectorPanel = new SubControlPanel("Vectors") with IProguardKeepClass {
    def addWithIcon(iconFilename: String, component: JComponent) = add(new IconPanel(component, iconFilename))
  }
  vectorPanel.add(new MyRadioButton("Centered", vectorViewModel.centered = true, vectorViewModel.centered, vectorViewModel.addListener))
  vectorPanel.add(new MyRadioButton("Point of Origin", vectorViewModel.centered = false, !vectorViewModel.centered, vectorViewModel.addListener))
  vectorPanel.add(Box.createRigidArea(new Dimension(10, 10)))
  vectorPanel.add(new MyCheckBox("Force Vectors", vectorViewModel.originalVectors_=, vectorViewModel.originalVectors, vectorViewModel.addListener))
  if (coordinateSystemFeaturesEnabled) {
    vectorPanel.addWithIcon("parallel_components_icon.gif", new MyCheckBox("Parallel Components", vectorViewModel.parallelComponents_=, vectorViewModel.parallelComponents, vectorViewModel.addListener))
    vectorPanel.addWithIcon("xy_components_icon.gif", new MyCheckBox("X-Y Components", vectorViewModel.xyComponentsVisible = _, vectorViewModel.xyComponentsVisible, vectorViewModel.addListener))
  }
  vectorPanel.add(Box.createRigidArea(new Dimension(10, 10)))
  vectorPanel.addWithIcon("sum_of_forces_icon.gif", new MyCheckBox("Sum of Forces", vectorViewModel.sumOfForcesVector_=, vectorViewModel.sumOfForcesVector, vectorViewModel.addListener))

  add(vectorPanel)

  val rampPanel = new SubControlPanel("Ramp Controls")
  rampPanel.add(new MyCheckBox("Frictionless", model.frictionless_=, model.frictionless, model.addListener))

  val positionSlider = new ScalaValueControl(RampDefaults.MIN_X, RampDefaults.MAX_X, "Object Position", "0.0", "meters",
    () => model.bead.position, model.bead.setPosition, model.bead.addListener)
  rampPanel.add(positionSlider)

  val angleSlider = new ScalaValueControl(0, 90, "Ramp Angle", "0.0", "degrees",
    () => model.rampSegments(1).getUnitVector.getAngle.toDegrees, value => model.setRampAngle(value.toRadians), model.rampSegments(1).addListener)

  rampPanel.add(angleSlider)

  add(rampPanel)

  if (useObjectComboBox) add(new RampComboBox(objectModel))

  getContentPanel.setFillNone()

  getContentPanel.setAnchor(GridBagConstraints.CENTER) //todo: make reset appear at the bottom
  val stepButton = new JButton("Step")
  stepButton.addActionListener(() => model.stepRecord(RampDefaults.DT_DEFAULT))
  add(stepButton)

  //  addResetAllButton(new Resettable {def reset = resetHandler()})
  getContentPanel.setAnchor(GridBagConstraints.SOUTH)
  getContentPanel.setFill(GridBagConstraints.NONE)
  val resetButton = new ResetAllButton(this)
  resetButton.addResettable(new Resettable {def reset = resetHandler()})
  add(resetButton)
}

class SubControlPanel(title: String) extends VerticalLayoutPanel {
  add(new TitleLabel(title))
  setBorder(BorderFactory.createRaisedBevelBorder)
}

class TitleLabel(label: String) extends JLabel(label) {
  setFont(new PhetFont(15, true))

  override def paintComponent(g: Graphics) = {
    g match {
      case g2: Graphics2D => g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      case _ => {}
    }
    super.paintComponent(g)
  }
}