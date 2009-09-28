package edu.colorado.phet.motionseries.sims.movingman

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.motionseries.model._
import java.awt.geom.Rectangle2D
import edu.colorado.phet.common.phetcommon.application.{PhetApplicationConfig, PhetApplicationLauncher}
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication
import edu.colorado.phet.motionseries.graphics._
import java.awt.Color
import edu.colorado.phet.scalacommon.record.{RecordModelControlPanel, PlaybackSpeedSlider}
import javax.swing.JFrame
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.sims.theramp.StageContainerArea
import edu.colorado.phet.motionseries.{MotionSeriesModule, MotionSeriesDefaults}
import edu.colorado.phet.motionseries.swing.ScalaValueControl
import edu.umd.cs.piccolox.pswing.PSwing
import edu.umd.cs.piccolo.util.PDebug

class BasicMovingManModule(frame: JFrame,
                           clock: ScalaClock,
                           name: String,
                           coordinateSystemFeaturesEnabled: Boolean,
                           showObjectSelectionNode: Boolean,
                           useObjectComboBox: Boolean, //todo: unused
                           showAppliedForceSlider: Boolean,
                           defaultBeadPosition: Double,
                           pausedOnReset: Boolean,
                           initialAngle: Double,
                           showFrictionControl: Boolean,
                           rampLayoutArea: Rectangle2D,stageContainerArea:StageContainerArea)
        extends MotionSeriesModule(frame, clock, name, defaultBeadPosition, pausedOnReset, initialAngle) {
	
	override def createMotionSeriesModel(defaultBeadPosition: Double, pausedOnReset: Boolean, initialAngle: Double) = 
		new MotionSeriesModel(defaultBeadPosition, pausedOnReset, initialAngle){
			override def thermalEnergyStrategy(x:Double) = 0.0
		}
	
  val canvas = new MovingManCanvas(motionSeriesModel, coordinateSystemModel, fbdModel, vectorViewModel, frame, showObjectSelectionNode, showAppliedForceSlider, initialAngle != 0.0, rampLayoutArea,stageContainerArea)
  setSimulationPanel(canvas)
  //  val controlPanel = new RampControlPanel(motionSeriesModel, wordModel, fbdModel, coordinateSystemModel, vectorViewModel,
  //    resetRampModule, coordinateSystemFeaturesEnabled, false, motionSeriesModel, false, showFrictionControl)
  //  setControlPanel(controlPanel)
  setClockControlPanel(new RecordModelControlPanel(motionSeriesModel, canvas, () => new PlaybackSpeedSlider(motionSeriesModel), Color.blue, 20))
  motionSeriesModel.selectedObject = MotionSeriesDefaults.movingMan
  vectorViewModel.xyComponentsVisible = false
  vectorViewModel.originalVectors = false
  vectorViewModel.parallelComponents = false
}

class MovingManCanvas(model: MotionSeriesModel, coordinateSystemModel: AdjustableCoordinateModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                      vectorViewModel: VectorViewModel, frame: JFrame, showObjectSelectionNode: Boolean, showAppliedForceSlider: Boolean,
                      rampAngleDraggable: Boolean, rampLayoutArea: Rectangle2D,stageContainerArea:StageContainerArea)
        extends MotionSeriesCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel,
          frame, rampLayoutArea,stageContainerArea) {
  override def addHeightAndAngleIndicators() = {}

  override def createRightSegmentNode  = new RampSegmentNode(model.rampSegments(1), transform, model)

  override def createBeadNode(b: Bead, t: ModelViewTransform2D, s: String, listener: () => Unit) = new PositionDragBeadNode(b, t, "moving-man/moving-man-standing.gif", "moving-man/moving-man-left.gif", listener, this)

  playAreaVectorNode.addVector(new PlayAreaVector(model.bead.velocityVector, MotionSeriesDefaults.PLAY_AREA_VELOCITY_VECTOR_SCALE),
    new PlayAreaOffset(model.bead, vectorViewModel, 0.5, model.bead.velocityVector))

  playAreaVectorNode.addVector(new PlayAreaVector(model.bead.accelerationVector, MotionSeriesDefaults.PLAY_AREA_ACCELERATION_VECTOR_SCALE),
    new PlayAreaOffset(model.bead, vectorViewModel, 3, model.bead.accelerationVector))
}

class IntroModule(frame: JFrame, clock: ScalaClock)
        extends BasicMovingManModule(frame, clock, "moving-man.module.intro.title".translate, false, false, false, false,
          -6, false, 0.0, true, MotionSeriesDefaults.movingManIntroViewport,MotionSeriesDefaults.fullScreenArea){
	
	val positionControl = new ScalaValueControl(-10,10,"position","0.0","m",()=>motionSeriesModel.bead.desiredPosition,
			x => motionSeriesModel.bead.setDesiredPosition(x),motionSeriesModel.addListener)
	canvas.addScreenNode(new PSwing(positionControl))
	
	val velocityControl = new ScalaValueControl(-20,20,"velocity","0.0","m/s",()=>motionSeriesModel.bead.velocity,
			v => motionSeriesModel.bead.setVelocity(v),motionSeriesModel.addListener)
	canvas.addScreenNode(new PSwing(velocityControl))
	
	val accelerationControl = new ScalaValueControl(-20,20,"acceleration","0.0","m/s/s",()=>motionSeriesModel.bead.acceleration,
			a => motionSeriesModel.bead.parallelAppliedForce = a ,motionSeriesModel.addListener)//todo: assumes mass = 1.0
	canvas.addScreenNode(new PSwing(accelerationControl))
	
}

class GraphingModule(frame: JFrame, clock: ScalaClock)
        extends BasicMovingManModule(frame, clock, "moving-man.module.graphing.title".translate, false, false, true, false,
          -6, false, 0.0, true, MotionSeriesDefaults.forceMotionViewport,MotionSeriesDefaults.fullScreenArea) {
  coordinateSystemModel.adjustable = false
  canvas.addScreenNode(new MovingManChartNode(canvas, motionSeriesModel))
}

class MovingManGameModule(frame: JFrame, clock: ScalaClock) extends BasicMovingManModule(frame, clock, "moving-man.module.game.title".translate, false, false, false, false, -6, false, 0.0, true, MotionSeriesDefaults.forceMotionViewport,MotionSeriesDefaults.fullScreenArea)

class MovingManApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  def newClock = new ScalaClock(MotionSeriesDefaults.DELAY, MotionSeriesDefaults.DT_DEFAULT)
  addModule(new IntroModule(getPhetFrame, newClock))
  addModule(new GraphingModule(getPhetFrame, newClock))
//  addModule(new MovingManGameModule(getPhetFrame, newClock))
}

object MovingManApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "motion-series".literal, "moving-man".literal, classOf[MovingManApplication])
}
