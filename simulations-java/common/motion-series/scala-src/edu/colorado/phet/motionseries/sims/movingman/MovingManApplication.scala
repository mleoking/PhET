package edu.colorado.phet.motionseries.sims.movingman

import edu.colorado.phet.common.phetcommon.view.PhetFrame
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.motionseries.model._
import java.awt.geom.Rectangle2D
import edu.colorado.phet.common.phetcommon.application.{PhetApplicationConfig, PhetApplicationLauncher}
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication
import edu.colorado.phet.motionseries.graphics._
import java.awt.Color
import javax.swing.event.{ChangeListener, ChangeEvent}
import javax.swing.JFrame
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.swing.ScalaValueControl
import edu.umd.cs.piccolox.pswing.PSwing
import java.lang.String
import edu.colorado.phet.motionseries.{StageContainerArea, MotionSeriesModule, MotionSeriesDefaults}
import edu.colorado.phet.recordandplayback.gui.{RecordAndPlaybackControlPanel, PlaybackSpeedSlider}

class BasicMovingManModule(frame: PhetFrame,
                           clock: ScalaClock,
                           name: String,
                           defaultBeadPosition: Double,
                           pausedOnReset: Boolean,
                           modelViewport: Rectangle2D,
                           stageContainerArea: StageContainerArea)
        extends MotionSeriesModule(frame, clock, name, defaultBeadPosition, pausedOnReset, 0.0, false) {
  override def createMotionSeriesModel(defaultBeadPosition: Double, pausedOnReset: Boolean, initialAngle: Double) =
    new MotionSeriesModel(defaultBeadPosition, pausedOnReset, initialAngle) {
      override def thermalEnergyStrategy(x: Double) = 0.0
    }

  val canvas = new MovingManCanvas(motionSeriesModel, coordinateSystemModel, fbdModel, vectorViewModel, frame,
    false, false, false, modelViewport, stageContainerArea)
  setSimulationPanel(canvas)
  setClockControlPanel(new RecordAndPlaybackControlPanel(motionSeriesModel, canvas, 20))
  motionSeriesModel.selectedObject = MotionSeriesDefaults.movingMan
  vectorViewModel.xyComponentsVisible = false
  vectorViewModel.originalVectors = false
  vectorViewModel.parallelComponents = false
}

class MovingManCanvas(model: MotionSeriesModel,
                      coordinateSystemModel: AdjustableCoordinateModel,
                      freeBodyDiagramModel: FreeBodyDiagramModel,
                      vectorViewModel: VectorViewModel,
                      frame: JFrame,
                      showObjectSelectionNode: Boolean,
                      showAppliedForceSlider: Boolean,
                      rampAngleDraggable: Boolean,
                      modelViewport: Rectangle2D,
                      stageContainerArea: StageContainerArea)
        extends MotionSeriesCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel, frame, modelViewport, stageContainerArea) {
  override def getContainerBounds = stageContainerArea.getBounds(getWidth, getHeight)

  override def addHeightAndAngleIndicators() = {}

  override def createRightSegmentNode = new RampSegmentNode(model.rampSegments(1), transform, model)

  override def createBeadNode(b: MovingManBead, t: ModelViewTransform2D, imageName: String, crashImageName:String,listener: () => Unit) =
    new PositionDragBeadNode(b, t, "moving-man/moving-man-standing.gif".literal, "moving-man/moving-man-left.gif".literal, listener, this)

  playAreaVectorNode.addVector(new PlayAreaVector(model.bead.velocityVector, MotionSeriesDefaults.PLAY_AREA_VELOCITY_VECTOR_SCALE),
    new PlayAreaOffset(model.bead, vectorViewModel, 0.5, model.bead.velocityVector))

  playAreaVectorNode.addVector(new PlayAreaVector(model.bead.accelerationVector, MotionSeriesDefaults.PLAY_AREA_ACCELERATION_VECTOR_SCALE),
    new PlayAreaOffset(model.bead, vectorViewModel, 3, model.bead.accelerationVector))
}

class IntroModule(frame: PhetFrame,
                  clock: ScalaClock)
        extends BasicMovingManModule(frame, clock, "moving-man.module.intro.title".translate,
          -6, false, MotionSeriesDefaults.movingManIntroViewport, MotionSeriesDefaults.oneGraphArea) {
  val positionControl = new ScalaValueControl(-10, 10, "position", "0.0", "m", () => motionSeriesModel.bead.desiredPosition,//todo: il8n
    x => {
      motionSeriesModel.setPaused(false)
      motionSeriesModel.bead.setPositionMode()
      motionSeriesModel.bead.setDesiredPosition(x)
    }, motionSeriesModel.addListener)
  val positionControlPSwing = new PSwing(positionControl)
  canvas.addStageNode(positionControlPSwing)

  //todo: need to distinguish between user-setting a value to velocity vs. velocity change originiating from the model
  //todo: could factor out focuscontrol, passes Double,Boolean for value,focus
  val velocityControl = new ScalaValueControl(-20, 20, "velocity", "0.0", "m/s", () => motionSeriesModel.bead.velocity,//todo: il8n
    v => {
      //      motionSeriesModel.bead.setVelocityMode()
      //      motionSeriesModel.bead.setVelocity(v)
    }, motionSeriesModel.addListener)
  velocityControl.addChangeListener(new ChangeListener {
    def stateChanged(e: ChangeEvent) = {
      if (velocityControl.getSlider.hasFocus) {
        motionSeriesModel.bead.setVelocityMode()
        motionSeriesModel.bead.setVelocity(velocityControl.getValue)
      }
    }
  })
  val velocityControlPSwing = new PSwing(velocityControl)
  canvas.addStageNode(velocityControlPSwing)

  val accelerationControl = new ScalaValueControl(-20, 20, "acceleration", "0.0", "m/s/s", () => motionSeriesModel.bead.acceleration,//todo: il8n
    a => {motionSeriesModel.bead.setAccelerationMode(); motionSeriesModel.bead.parallelAppliedForce = a}, motionSeriesModel.addListener) //todo: assumes mass = 1.0
  val accelerationControlPSwing = new PSwing(accelerationControl)
  canvas.addStageNode(accelerationControlPSwing)

  val origin = canvas.transform.modelToView(0, -1)
  val padY = 3
  positionControlPSwing.setOffset(origin.getX - positionControlPSwing.getFullBounds.getWidth / 2, origin.getY)
  velocityControlPSwing.setOffset(origin.getX - velocityControlPSwing.getFullBounds.getWidth / 2, positionControlPSwing.getFullBounds.getMaxY + padY)
  accelerationControlPSwing.setOffset(origin.getX - accelerationControlPSwing.getFullBounds.getWidth / 2, velocityControlPSwing.getFullBounds.getMaxY + padY)
}

class GraphingModule(frame: PhetFrame,
                     clock: ScalaClock)
        extends BasicMovingManModule(frame, clock, "moving-man.module.graphing.title".translate,
          -6, false, MotionSeriesDefaults.movingManIntroViewport, MotionSeriesDefaults.oneGraphArea) {
  coordinateSystemModel.adjustable = false
  canvas.addScreenNode(new MovingManChartNode(canvas, motionSeriesModel))
}

class MovingManGameModule(frame: PhetFrame,
                          clock: ScalaClock)
        extends BasicMovingManModule(frame, clock, "moving-man.module.game.title".translate,
          -6, false, MotionSeriesDefaults.forceMotionViewport, MotionSeriesDefaults.fullScreenArea)

class MovingManApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  def newClock = new ScalaClock(MotionSeriesDefaults.DELAY, MotionSeriesDefaults.DT_DEFAULT)
  addModule(new IntroModule(getPhetFrame, newClock))
  addModule(new GraphingModule(getPhetFrame, newClock))
  //  addModule(new MovingManGameModule(getPhetFrame, newClock))
}

object MovingManApplication {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "motion-series".literal, "moving-man".literal, classOf[MovingManApplication])
}
