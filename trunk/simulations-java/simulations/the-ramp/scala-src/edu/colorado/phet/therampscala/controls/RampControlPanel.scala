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
import RampResources._

class RampControlPanel(model: RampModel, wordModel: WordModel,
                       freeBodyDiagramModel: FreeBodyDiagramModel,
                       coordinateSystemModel: CoordinateSystemModel,
                       vectorViewModel: VectorViewModel, resetHandler: () => Unit,
                       coordinateSystemFeaturesEnabled: Boolean,
                       useObjectComboBox: Boolean, objectModel: ObjectModel) extends JPanel(new BorderLayout) {
  val body = new RampControlPanelBody(model, wordModel, freeBodyDiagramModel, coordinateSystemModel, vectorViewModel, resetHandler,
    coordinateSystemFeaturesEnabled, useObjectComboBox, objectModel)

  val southControlPanel = new JPanel()
  val resetButton = new ResetAllButton(this)
  resetButton.addResettable(new Resettable {def reset = resetHandler()})
  southControlPanel.add(resetButton)

  add(body, BorderLayout.NORTH)
  add(southControlPanel, BorderLayout.SOUTH)

  def addToBody(component: JComponent) = body.add(component)
}

class RampControlPanelBody(model: RampModel, wordModel: WordModel,
                           freeBodyDiagramModel: FreeBodyDiagramModel,
                           coordinateSystemModel: CoordinateSystemModel,
                           vectorViewModel: VectorViewModel, resetHandler: () => Unit,
                           coordinateSystemFeaturesEnabled: Boolean,
                           useObjectComboBox: Boolean, objectModel: ObjectModel) extends ControlPanel {
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

  add(new TitleLabel("display.free-body-diagram".translate))
  add(boxLayout(
    new MyRadioButton("controls.show".translate, freeBodyDiagramModel.visible = true, freeBodyDiagramModel.visible, freeBodyDiagramModel.addListener),
    new MyRadioButton("controls.hide".translate, freeBodyDiagramModel.visible = false, !freeBodyDiagramModel.visible, freeBodyDiagramModel.addListener)
    ))

  if (coordinateSystemFeaturesEnabled) {
    add(new TitleLabel("coordinates.coordinate-system".translate))
    add(boxLayout(
      new MyRadioButton("coordinates.fixed".translate, coordinateSystemModel.fixed = true, coordinateSystemModel.fixed, coordinateSystemModel.addListener),
      new MyRadioButton("coordinates.adjustable".translate, coordinateSystemModel.adjustable = true, coordinateSystemModel.adjustable, coordinateSystemModel.addListener)
      ))
  }

  class IconPanel(component: JComponent, iconFilename: String) extends JPanel {
    setLayout(new BorderLayout)
    add(component, BorderLayout.WEST)
    add(new JLabel(new ImageIcon(RampResources.getImage(iconFilename))), BorderLayout.EAST)
  }

  val vectorPanel = new SubControlPanel("vectors.title".translate) with IProguardKeepClass {
    def addWithIcon(iconFilename: String, component: JComponent) = add(new IconPanel(component, iconFilename))
  }
  vectorPanel.add(new MyRadioButton("vectors.centered".translate, vectorViewModel.centered = true, vectorViewModel.centered, vectorViewModel.addListener))
  vectorPanel.add(new MyRadioButton("vectors.point-of-origin".translate, vectorViewModel.centered = false, !vectorViewModel.centered, vectorViewModel.addListener))
  vectorPanel.add(Box.createRigidArea(new Dimension(10, 10)))
  vectorPanel.add(new MyCheckBox("vectors.force-vectors".translate, vectorViewModel.originalVectors_=, vectorViewModel.originalVectors, vectorViewModel.addListener))
  if (coordinateSystemFeaturesEnabled) {
    vectorPanel.addWithIcon("parallel_components_icon.gif".literal, new MyCheckBox("vectors.parallel-components".translate, vectorViewModel.parallelComponents_=, vectorViewModel.parallelComponents, vectorViewModel.addListener))
    vectorPanel.addWithIcon("xy_components_icon.gif".literal, new MyCheckBox("vectors.x-y-components".translate, vectorViewModel.xyComponentsVisible = _, vectorViewModel.xyComponentsVisible, vectorViewModel.addListener))
  }
  vectorPanel.add(Box.createRigidArea(new Dimension(10, 10)))
  vectorPanel.addWithIcon("sum_of_forces_icon.gif".literal, new MyCheckBox("vectors.sum-of-forces".translate, vectorViewModel.sumOfForcesVector_=, vectorViewModel.sumOfForcesVector, vectorViewModel.addListener))

  add(vectorPanel)

  val rampPanel = new SubControlPanel("ramp.controls.title".translate)
  rampPanel.add(new MyCheckBox("controls.frictionless".translate, model.frictionless_=, model.frictionless, model.addListener))

  val positionSlider = new ScalaValueControl(RampDefaults.MIN_X, RampDefaults.MAX_X, "object.position".translate, "0.0".literal, "units.meters".translate,
    () => model.bead.position, model.bead.setPosition, model.bead.addListener)
  rampPanel.add(positionSlider)

  val angleSlider = new ScalaValueControl(0, 90, "property.ramp-angle".translate, "0.0".literal, "units.degrees".translate,
    () => model.rampSegments(1).getUnitVector.getAngle.toDegrees, value => model.setRampAngle(value.toRadians), model.rampSegments(1).addListener)

  rampPanel.add(angleSlider)

  add(rampPanel)

  if (useObjectComboBox) add(new RampComboBox(objectModel))

  getContentPanel.setFillNone()
  getContentPanel.setAnchor(GridBagConstraints.CENTER)
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