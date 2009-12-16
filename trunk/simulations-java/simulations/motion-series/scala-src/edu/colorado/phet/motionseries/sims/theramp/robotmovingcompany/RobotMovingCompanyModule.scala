package edu.colorado.phet.motionseries.sims.theramp.robotmovingcompany

import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.piccolophet.nodes.{ArrowNode, HTMLNode, PhetPPath}
import edu.colorado.phet.common.phetcommon.view.PhetFrame
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.Predef._
import edu.colorado.phet.motionseries.{MotionSeriesResources, MotionSeriesDefaults, MotionSeriesModule}
import java.awt.event.{KeyEvent, KeyAdapter}
import java.awt.geom.Point2D
import java.awt.{BasicStroke, Rectangle, Color}
import javax.swing.{SwingUtilities}
import edu.umd.cs.piccolo.nodes.{PText, PImage}
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.motionseries.model.{MotionSeriesObject, MotionSeriesModel}

class RobotMovingCompanyModule(frame: PhetFrame,
                               clock: ScalaClock,
                               initAngle: Double,
                               appliedForce: Double,
                               objectList: List[MotionSeriesObject])
        extends MotionSeriesModule(frame, clock, "module.robotMovingCompany".translate, 5, false, MotionSeriesDefaults.defaultRampAngle, false) {
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
    }
  }

  val gameModel = new RobotMovingCompanyGameModel(motionSeriesModel, clock, initAngle, appliedForce, objectList)

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
      SwingUtilities.invokeLater(new Runnable {
        def run = {

          var removed = false
          //gray out everything else
          val overlayNode = new PhetPPath(new Rectangle(canvas.getWidth, canvas.getHeight), new Color(100, 120, 100, 240))
          canvas.addScreenNode(overlayNode)

          MotionSeriesResources.getAudioClip("game/robot-moving-company-us8mon.wav".literal).play()
          val intro = new IntroScreen()
          intro.centerWithin(canvas.getWidth, canvas.getHeight)
          canvas.addScreenNode(intro)

          canvas.addKeyListener(new KeyAdapter {
            override def keyPressed(e: KeyEvent) = {
              if (!removed) {
                canvas.removeScreenNode(intro)
                canvas.removeScreenNode(overlayNode)
                removed = true
              }
            }
          })
        }
      })
    }
  }
}

class IntroScreen extends PlayAreaDialog(400, 500) {
  val titleNode = new HTMLNode("game.intro.robot-moving-company".translate, new PhetFont(52, true), Color.blue) //todo: translate
  titleNode.setOffset(getFullBounds.getWidth / 2 - titleNode.getFullBounds.getWidth / 2, 20)
  addChild(titleNode)

  val text = new PNode {
    val mottoBorder = new PText("game.intro.our-motto".translate)

    addChild(mottoBorder)
    val mottoBody = new HTMLNode("game.intro.motto-text".translate) {
      setFont(new PhetFont(25, true))
    }
    addChild(mottoBody)
    mottoBody.setOffset(0, mottoBorder.getFullBounds.getHeight)
  }
  val buttonCluster = new KeyboardButtonIcons

  val labeledButtonCluster = new PNode {
    addChild(text)
    addChild(buttonCluster)
    buttonCluster.setOffset(text.getFullBounds.getWidth / 2 - buttonCluster.getFullBounds.getWidth / 2, text.getFullBounds.getHeight + 5)
  }
  addChild(labeledButtonCluster)

  val pressToBegin = new PText("game.intro.press-to-begin".translate)
  addChild(pressToBegin)
  pressToBegin.setOffset(background.getFullBounds.getWidth / 2 - pressToBegin.getFullBounds.getWidth / 2, background.getFullBounds.getHeight - pressToBegin.getFullBounds.getHeight - 10)

  labeledButtonCluster.setOffset(background.getFullBounds.getWidth / 2 - labeledButtonCluster.getFullBounds.getWidth / 2, pressToBegin.getFullBounds.getY - labeledButtonCluster.getFullBounds.getHeight - 10)
}