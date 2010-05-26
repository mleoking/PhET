package edu.colorado.phet.motionseries.controls

import edu.colorado.phet.common.phetcommon.model.Resettable
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.phetcommon.view.util.{BufferedImageUtils, PhetFont}
import edu.colorado.phet.common.phetcommon.view.{ControlPanel, VerticalLayoutPanel}
import edu.colorado.phet.motionseries.graphics._
import java.awt.event.{MouseEvent, MouseAdapter}
import java.awt._
import java.awt.geom._
import java.awt.image._
import javax.swing._
import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.motionseries.swing._
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.swing.MyRadioButton
import edu.colorado.phet.motionseries.MotionSeriesResources
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import edu.umd.cs.piccolo.{PNode}
import edu.umd.cs.piccolox.pswing.{PSwingCanvas, PSwing}

class RampControlPanel(model: MotionSeriesModel,
                       freeBodyDiagramModel: FreeBodyDiagramModel,
                       coordinateSystemModel: AdjustableCoordinateModel,
                       vectorViewModel: VectorViewModel,
                       resetHandler: () => Unit,
                       coordinateSystemFeaturesEnabled: Boolean,
                       useObjectComboBox: Boolean,
                       objectModel: ObjectModel,
                       showAngleSlider: Boolean,
                       showFrictionControl: Boolean,
                       showBounceControl: Boolean)
        extends ControlPanel {
  val body = new RampControlPanelBody(model, freeBodyDiagramModel, coordinateSystemModel, vectorViewModel, resetHandler,
    coordinateSystemFeaturesEnabled, useObjectComboBox, objectModel, showAngleSlider, showFrictionControl, showBounceControl)

  addControl(body)
  addResetAllButton(new Resettable {def reset = resetHandler()})

  def addPrimaryControl(component: JComponent): Unit = body.add(component)

  def addPrimaryControl(component: scala.swing.Component): Unit = addPrimaryControl(component.peer)
}

class RampControlPanelBody(model: MotionSeriesModel,
                           freeBodyDiagramModel: FreeBodyDiagramModel,
                           coordinateSystemModel: AdjustableCoordinateModel,
                           vectorViewModel: VectorViewModel,
                           resetHandler: () => Unit,
                           coordinateSystemFeaturesEnabled: Boolean,
                           useObjectComboBox: Boolean,
                           objectModel: ObjectModel,
                           showAngleSlider: Boolean,
                           showFrictionControl: Boolean,
                           showBounceControl: Boolean) extends ControlPanel {
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
    new MyRadioButton("controls.show".translate, freeBodyDiagramModel.visible = true, freeBodyDiagramModel.visible, freeBodyDiagramModel.addListener).peer,
    new MyRadioButton("controls.hide".translate, freeBodyDiagramModel.visible = false, !freeBodyDiagramModel.visible, freeBodyDiagramModel.addListener).peer
    ))

  class IconPanel(component: JComponent, icon: Icon) extends JPanel {
    def this(component: JComponent, iconFilename: String) = this (component, new ImageIcon(MotionSeriesResources.getImage(iconFilename)))
    setLayout(new BorderLayout)
    add(component, BorderLayout.WEST)
    add(new JLabel(icon), BorderLayout.EAST)
  }

  val vectorPanel = new SubControlPanel("vectors.title".translate) with IProguardKeepClass {
    //We decided to remove the point of origin visualization option from this sim, thinking we would
    //add support for it in a separate sim.  I'll leave this here but commented out in case we change our minds.
    //    add(new MyRadioButton("vectors.centered".translate, vectorViewModel.centered = true, vectorViewModel.centered, vectorViewModel.addListener).peer)
    //    add(new MyRadioButton("vectors.point-of-origin".translate, vectorViewModel.centered = false, !vectorViewModel.centered, vectorViewModel.addListener).peer)
    //    add(Box.createRigidArea(new Dimension(10, 10)))

    if (coordinateSystemFeaturesEnabled) {
      //in coordinates frame mode, you have to show one of the vector choices
      def setVectorVisibility(original:Boolean,parallel:Boolean,xy:Boolean) = {
        vectorViewModel.originalVectors = original
        vectorViewModel.parallelComponents = parallel
        vectorViewModel.xyComponentsVisible = xy
      }
      add(new MyRadioButton("vectors.force-vectors".translate, setVectorVisibility(true,false,false), vectorViewModel.originalVectors, vectorViewModel.addListener).peer)
      addWithIcon("parallel_components_icon.gif".literal, new MyRadioButton("vectors.parallel-components".translate, setVectorVisibility(false,true,false), vectorViewModel.parallelComponents, vectorViewModel.addListener).peer)
      addWithIcon("xy_components_icon.gif".literal, new MyRadioButton("vectors.x-y-components".translate, setVectorVisibility(false,false,true), vectorViewModel.xyComponentsVisible, vectorViewModel.addListener).peer)
    }else{
      //show a check box that allows you to turn off viewing the vectors
      add(new MyCheckBox("vectors.force-vectors".translate, vectorViewModel.originalVectors_=, vectorViewModel.originalVectors, vectorViewModel.addListener).peer)
    }
    add(Box.createRigidArea(new Dimension(10, 10)))
    addWithIcon(createSumForceIcon, new MyCheckBox("vectors.sum-of-forces".translate, vectorViewModel.sumOfForcesVector_=, vectorViewModel.sumOfForcesVector, vectorViewModel.addListener).peer)

    def addWithIcon(iconFilename: String, component: JComponent) = add(new IconPanel(component, iconFilename))

    def addWithIcon(image: BufferedImage, component: JComponent) = add(new IconPanel(component, new ImageIcon(image)))

    def createSumForceIcon = {
      val rect = new Rectangle2D.Double(0, 0, 1, 1)
      val vector = new Vector(MotionSeriesDefaults.totalForceColor, "total-force".literal, "force.abbrev.total".translate, () => new Vector2D(42, 0), (v: Vector2D, c: Color) => {c},0.0)
      val vectorNode = new VectorNode(new ModelViewTransform2D(rect, rect), vector, new ConstantVectorValue(new Vector2D(-42, 0)), 75)
      val bufIm = BufferedImageUtils.toBufferedImage(vectorNode.toImage)
      BufferedImageUtils.multiScaleToHeight(bufIm, 35)
    }
  }

  if (showFrictionControl) {
    val frictionPanel = new SubControlPanel("controls.friction".translate)

    def getSegmentIcon(_frictionless: Boolean) = {
      val dummyModelBounds = new Rectangle2D.Double(0, 0, 10, 10)
      val dummyViewBounds = new Rectangle2D.Double(0, 0, 800, 600)
      val surfaceModel = new RampSurfaceModel {def frictionless = _frictionless}

      val segment = new RampSegment(new Point2D.Double(0, 0), new Point2D.Double(3, 0))
      val node = new RampSegmentNode(segment, new ModelViewTransform2D(dummyModelBounds, dummyViewBounds, false), surfaceModel)
      node.toImage(75, (75.0 / node.getFullBounds.getWidth * node.getFullBounds.getHeight).toInt, new Color(255, 255, 255, 0))
    }

    def getIceIcon = getSegmentIcon(true)

    def getWoodIcon = getSegmentIcon(false)

    val onButton = new MyRadioButton("surface.ice-no-friction".translate, model.frictionless = true, model.frictionless, model.addListener)

    val onButtonPanel = new JPanel() {
      add(onButton.peer)
      add(new JLabel(new ImageIcon(getIceIcon)))
    }

    val offButton = new MyRadioButton("surface.wood".translate, model.frictionless = false, !model.frictionless, model.addListener)

    val offButtonPanel = new JPanel() {
      add(offButton.peer)
      add(new JLabel(new ImageIcon(getWoodIcon)))
    }

    val panel = new VerticalLayoutPanel
    panel.add(onButtonPanel)
    panel.add(offButtonPanel)
    frictionPanel.add(panel)
    add(frictionPanel)
  }

  if (coordinateSystemFeaturesEnabled) {
    add(new TitleLabel("coordinates.coordinate-system".translate))
    add(boxLayout(
      new MyRadioButton("coordinates.fixed".translate, coordinateSystemModel.fixed = true, coordinateSystemModel.fixed, coordinateSystemModel.addListener).peer,
      new MyRadioButton("coordinates.adjustable".translate, coordinateSystemModel.adjustable = true, coordinateSystemModel.adjustable, coordinateSystemModel.addListener).peer
      ))
  }
  add(vectorPanel)

  if (showBounceControl) {
    val bouncePanel = new SubControlPanel("walls.type".translate)
    val onButton = new MyRadioButton("walls.brick".translate, model.bounce = false, !model.bounce, model.addListener)
    val offButton = new MyRadioButton("walls.bouncy".translate, model.bounce = true, model.bounce, model.addListener)
    val panel = new JPanel
    panel.add(onButton.peer)
    panel.add(offButton.peer)
    bouncePanel.add(panel)
    add(bouncePanel)
    defineInvokeAndPass(model.addListenerByName) {
      bouncePanel.titleLabel.setEnabled(model.walls)
      onButton.peer.setEnabled(model.walls)
      offButton.peer.setEnabled(model.walls)
    }
  }

  val moreControlsPanel = new SubControlPanel("more.controls.title".translate)
  val positionSlider = new ScalaValueControl(MotionSeriesDefaults.MIN_X, MotionSeriesDefaults.MAX_X, "object.position".translate, "0.0".literal, "units.meters".translate,
    () => model.bead.position, x => model.bead.setPosition(x), model.bead.addListener)
  positionSlider.getSlider.addMouseListener(new MouseAdapter() {
    override def mousePressed(e: MouseEvent) = {
      val x: Double = if (model.bead.position > MotionSeriesDefaults.MAX_X) MotionSeriesDefaults.MAX_X
      else if (model.bead.position < MotionSeriesDefaults.MIN_X) MotionSeriesDefaults.MIN_X
      else model.bead.position
      model.bead.setPosition(x)
      model.bead.attach()
      model.bead.setVelocity(0.0)
    }
  })
  moreControlsPanel.add(positionSlider)

  if (showAngleSlider) {
    val angleSlider = new ScalaValueControl(0, MotionSeriesDefaults.MAX_ANGLE.toDegrees, "property.ramp-angle".translate, "0.0".literal, "units.degrees".translate,
      () => model.rampSegments(1).getUnitVector.angle.toDegrees, value => model.rampAngle = value.toRadians, model.rampSegments(1).addListener)
    moreControlsPanel.add(angleSlider)
  }

  add(moreControlsPanel)

  //Embed in its own PhetPCanvas so we can easily reuse the PComboBox code
  class EmbeddedObjectSelectionPanel extends PhetPCanvas {
    val boxNode = new ComboBoxNode(objectModel, this)
    addScreenChild(boxNode)
    setPreferredSize(new Dimension(boxNode.getFullBounds.getWidth.toInt, boxNode.getFullBounds.getHeight.toInt))
    setBackground(new TitleLabel("hello".literal).getBackground)
  }

  if (useObjectComboBox) add(new EmbeddedObjectSelectionPanel)

  getContentPanel.setFillNone()
  getContentPanel.setAnchor(GridBagConstraints.CENTER)
}

class ComboBoxNode(objectModel: ObjectModel, canvas: PSwingCanvas) extends PNode {
  val text = new PSwing(new TitleLabel("controls.choose-object".translate))
  addChild(text)
  val boxPanel = new ObjectSelectionComboBox(objectModel)
  val pswing = new PSwing(boxPanel)
  pswing.setOffset(0, text.getFullBounds.getHeight)
  boxPanel.setEnvironment(pswing, canvas)
  addChild(pswing)
}

class SubControlPanel(title: String) extends VerticalLayoutPanel {
  val titleLabel = new TitleLabel(title)
  add(titleLabel)
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