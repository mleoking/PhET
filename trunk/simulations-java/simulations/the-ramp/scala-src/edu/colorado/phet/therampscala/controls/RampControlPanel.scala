package edu.colorado.phet.therampscala.controls


import common.phetcommon.view.{VerticalLayoutPanel, ResetAllButton}
import javax.swing.{JLabel, BorderFactory}
import scalacommon.swing.MyRadioButton
import swing.{MyCheckBox, ScalaValueControl}
import edu.colorado.phet.scalacommon.Predef._

class RampControlPanel(model: RampModel, wordModel: WordModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                      coordinateSystemModel: CoordinateSystemModel, vectorViewModel: VectorViewModel) extends VerticalLayoutPanel {
  //  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))

  add(new MyRadioButton("Physics words", wordModel.physicsWords = true, wordModel.physicsWords, wordModel.addListener))
  add(new MyRadioButton("Everyday words", wordModel.everydayWords = true, wordModel.everydayWords, wordModel.addListener))

  add(new JLabel("Free Body Diagram"))
  add(new MyRadioButton("Show", freeBodyDiagramModel.visible = true, freeBodyDiagramModel.visible, freeBodyDiagramModel.addListener))
  add(new MyRadioButton("Hide", freeBodyDiagramModel.visible = false, !freeBodyDiagramModel.visible, freeBodyDiagramModel.addListener))

  add(new JLabel("Coordinate System"))
  add(new MyRadioButton("Fixed", coordinateSystemModel.fixed = true, coordinateSystemModel.fixed, coordinateSystemModel.addListener))
  add(new MyRadioButton("Adjustable", coordinateSystemModel.adjustable = true, coordinateSystemModel.adjustable, coordinateSystemModel.addListener))

  class SubControlPanel(title: String) extends VerticalLayoutPanel {
    add(new JLabel(title))
    setBorder(BorderFactory.createRaisedBevelBorder)
  }

  val vectorPanel = new SubControlPanel("Vectors")
  vectorPanel.add(new MyCheckBox("Original Vectors", vectorViewModel.originalVectors_=, vectorViewModel.originalVectors, vectorViewModel.addListener))
  vectorPanel.add(new MyCheckBox("Parallel Components", vectorViewModel.parallelComponents_=, vectorViewModel.parallelComponents, vectorViewModel.addListener))
  vectorPanel.add(new MyCheckBox("X-Y Components", vectorViewModel.xyComponents_=, vectorViewModel.xyComponents, vectorViewModel.addListener))
  vectorPanel.add(new MyCheckBox("Sum of Forces Vector", vectorViewModel.sumOfForcesVector_=, vectorViewModel.sumOfForcesVector, vectorViewModel.addListener))

  add(vectorPanel)

  val rampPanel = new SubControlPanel("Ramp Controls")
  rampPanel.add(new MyCheckBox("Walls", model.walls_=, model.walls, model.addListener))
  rampPanel.add(new MyCheckBox("Frictionless", model.frictionless_=, model.frictionless, model.addListener))

  add(rampPanel)

  val positionSlider = new ScalaValueControl(RampDefaults.MIN_X, RampDefaults.MAX_X, "Object Position", "0.0", "meters",
    model.beads(0).position, model.beads(0).setPosition, model.beads(0).addListener)
  add(positionSlider)

  val angleSlider = new ScalaValueControl(0, 90, "Ramp Angle", "0.0", "degrees",
    model.rampSegments(1).getUnitVector.getAngle.toDegrees, value => model.setRampAngle(value.toRadians), model.rampSegments(1).addListener)

  add(angleSlider)

  val resetButton = new ResetAllButton(this)
  add(resetButton)
}