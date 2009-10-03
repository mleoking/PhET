package edu.colorado.phet.motionseries.sims.theramp.robotmovingcompany

import common.phetcommon.view.util.PhetFont
import common.piccolophet.nodes.{ArrowNode, HTMLNode, PhetPPath}
import edu.colorado.phet.common.phetcommon.view.PhetFrame
import edu.colorado.phet.motionseries.model.MotionSeriesModel
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.motionseries.Predef._
import edu.colorado.phet.motionseries.{MotionSeriesResources, MotionSeriesDefaults, MotionSeriesModule}
import java.awt.event.{KeyEvent, KeyAdapter}
import java.awt.geom.Point2D
import java.awt.{BasicStroke, Rectangle, Color}
import javax.swing.SwingUtilities
import umd.cs.piccolo.nodes.{PText, PImage}
import umd.cs.piccolo.PNode

class RobotMovingCompanyModule(frame: PhetFrame,
                               clock: ScalaClock,
                               initAngle: Double)
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

  val gameModel = new RobotMovingCompanyGameModel(motionSeriesModel, clock, initAngle)

  gameModel.itemFinishedListeners += ((scalaRampObject, result) => {
    val audioClip = result match {
      case Result(_, true, _, _) => Some("smash0.wav".literal)
      case Result(true, false, _, _) => Some("tintagel/DIAMOND.WAV".literal)
      case Result(false, false, _, _) => Some("tintagel/PERSONAL.WAV".literal)
      case _ => None
    }
    if (!audioClip.isEmpty) MotionSeriesResources.getAudioClip(audioClip.get).play()
  })

  val canvas = new RobotMovingCompanyCanvas(motionSeriesModel, coordinateSystemModel, fbdModel, vectorViewModel, frame, gameModel, MotionSeriesDefaults.robotMovingCompanyRampArea)

  setSimulationPanel(canvas)
  setLogoPanelVisible(false)

  var inited = false

  override def activate = {
    super.activate()
    SwingUtilities.invokeLater(new Runnable{
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
  val titleNode = new HTMLNode("<html>Robot<br>Moving<br>Company</html>", new PhetFont(52, true), Color.blue) //todo: translate
  titleNode.setOffset(getFullBounds.getWidth / 2 - titleNode.getFullBounds.getWidth / 2, 20)
  addChild(titleNode)

  def directionKeyNode(dx: Double, dy: Double) = {
    new PImage(MotionSeriesResources.getImage("robotmovingcompany/empty-key.png")) {
      val b = getFullBounds
      val y1 = b.getCenterY + b.getHeight * dy
      val y2 = b.getCenterY - b.getHeight * dy
      val x1 = b.getCenterX + b.getWidth * dx
      val x2 = b.getCenterX - b.getWidth * dx
      val node = new ArrowNode(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), 20, 20, 10) {
        setPaint(Color.black)
        setStroke(new BasicStroke(2))
        setStrokePaint(Color.gray)
      }
      scale(0.8)
      addChild(node)
    }
  }

  val leftKey = directionKeyNode(1 / 5.0, 0)
  val rightKey = directionKeyNode(-1 / 5.0, 0)

  val text = new PText("Apply Force\nDeliver Objects\nLeftover Energy = Points") {
    setFont(new PhetFont(25, true))
  }

  val buttonCluster = new PNode {
    addChild(leftKey)
    addChild(rightKey)
    rightKey.setOffset(leftKey.getFullBounds.getMaxX + 10, leftKey.getFullBounds.getY)
  }
  val labeledButtonCluster = new PNode {
    addChild(text)
    addChild(buttonCluster)
    buttonCluster.setOffset(text.getFullBounds.getWidth / 2 - buttonCluster.getFullBounds.getWidth / 2, text.getFullBounds.getHeight + 5)
  }
  addChild(labeledButtonCluster)

  val pressToBegin = new PText("Apply a force to begin")
  addChild(pressToBegin)
  pressToBegin.setOffset(background.getFullBounds.getWidth / 2 - pressToBegin.getFullBounds.getWidth / 2, background.getFullBounds.getHeight - pressToBegin.getFullBounds.getHeight - 10)

  labeledButtonCluster.setOffset(background.getFullBounds.getWidth / 2 - labeledButtonCluster.getFullBounds.getWidth / 2, pressToBegin.getFullBounds.getY - labeledButtonCluster.getFullBounds.getHeight - 10)
}