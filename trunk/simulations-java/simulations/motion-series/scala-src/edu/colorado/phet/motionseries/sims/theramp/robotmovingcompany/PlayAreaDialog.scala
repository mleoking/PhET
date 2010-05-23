package edu.colorado.phet.motionseries.sims.theramp.robotmovingcompany

import edu.colorado.phet.common.piccolophet.nodes.{HTMLNode}
import java.awt.{Color}
import edu.colorado.phet.common.phetcommon.view.util.{BufferedImageUtils, PhetFont}
import edu.colorado.phet.common.piccolophet.nodes.layout.SwingLayoutNode
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt._
import edu.colorado.phet.scalacommon.ScalaClock
import edu.umd.cs.piccolo.nodes.{PImage, PText}
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import edu.colorado.phet.motionseries.model._
import edu.umd.cs.piccolox.pswing.PSwing
import edu.colorado.phet.motionseries.MotionSeriesResources._
import geom.{Line2D, RoundRectangle2D}
import javax.swing._
import edu.colorado.phet.motionseries.{MotionSeriesDefaults, MotionSeriesResources}

/**
 * The PlayAreaDialog is a piccolo node that looks like a window in the play area, used for instructions
 * and summarizing game results.
 * @author Sam Reid
 */
class PlayAreaDialog(width: Double, height: Double) extends PNode {
  val background = new PhetPPath(new RoundRectangle2D.Double(0, 0, width, height, 20, 20), MotionSeriesDefaults.dialogBackground, new BasicStroke(2), MotionSeriesDefaults.dialogBorder)
  addChild(background)

  def centerWithin(w: Double, h: Double) = setOffset(w / 2 - getFullBounds.width / 2, h / 2 - getFullBounds.height / 2)
}

/**
 * The IntroScreen is a welcome and instruction screen that introduces the Robot Moving Company game.
 * @author Sam Reid
 */
class IntroScreen extends PlayAreaDialog(400, 500) {
  val titleNode = new HTMLNode("game.intro.robot-moving-company".translate, Color.blue, new PhetFont(52, true)) //todo: translate
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

class SummaryScreenNode(gameModel: RobotMovingCompanyGameModel,
                        scalaRampObject: MotionSeriesObject,
                        result: Result,
                        okPressed: SummaryScreenNode => Unit,
                        okButtonText: String) extends PlayAreaDialog(400, 400) {
  val text = result match {
    case Result(_, true, _, _) => "game.result.crashed".translate
    case Result(true, false, _, _) => "game.result.delivered-successfully".translate
    case Result(false, false, _, _) => "game.result.missed-the-house".translate
    case _ => "Disappeared?".literal //should never happen
  }
  val pText = new PText("game.result.description.pattern.name-text".messageformat(scalaRampObject.name, text))
  pText.setFont(new PhetFont(22, true))
  addChild(pText)
  pText.setOffset(background.getFullBounds.getCenterX - pText.getFullBounds.width / 2, 20)

  val image = new PImage(BufferedImageUtils.rescaleYMaintainAspectRatio(MotionSeriesResources.getImage(scalaRampObject.imageFilename), 150))
  image.setOffset(background.getFullBounds.getCenterX - image.getFullBounds.width / 2, pText.getFullBounds.getMaxY + 20)
  addChild(image)

  val doneButton = new JButton(okButtonText)
  val donePSwing = new PSwing(doneButton)
  donePSwing.setOffset(background.getFullBounds.getCenterX - donePSwing.getFullBounds.width / 2, background.getFullBounds.getMaxY - donePSwing.getFullBounds.height - 20)
  doneButton.addActionListener(() => {okPressed(this)})

  val layoutNode = new SwingLayoutNode(new GridBagLayout)

  def constraints(gridX: Int, gridY: Int, gridWidth: Int) = new GridBagConstraints(gridX, gridY, gridWidth, 1, 0.5, 0.5, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2)
  class SummaryText(text: String) extends PText(text) {
    setFont(new PhetFont(14, true))
  }

  layoutNode.addChild(new SummaryText("game.summary.pattern.points-multiplier".messageformat(result.objectPoints, result.scoreMultiplier)), constraints(0, 0, 2))
  layoutNode.addChild(new SummaryText("game.summary.pattern.energy-points".messageformat(result.robotEnergy, result.pointsPerJoule)), constraints(0, 1, 2))
  layoutNode.addChild(new SummaryText("game.summary.pattern.earned-object-points".messageformat(result.totalObjectPoints)), constraints(2, 0, 1))
  layoutNode.addChild(new SummaryText("game.summary.pattern.earned-energy-points".messageformat(result.totalEnergyPoints)), constraints(2, 1, 1))
  layoutNode.addChild(new PhetPPath(new Line2D.Double(0, 0, 100, 0), new BasicStroke(2), Color.black), constraints(2, 2, 1))
  layoutNode.addChild(new SummaryText("game.summary.total".translate), constraints(0, 3, 2))
  layoutNode.addChild(new SummaryText("game.summary.pattern.result-score".messageformat(result.score)), constraints(2, 3, 1))
  layoutNode.setOffset(background.getFullBounds.getCenterX - layoutNode.getFullBounds.width / 2, image.getFullBounds.getMaxY + 10)
  addChild(layoutNode)

  addChild(donePSwing)

  def requestFocus() = doneButton.requestFocus()
}

object TestSummaryScreen {
  def main(args: Array[String]) {
    val robotMovingCompanyGameModel = new RobotMovingCompanyGameModel(new MotionSeriesModel(5, true, MotionSeriesDefaults.defaultRampAngle), new ScalaClock(30, 30 / 1000.0), MotionSeriesDefaults.defaultRampAngle, 500.0, MotionSeriesDefaults.objects)
    val summaryScreenNode = new SummaryScreenNode(robotMovingCompanyGameModel,
      MotionSeriesDefaults.objects(0), new Result(true, false, 64, 100), a => {
        a.setVisible(false)
      }, "Ok".literal)
    val frame = new JFrame
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setSize(800, 600)
    val canvas = new PhetPCanvas()
    canvas.addScreenChild(summaryScreenNode)
    frame.setContentPane(canvas)
    frame.setVisible(true)
  }
}