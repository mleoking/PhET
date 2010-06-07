package edu.colorado.phet.forcesandmotion

import edu.colorado.phet.common.phetcommon.view.PhetFrame
import java.awt.geom.Rectangle2D
import edu.colorado.phet.common.phetcommon.application.{PhetApplicationConfig, PhetApplicationLauncher}
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication
import edu.colorado.phet.motionseries.model.{MotionSeriesModel, AdjustableCoordinateModel, FreeBodyDiagramModel, VectorViewModel}
import edu.colorado.phet.motionseries.graphics._
import java.awt.Color
import javax.swing.JFrame
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.umd.cs.piccolox.pswing.PSwing
import edu.colorado.phet.motionseries.controls.RampControlPanel
import edu.colorado.phet.motionseries.{StageContainerArea, MotionSeriesModule, MotionSeriesDefaults}
import edu.colorado.phet.recordandplayback.gui.{RecordAndPlaybackControlPanel, PlaybackSpeedSlider}
import edu.colorado.phet.motionseries.sims.theramp.robotmovingcompany.RobotMovingCompanyModule

class ForcesAndMotionModule(frame: PhetFrame,
                            name: String,
                            coordinateSystemFeaturesEnabled: Boolean,
                            showObjectSelectionNode: Boolean,
                            useObjectComboBox: Boolean,
                            showAppliedForceSlider: Boolean,
                            defaultBeadPosition: Double,
                            pausedOnReset: Boolean,
                            initialAngle: Double,
                            showFrictionControl: Boolean,
                            showBounceControl: Boolean,
                            modelViewport: Rectangle2D,
                            stageContainerArea: StageContainerArea,
                            fbdPopupOnly: Boolean)
        extends MotionSeriesModule(frame, new ScalaClock(MotionSeriesDefaults.DELAY, MotionSeriesDefaults.DT_DEFAULT), name, defaultBeadPosition, pausedOnReset, initialAngle, fbdPopupOnly) {
  val canvas = new ForcesAndMotionCanvas(motionSeriesModel, coordinateSystemModel, fbdModel, vectorViewModel, frame,
    showObjectSelectionNode, showAppliedForceSlider, initialAngle != 0.0, modelViewport, stageContainerArea)
  setSimulationPanel(canvas)
  val controlPanel = new RampControlPanel(motionSeriesModel, fbdModel, coordinateSystemModel, vectorViewModel,
    resetRampModule, coordinateSystemFeaturesEnabled, useObjectComboBox, motionSeriesModel, false, showFrictionControl, showBounceControl)
  setControlPanel(controlPanel)
  setClockControlPanel(new RecordAndPlaybackControlPanel(motionSeriesModel, canvas, 20))
}

class ForcesAndMotionCanvas(model: MotionSeriesModel,
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
  override def addHeightAndAngleIndicators() = {}

  override def createRightSegmentNode: HasPaint = new RampSegmentNode(model.rampSegments(1), transform, model)
}

class IntroModule(frame: PhetFrame) extends ForcesAndMotionModule(frame, "forces-and-motion.module.intro.title".translate, false, true, false, true, -3, false, 0.0, true, true, MotionSeriesDefaults.forceMotionViewport, MotionSeriesDefaults.forceMotionArea, false)

class FrictionModule(frame: PhetFrame)
        extends ForcesAndMotionModule(frame, "forces-and-motion.module.friction.title".translate,
          false, false, false, true, -6, false, 0.0, false, true, MotionSeriesDefaults.forceMotionFrictionViewport, MotionSeriesDefaults.forceMotionFrictionArea, false) {
  motionSeriesModel.selectedObject = MotionSeriesDefaults.custom // so that it resizes
  val frictionPlayAreaControlPanel = new PSwing(new FrictionPlayAreaControlPanel(motionSeriesModel.bead))
  frictionPlayAreaControlPanel.setOffset(canvas.stage.getWidth / 2 - frictionPlayAreaControlPanel.getFullBounds.getWidth / 2, canvas.stage.getHeight - frictionPlayAreaControlPanel.getFullBounds.getHeight - 2)
  canvas.addBehindVectorNodes(frictionPlayAreaControlPanel)
  motionSeriesModel.frictionless = false
}

class GraphingModule(frame: PhetFrame)
        extends ForcesAndMotionModule(frame, "forces-and-motion.module.graphing.title".translate,
          false, false, true, false, -6, false, 0.0, true, true, MotionSeriesDefaults.forceMotionGraphViewport, MotionSeriesDefaults.forceEnergyGraphArea, true) {
  coordinateSystemModel.adjustable = false
  canvas.addScreenNode(new ForcesAndMotionChartNode(canvas, motionSeriesModel))
}

class ForcesAndMotionApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new IntroModule(getPhetFrame))
  addModule(new FrictionModule(getPhetFrame))
  addModule(new GraphingModule(getPhetFrame))
  addModule(new RobotMovingCompanyModule(getPhetFrame, 1E-8, MotionSeriesDefaults.forcesAndMotionRobotForce, MotionSeriesDefaults.objectsForForce1DGame)) //todo: this 1E-8 workaround seems necessary to avoid problems, we should find out why
}

object ForcesAndMotionApplication{
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "motion-series".literal, "forces-and-motion".literal, classOf[ForcesAndMotionApplication])
}
