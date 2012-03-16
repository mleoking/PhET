package edu.colorado.phet.motionseries.sims.forcesandmotionbasics

import edu.colorado.phet.common.phetcommon.view.PhetFrame
import java.awt.geom.Rectangle2D
import edu.colorado.phet.common.phetcommon.application.{PhetApplicationConfig, PhetApplicationLauncher}
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication
import edu.colorado.phet.motionseries.graphics._
import javax.swing.JFrame
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.umd.cs.piccolox.pswing.PSwing
import edu.colorado.phet.motionseries.controls.MotionSeriesControlPanel
import edu.colorado.phet.recordandplayback.gui.RecordAndPlaybackControlPanel
import edu.colorado.phet.motionseries.sims.rampforcesandmotion.robotmovingcompany.RobotMovingCompanyModule
import edu.colorado.phet.motionseries.{StageContainerArea, MotionSeriesModule, MotionSeriesDefaults}
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.motionseries.model._

class ForcesAndMotionBasicsModule(frame: PhetFrame,
                                  name: String,
                                  coordinateSystemFeaturesEnabled: Boolean,
                                  showObjectSelectionNode: Boolean,
                                  useObjectComboBox: Boolean,
                                  showAppliedForceSlider: Boolean,
                                  defaultPosition: Double,
                                  initialAngle: Double,
                                  showFrictionControl: Boolean,
                                  showBounceControl: Boolean,
                                  modelViewport: Rectangle2D,
                                  stageContainerArea: StageContainerArea,
                                  fbdPopupOnly: Boolean,

                                  //Flag to indicate whether a control will be shown to enable/disable "show gravity and normal forces"
                                  _showGravityNormalForceCheckBox: Boolean = false)
        extends MotionSeriesModule(frame, new ScalaClock(MotionSeriesDefaults.DELAY, MotionSeriesDefaults.DT_DEFAULT), name, defaultPosition, initialAngle, fbdPopupOnly) {
  val canvas = new ForcesAndMotionBasicsCanvas(motionSeriesModel, coordinateSystemModel, fbdModel, vectorViewModel, frame,
                                               showObjectSelectionNode, showAppliedForceSlider, initialAngle != 0.0, modelViewport, stageContainerArea)
  setSimulationPanel(canvas)
  val controlPanel = new MotionSeriesControlPanel(motionSeriesModel, fbdModel, coordinateSystemModel, vectorViewModel,
                                                  resetRampModule, coordinateSystemFeaturesEnabled, useObjectComboBox, motionSeriesModel, false, showFrictionControl, showBounceControl, "position.controls.title".translate, audioEnabled,

                                                  //No FBD on/off panel in the "Basics" sim
                                                  showFBDPanel = false,

                                                  //In the "Basics" application, gravity and normal forces aren't shown by default, but there is a control to allow the user to show them
                                                  showGravityNormalForceCheckBox = _showGravityNormalForceCheckBox)
  setControlPanel(controlPanel)
  setClockControlPanel(createRecordAndPlaybackPanel)

  def createRecordAndPlaybackPanel = new RecordAndPlaybackControlPanel(motionSeriesModel, canvas, 20)

  //In the "Basics" application, gravity and normal forces aren't shown by default, but there is a control to allow the user to show them
  override def gravityAndNormalForceShownByDefault = false
}

class ForcesAndMotionBasicsCanvas(model: MotionSeriesModel,
                                  coordinateSystemModel: AdjustableCoordinateModel,
                                  freeBodyDiagramModel: FreeBodyDiagramModel,
                                  vectorViewModel: VectorViewModel,
                                  frame: JFrame,
                                  showObjectSelectionNode: Boolean,
                                  showAppliedForceSlider: Boolean,
                                  rampAngleDraggable: Boolean,
                                  modelViewport: Rectangle2D,
                                  stageContainerArea: StageContainerArea)
        extends MotionSeriesCanvasDecorator(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel,
                                            frame, showObjectSelectionNode, showAppliedForceSlider, rampAngleDraggable, modelViewport, stageContainerArea) {
  override def addHeightAndAngleIndicators() {}

  override def createRightSegmentNode: HasPaint = new RampSegmentNode(model.rightRampSegment, transform, model, model.motionSeriesObject)

  def attachListenerToRightWall(node: PNode) {} //cannot drag the wall to rotate the ramp in this sim

  //Function used to generate display text for the MotionSeriesObjectType, usually shows HTML that includes mass and friction coefficients, but omits the friction coefficients in the Basics application
  override def motionSeriesObjectTypeToString(m: MotionSeriesObjectType): String = m.getDisplayText
}

class IntroModule(frame: PhetFrame) extends ForcesAndMotionBasicsModule(frame, "forces-and-motion.module.intro.title".translate, false, true, false,
                                                                        true, -6, 0.0, true, true, MotionSeriesDefaults.forceMotionViewport, MotionSeriesDefaults.forceMotionArea, false)

class FrictionModule(frame: PhetFrame) extends ForcesAndMotionBasicsModule(frame, "forces-and-motion.module.friction.title".translate,
                                                                           false, false, false, true, -6.0, 0.0, false, true, MotionSeriesDefaults.forceMotionFrictionViewport, MotionSeriesDefaults.forceMotionFrictionArea, false,
                                                                           _showGravityNormalForceCheckBox = true) {

  // so that it resizes
  motionSeriesModel.selectedObject = MotionSeriesDefaults.custom
  val frictionPlayAreaControlPanel = new PSwing(new BasicsFrictionPlayAreaControlPanel(motionSeriesModel.motionSeriesObject))
  frictionPlayAreaControlPanel.setOffset(canvas.stage.getWidth / 2 - frictionPlayAreaControlPanel.getFullBounds.getWidth / 2, canvas.stage.getHeight - frictionPlayAreaControlPanel.getFullBounds.getHeight - 2)
  canvas.addBehindVectorNodes(frictionPlayAreaControlPanel)
  motionSeriesModel.frictionless = false
}

class ForcesAndMotionBasicsApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new IntroModule(getPhetFrame))
  addModule(new FrictionModule(getPhetFrame))
  addModule(new RobotMovingCompanyModule(getPhetFrame, 1E-8, MotionSeriesDefaults.forcesAndMotionRobotForce, MotionSeriesDefaults.objectsForForce1DGame)) //todo: this 1E-8 workaround seems necessary to avoid problems, we should find out why
}

object ForcesAndMotionBasicsApplication {
  def main(args: Array[String]) {

    //In "Basics" mode, the static friction should always equal the kinetic friction, so enable that here
    Settings.basicsMode = true

    new PhetApplicationLauncher().launchSim(args, "motion-series".literal, "forces-and-motion-basics".literal, classOf[ForcesAndMotionBasicsApplication])
  }
}