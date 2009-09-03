package edu.colorado.phet.motionseries.sims.theramp.robotmovingcompany

import javax.swing.JFrame
import model.MotionSeriesModel
import scalacommon.ScalaClock
import motionseries.Predef._

class RobotMovingCompanyModule(frame: JFrame, clock: ScalaClock)
        extends MotionSeriesModule(frame, clock, "module.robotMovingCompany".translate, 5, false, MotionSeriesDefaults.defaultRampAngle) {
  override def reset() = {
    super.reset()
    rampModel.frictionless = false
  }

  override def resetAll() = {
    super.resetAll()
    rampModel.frictionless = false
  }

  override def createRampModel(defaultBeadPosition: Double, pausedOnReset: Boolean, initialAngle: Double) = {
    new MotionSeriesModel(defaultBeadPosition, pausedOnReset, initialAngle) {
      override def updateSegmentLengths() = setSegmentLengths(rampLength, rampLength)
      frictionless = false
    }

  }

  val gameModel = new RobotMovingCompanyGameModel(rampModel, clock)

  gameModel.itemFinishedListeners += ((scalaRampObject, result) => {
    val audioClip = result match {
      case Result(_, true, _, _) => Some("smash0.wav".literal)
      case Result(true, false, _, _) => Some("tintagel/DIAMOND.WAV".literal)
      case Result(false, false, _, _) => Some("tintagel/PERSONAL.WAV".literal)
      case _ => None
    }
    if (!audioClip.isEmpty) MotionSeriesResources.getAudioClip(audioClip.get).play()
  })

  val canvas = new RobotMovingCompanyCanvas(rampModel, coordinateSystemModel, fbdModel, vectorViewModel, frame, gameModel)

  setSimulationPanel(canvas)
  setLogoPanelVisible(false)
}