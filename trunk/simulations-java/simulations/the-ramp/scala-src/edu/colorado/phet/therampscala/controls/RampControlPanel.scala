package edu.colorado.phet.therampscala.controls


import common.phetcommon.view.util.PhetFont
import common.phetcommon.view.{ControlPanel, VerticalLayoutPanel, ResetAllButton}
import java.awt._
import javax.swing._
import model.RampModel
import scalacommon.swing.MyRadioButton
import scalacommon.util.Observable
import swing.{MyCheckBox, ScalaValueControl}
import edu.colorado.phet.scalacommon.Predef._

class RampControlPanel(model: RampModel, wordModel: WordModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                       coordinateSystemModel: CoordinateSystemModel, vectorViewModel: VectorViewModel) extends ControlPanel {
  getContentPanel.setAnchor(GridBagConstraints.WEST)
  getContentPanel.setFill(GridBagConstraints.HORIZONTAL)
  override def add(comp: Component) = {
    addControl(comp)
    comp
  }

  add(new MyRadioButton("Physics words", wordModel.physicsWords = true, wordModel.physicsWords, wordModel.addListener))
  add(new MyRadioButton("Everyday words", wordModel.everydayWords = true, wordModel.everydayWords, wordModel.addListener))

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

  add(new TitleLabel("Coordinate System"))
  add(boxLayout(
    new MyRadioButton("Fixed", coordinateSystemModel.fixed = true, coordinateSystemModel.fixed, coordinateSystemModel.addListener),
    new MyRadioButton("Adjustable", coordinateSystemModel.adjustable = true, coordinateSystemModel.adjustable, coordinateSystemModel.addListener)
    ))

  class SubControlPanel(title: String) extends VerticalLayoutPanel {
    add(new TitleLabel(title))
    setBorder(BorderFactory.createRaisedBevelBorder)
  }

  val vectorPanel = new SubControlPanel("Vectors")
  vectorPanel.add(new MyRadioButton("Centered", vectorViewModel.centered = true, vectorViewModel.centered, vectorViewModel.addListener))
  vectorPanel.add(new MyRadioButton("Point of Origin", vectorViewModel.centered = false, !vectorViewModel.centered, vectorViewModel.addListener))
  vectorPanel.add(Box.createRigidArea(new Dimension(10, 10)))
  vectorPanel.add(new MyCheckBox("Original", vectorViewModel.originalVectors_=, vectorViewModel.originalVectors, vectorViewModel.addListener))
  vectorPanel.add(new MyCheckBox("Parallel Components", vectorViewModel.parallelComponents_=, vectorViewModel.parallelComponents, vectorViewModel.addListener))
  vectorPanel.add(new MyCheckBox("X-Y Components", vectorViewModel.xyComponents_=, vectorViewModel.xyComponents, vectorViewModel.addListener))
  vectorPanel.add(new MyCheckBox("Sum of Forces", vectorViewModel.sumOfForcesVector_=, vectorViewModel.sumOfForcesVector, vectorViewModel.addListener))

  add(vectorPanel)

  val rampPanel = new SubControlPanel("Ramp Controls")
  //  rampPanel.add(new MyCheckBox("Walls", model.walls_=, model.walls, model.addListener))
  rampPanel.add(new MyCheckBox("Frictionless", model.frictionless_=, model.frictionless, model.addListener))

  val positionSlider = new ScalaValueControl(RampDefaults.MIN_X, RampDefaults.MAX_X, "Object Position", "0.0", "meters",
    model.beads(0).position, model.beads(0).setPosition, model.beads(0).addListener)
  rampPanel.add(positionSlider)

  val angleSlider = new ScalaValueControl(0, 90, "Ramp Angle", "0.0", "degrees",
    model.rampSegments(1).getUnitVector.getAngle.toDegrees, value => model.setRampAngle(value.toRadians), model.rampSegments(1).addListener)

  rampPanel.add(angleSlider)

  add(rampPanel)

  getContentPanel.setAnchor(GridBagConstraints.SOUTH) //todo: make reset appear at the bottom
  getContentPanel.setFill(GridBagConstraints.NONE)
  val resetButton = new ResetAllButton(this)
  add(resetButton)

  val stepButton=new JButton("Step")
  stepButton.addActionListener(()=>model.stepRecord(RampDefaults.DT_DEFAULT))
  add(stepButton)
}