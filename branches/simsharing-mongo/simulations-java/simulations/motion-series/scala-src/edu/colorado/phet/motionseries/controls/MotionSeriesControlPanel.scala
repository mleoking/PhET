package edu.colorado.phet.motionseries.controls

import edu.colorado.phet.common.phetcommon.model.Resettable
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.phetcommon.view.util.{BufferedImageUtils, PhetFont}
import java.awt._
import java.awt.geom._
import java.awt.image._
import javax.swing._
import edu.colorado.phet.motionseries.swing._
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.swing.MyRadioButton
import edu.colorado.phet.motionseries.MotionSeriesResources
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import edu.colorado.phet.common.phetcommon.math.MathUtil
import java.awt.event.{MouseEvent, MouseAdapter}
import edu.colorado.phet.common.phetcommon.view.{PhetTitledBorder, ControlPanel, VerticalLayoutPanel}
import edu.colorado.phet.motionseries.graphics.{RampSegmentNode, RampSurfaceModel, VectorNode, ObjectSelectionModel}
import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.motionseries.util.ScalaMutableBoolean
import edu.colorado.phet.motionseries.sims.forcesandmotionbasics.Settings

class MotionSeriesControlPanel(model: MotionSeriesModel,
                               freeBodyDiagramModel: FreeBodyDiagramModel,
                               coordinateSystemModel: AdjustableCoordinateModel,
                               vectorViewModel: VectorViewModel,
                               resetHandler: () => Unit,
                               coordinateSystemFeaturesEnabled: Boolean,
                               useObjectComboBox: Boolean,
                               objectModel: ObjectSelectionModel,
                               showAngleSlider: Boolean,
                               showFrictionControl: Boolean,
                               showBounceControl: Boolean,
                               subControlPanelTitle: String,
                               audioEnabled: ScalaMutableBoolean,

                               //Flag to indicate whether the FBD on/off panel should be shown, disabled for "Basics" sim
                               showFBDPanel: Boolean = true,

                               //Flag to indicate whether a control will be shown to enable/disable "show gravity and normal forces"
                               showGravityNormalForceCheckBox: Boolean = false)
        extends ControlPanel {
  val body = new RampControlPanelBody(model, freeBodyDiagramModel, coordinateSystemModel, vectorViewModel, resetHandler,
                                      coordinateSystemFeaturesEnabled, useObjectComboBox, objectModel, showAngleSlider,
                                      showFrictionControl, showBounceControl, subControlPanelTitle, showFBDPanel, showGravityNormalForceCheckBox)

  addControl(body)
  addControl(new AudioEnabledCheckBox(audioEnabled))
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
                           objectModel: ObjectSelectionModel,
                           showAngleSlider: Boolean,
                           showFrictionControl: Boolean,
                           showBounceControl: Boolean,
                           subControlPanelTitle: String,

                           //Flag to indicate whether the FBD on/off panel should be shown, disabled for "Basics" sim
                           showFBDPanel: Boolean,

                           //Flag to indicate whether a control will be shown to enable/disable "show gravity and normal forces"
                           showGravityNormalForceCheckBox: Boolean) extends ControlPanel {
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

  val fbdPanel = new SubControlPanel("display.free-body-diagram".translate) {
    add(boxLayout(
      new MyRadioButton("controls.show".translate, freeBodyDiagramModel.visible = true, freeBodyDiagramModel.visible, freeBodyDiagramModel.addListener).peer,
      new MyRadioButton("controls.hide".translate, freeBodyDiagramModel.visible = false, !freeBodyDiagramModel.visible, freeBodyDiagramModel.addListener).peer
    ))
  }

  //Show the FBD on/off panel, but not for the "Basics" sim
  if ( showFBDPanel ) {
    add(fbdPanel)
  }

  class IconPanel(component: JComponent, icon: Icon) extends JPanel {
    def this(component: JComponent, iconFilename: String) = this (component, new ImageIcon(MotionSeriesResources.getImage(iconFilename)))

    setLayout(new BorderLayout)
    add(component, BorderLayout.WEST)
    add(new JLabel(icon), BorderLayout.EAST)
  }

  val vectorPanel = new SubControlPanel("vectors.title".translate) with IProguardKeepClass {

    //show a check box that allows you to turn off viewing the vectors
    add(new MyCheckBox("vectors.force-vectors".translate, vectorViewModel.originalVectors_=, vectorViewModel.originalVectors, vectorViewModel.addListener).peer)
    add(Box.createRigidArea(new Dimension(10, 10)))

    //For "Basics" application, use the word "total" instead of "sum"
    val string = if ( Settings.basicsMode ) {
      "Total Force"
    }
    else {
      "vectors.sum-of-forces".translate
    }
    addWithIcon(createSumForceIcon, new MyCheckBox(string, vectorViewModel.sumOfForcesVector_=, vectorViewModel.sumOfForcesVector, vectorViewModel.addListener).peer)

    //In the "Basics" application, gravity and normal forces aren't shown by default, but there is a control to allow the user to show them
    if ( showGravityNormalForceCheckBox ) {
      add(Box.createRigidArea(new Dimension(10, 10)))
      add(new MyCheckBox("Gravity and Normal Forces", vectorViewModel.gravityAndNormalForce_=, vectorViewModel.gravityAndNormalForce, vectorViewModel.addListener).peer)
    }

    def addWithIcon(iconFilename: String, component: JComponent) = add(new IconPanel(component, iconFilename))

    def addWithIcon(image: BufferedImage, component: JComponent) = add(new IconPanel(component, new ImageIcon(image)))

    def createSumForceIcon = {
      val rect = new Rectangle2D.Double(0, 0, 1, 1)

      //For "Basics" application, use the word "total" instead of "sum"
      val string = if ( Settings.basicsMode ) {
        "total"
      }
      else {
        "force.abbrev.total".translate
      }
      val vector = new Vector(MotionSeriesDefaults.sumForceColor, "totalForce".translate, string, new Vector2DModel(42, 0), (v: Vector2D, c: Color) => {c}, 0.0)
      val vectorNode = new VectorNode(new ModelViewTransform2D(rect, rect), vector, new Vector2DModel(-42, 0), 75, 1, true)
      val bufIm = BufferedImageUtils.toBufferedImage(vectorNode.toImage)
      BufferedImageUtils.multiScaleToHeight(bufIm, 35)
    }
  }

  if ( showFrictionControl ) {
    val frictionPanel = new SubControlPanel("controls.friction".translate)

    def getSegmentIcon(_frictionless: Boolean) = {
      val dummyModelBounds = new Rectangle2D.Double(0, 0, 10, 10)
      val dummyViewBounds = new Rectangle2D.Double(0, 0, 800, 600)
      val surfaceModel = new RampSurfaceModel {def frictionless = _frictionless}

      val segment = new RampSegment(new Point2D.Double(0, 0), new Point2D.Double(3, 0))
      val node = new RampSegmentNode(segment, new ModelViewTransform2D(dummyModelBounds, dummyViewBounds, false), surfaceModel, model.motionSeriesObject)
      node.toImage(75, ( 75.0 / node.getFullBounds.getWidth * node.getFullBounds.getHeight ).toInt, new Color(255, 255, 255, 0))
    }

    val gridPanel = new JPanel {
      def iceIcon = getSegmentIcon(true)

      def woodIcon = getSegmentIcon(false)

      val iceButton = ( new MyRadioButton("surface.ice-no-friction".translate, model.frictionless = true, model.frictionless, model.addListener).peer )
      val iceLabel = ( new JLabel(new ImageIcon(iceIcon)) )

      val woodButton = ( new MyRadioButton("surface.wood".translate, model.frictionless = false, !model.frictionless, model.addListener).peer )
      val woodLabel = ( new JLabel(new ImageIcon(woodIcon)) )

      val constraints = new GridBagConstraints {
        anchor = GridBagConstraints.WEST
      }

      def addElement(c: Component) = add(c, constraints)

      setLayout(new GridBagLayout)
      constraints.gridy = 0
      addElement(iceButton)
      addElement(iceLabel)
      constraints.gridy = 1
      addElement(woodButton)
      addElement(woodLabel)
    }

    frictionPanel.add(gridPanel)
    add(frictionPanel)
  }

  add(vectorPanel)

  if ( showBounceControl ) {
    add(new SubControlPanel("walls.type".translate) {
      val brickButton = new MyRadioButton("walls.brick".translate, model.wallsBounce = false, !model.wallsBounce.booleanValue, model.wallsBounce.addListener)
      val bouncyButton = new MyRadioButton("walls.bouncy".translate, model.wallsBounce = true, model.wallsBounce.booleanValue, model.wallsBounce.addListener)
      defineInvokeAndPass(model.addListenerByName) {
                                                     brickButton.peer.setEnabled(model.walls.booleanValue)
                                                     bouncyButton.peer.setEnabled(model.walls.booleanValue)
                                                   }
      add(new JPanel {
        add(brickButton.peer)
        add(bouncyButton.peer)
      })
    })
  }

  val moreControlsPanel = new SubControlPanel(subControlPanelTitle)

  import MotionSeriesDefaults.MIN_X
  import MotionSeriesDefaults.MAX_X

  //The slider can only go from MIN_X to MAX_X even though if the walls are disabled the object might go further
  val positionSlider: ScalaValueControl = new ScalaValueControl(MIN_X, MAX_X, "object.position".translate, "0.0".literal, "units.meters".translate,
                                                                () => model.motionSeriesObject.position,
                                                                x => {
                                                                  //Use the wallRange() for determining the max location of the object, which accounts for whether walls are enabled or disabled
                                                                  val clampedValue = if ( model.walls.booleanValue ) {
                                                                    MathUtil.clamp(
                                                                      model.wallRange().min + model.motionSeriesObject.width / 2,
                                                                      x,
                                                                      model.wallRange().max - model.motionSeriesObject.width / 2)
                                                                  }
                                                                  else {
                                                                    x
                                                                  } //only clamp if there are walls
                                                                  model.motionSeriesObject.position = clampedValue
                                                                  if ( clampedValue != x ) {
                                                                    positionSlider.setValue(clampedValue)
                                                                  } //Have to make sure the readout indicates the model value, not the requested user value
                                                                },
                                                                model.motionSeriesObject.positionProperty.addListener)
  positionSlider.getSlider.addMouseListener(new MouseAdapter() {
    def setPosition(): Unit = {
      val obj = model.motionSeriesObject
      val x: Double = if ( obj.position > MotionSeriesDefaults.MAX_X ) {
        MotionSeriesDefaults.MAX_X
      }
      else if ( obj.position < MotionSeriesDefaults.MIN_X ) {
        MotionSeriesDefaults.MIN_X
      }
      else {
        obj.position
      }
      obj.position = x
      obj.attach()
      obj.velocity = 0
      obj.userSpecifiedPosition = true
    }

    override def mousePressed(e: MouseEvent) = {
      setPosition()
    }

    override def mouseReleased(e: MouseEvent) = {
      model.motionSeriesObject.userSpecifiedPosition = false
    }
  })
  moreControlsPanel.add(positionSlider)

  if ( showAngleSlider ) {
    val angleSlider = new ScalaValueControl(0, MotionSeriesDefaults.MAX_ANGLE.toDegrees, "property.ramp-angle".translate, "0.0".literal, "units.degrees".translate,
                                            () => model.rightRampSegment.unitVector.angle.toDegrees, value => model.rampAngle = value.toRadians, model.rightRampSegment.addListener)
    moreControlsPanel.add(angleSlider)
  }

  add(moreControlsPanel)

  //Embed in its own PhetPCanvas so we can easily reuse the PComboBox code
  class EmbeddedObjectSelectionPanel extends PhetPCanvas {
    val boxNode = new ObjectSelectionComboBoxNode(objectModel, this, false, _.getDisplayTextHTML)
    addScreenChild(boxNode)
    setPreferredSize(new Dimension(boxNode.getFullBounds.getWidth.toInt, boxNode.getFullBounds.getHeight.toInt))
    setBackground(new TitleLabel("hello".literal).getBackground)
    setBorder(null) //get rid of black outline
  }

  if ( useObjectComboBox ) {
    add(new SubControlPanel("controls.choose-object".translate) {
      add(new EmbeddedObjectSelectionPanel)
    })
  }

  getContentPanel.setFillNone()
  getContentPanel.setAnchor(GridBagConstraints.CENTER)
}

class SubControlPanel(title: String) extends VerticalLayoutPanel {
  setBorder(new PhetTitledBorder(title))
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