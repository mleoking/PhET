package edu.colorado.phet.motionseries.sims.theramp.robotmovingcompany

import edu.colorado.phet.common.piccolophet.nodes.{PhetPPath}
import edu.colorado.phet.common.phetcommon.view.PhetFrame
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.Predef._
import edu.colorado.phet.motionseries.{MotionSeriesResources, MotionSeriesDefaults, MotionSeriesModule}
import java.awt.{Rectangle, Color}
import edu.colorado.phet.motionseries.model.{MotionSeriesObject, MotionSeriesModel}
import MotionSeriesDefaults._
import javax.swing.{Timer, SwingUtilities}
import java.awt.event.{ActionEvent, ActionListener, KeyEvent, KeyAdapter}

/**
 * The RobotMovingCompanyModule is the main game mode for the Ramp II and Forces and Motion simulations.
 * @author Sam Reid
 */
class RobotMovingCompanyModule(frame: PhetFrame,
                               initAngle: Double = defaultRampAngle,
                               appliedForce: Double = rampRobotForce,
                               objectList: List[MotionSeriesObject] = objects)
        extends MotionSeriesModule(frame, new ScalaClock(MotionSeriesDefaults.DELAY, MotionSeriesDefaults.DT_DEFAULT), "module.robotMovingCompany".translate, 5, false, MotionSeriesDefaults.defaultRampAngle, false) {
  override def reset() = {
    super.reset()
    motionSeriesModel.frictionless = false
  }

  override def resetAll() = {
    super.resetAll()
    motionSeriesModel.frictionless = false
  }

  override def createMotionSeriesModel(defaultBeadPosition: Double, pausedOnReset: Boolean, initialAngle: Double) = {
    new MotionSeriesModel(defaultBeadPosition, pausedOnReset, initialAngle) {
      override def updateSegmentLengths() = setSegmentLengths(rampLength, rampLength)
      frictionless = false
      //This is a highly unorthodox way to achieve the desired behavior.  The requested feature is that objects should not be able to be moved 
      //beyond the left edge of the leftmost ramp segment in the game modes.
      //This solves the problem by enabling walls whenever the bead is to the left of the origin.
      //A better designed way would be to always have the left wall enabled.
      stepListeners += (() => walls = gameModel.bead.position < 0)
    }
  }

  val gameModel = new RobotMovingCompanyGameModel(motionSeriesModel, getClock.asInstanceOf[ScalaClock], initAngle, appliedForce, objectList) //todo: fix cast

  gameModel.itemFinishedListeners += ((scalaRampObject, result) => {
    val audioClip = result match {
      case Result(_, true, _, _) => Some("smash0.wav".literal)
      case Result(true, false, _, _) => Some("tintagel/DIAMOND.WAV".literal)
      case Result(false, false, _, _) => Some("tintagel/PERSONAL.WAV".literal)
      case _ => None
    }
    if (!audioClip.isEmpty) MotionSeriesResources.getAudioClip(audioClip.get).play()
  })

  val canvas = new RobotMovingCompanyCanvas(motionSeriesModel, coordinateSystemModel, fbdModel,
    vectorViewModel, frame, gameModel, MotionSeriesDefaults.robotMovingCompanyRampArea, gameModel.energyScale)

  setSimulationPanel(canvas)
  setClockControlPanel(null)
  setLogoPanelVisible(false)

  var inited = false

  override def activate = {
    super.activate()
    SwingUtilities.invokeLater(new Runnable {
      def run = canvas.requestFocus()
    })
    if (!inited) {
      inited = true
      inSwingThread {
        //gray out the play area to help focus on the instruction panel
        val overlayNode = new PhetPPath(new Rectangle(canvas.getWidth, canvas.getHeight), new Color(100, 120, 100, 240))
        canvas.addScreenNode(overlayNode)

        MotionSeriesResources.getAudioClip("game/robot-moving-company-us8mon.wav".literal).play() //Play startup sound "Robot moving company"
        val intro = new IntroDialog {
          centerWithin(canvas.getWidth, canvas.getHeight)
        }
        canvas.addScreenNode(intro)

        canvas.addKeyListener(new KeyAdapter {
          override def keyPressed(e: KeyEvent) = {
            canvas.removeKeyListener(this)
            canvas.removeScreenNode(intro)
            canvas.removeScreenNode(overlayNode)
            val wiggleMeTimer = new Timer(5000, new ActionListener {
              def actionPerformed(e: ActionEvent) = {
                //no keys pressed=> no energy used, remind the user they should use keyboard
                //but the intro screen keypress may have leaked over, so use a heuristic 92%
                if (!canvas.hasUserAppliedForce)
                  canvas.showWiggleMe()
              }
            }) {
              setRepeats(false)
              start()
            }
          }
        })
      }
    }
  }
}