package edu.colorado.phet.therampscala.robotmovingcompany

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.{BufferedImageUtils, PhetFont}
import common.piccolophet.nodes.layout.SwingLayoutNode
import common.piccolophet.PhetPCanvas
import java.awt._
import geom.{Line2D, RoundRectangle2D}
import javax.swing.event.{ChangeEvent, ChangeListener}

import javax.swing.{JButton, JOptionPane, JFrame}
import scalacommon.ScalaClock
import common.phetcommon.view.VerticalLayoutPanel
import umd.cs.piccolo.nodes.{PImage, PText}
import umd.cs.piccolo.PNode
import common.piccolophet.nodes.PhetPPath
import scalacommon.math.Vector2D
import graphics._
import model._
import swing.{ScalaButton}
import umd.cs.piccolox.pswing.PSwing
import scalacommon.Predef._
import RampResources._

class RobotMovingCompanyCanvas(model: RampModel, coordinateSystemModel: CoordinateSystemModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                               vectorViewModel: VectorViewModel, frame: JFrame, gameModel: RobotMovingCompanyGameModel)
        extends AbstractRampCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel, frame) {
  beadNode.setVisible(false)
  vectorNode.setVisible(false)
  pusherNode.setVisible(false)

  val controlPanel = new VerticalLayoutPanel
  controlPanel.setFillNone()
  val robotGoButton = new ScalaButton("Let Go", () => {
    gameModel.launched = true
    model.bead.parallelAppliedForce = 0
    model.setPaused(false)
  })
  gameModel.addListener(() => {robotGoButton.setEnabled(!gameModel.launched)})

  val appliedForceControl = new AppliedForceSlider(() => 0, value => 0, gameModel.addListener) //todo: last param is a dummy
  appliedForceControl.addChangeListener(new ChangeListener() {
    def stateChanged(e: ChangeEvent) = {
      gameModel.launched = true
      model.setPaused(false)
    }
  })
  controlPanel.add(appliedForceControl)
  controlPanel.add(robotGoButton)

  def showGameSummary() = {
    JOptionPane.showMessageDialog(RobotMovingCompanyCanvas.this, "That was the last object to move.  \nYour score is: " + gameModel.score + ".")
    gameModel.resetAll()
  }

  gameModel.itemFinishedListeners += ((scalaRampObject: ScalaRampObject, result: Result) => {
    val summaryScreen = new SummaryScreenNode(gameModel, scalaRampObject, result, node => {
      removeNode(node)
      if (gameModel.isLastObject(scalaRampObject))
        showGameSummary()
      else
        gameModel.nextObject()
    }, if (gameModel.isLastObject(scalaRampObject)) "Show Summary" else "Ok")
    summaryScreen.setOffset(canonicalBounds.getCenterX - summaryScreen.getFullBounds.width / 2,
      canonicalBounds.getCenterY - summaryScreen.getFullBounds.height / 2)
    addNode(summaryScreen)
  })

  val pswingControlPanel = new PSwing(controlPanel)
  addNode(pswingControlPanel)

  pswingControlPanel.setOffset(0, transform.modelToView(0, -1).y)
  fbdNode.setOffset(pswingControlPanel.getFullBounds.getMaxX + 10, pswingControlPanel.getFullBounds.getY)
  freeBodyDiagramModel.visible = true
  freeBodyDiagramModel.closable = false

  val surfaceChooser = new SurfaceChooser(gameModel.surfaceModel)
  surfaceChooser.setOffset(fbdNode.getFullBounds.getMaxX + 10, fbdNode.getFullBounds.getY)
  addNode(surfaceChooser)

  addNode(new BeadNode(gameModel.house, transform, RampDefaults.house.imageFilename))

  val scoreboard = new ScoreboardNode(transform, gameModel)
  addNode(scoreboard)

  val energyMeter = new RobotEnergyMeter(transform, gameModel)
  energyMeter.setOffset(scoreboard.getFullBounds.getX + 5, scoreboard.getFullBounds.getMaxY + 5)
  addNode(energyMeter)

  val robotGraphics = new RobotGraphics(transform, gameModel)
  addNode(2, robotGraphics) //behind ramp

  gameModel.beadCreatedListeners += init
  init(gameModel.bead, gameModel.selectedObject)

  gameModel.surfaceModel.addListener(updateRampColor)
  def updateRampColor() = {
    rightSegmentNode.paintColor = gameModel.surfaceModel.surfaceType.color
    leftSegmentNode.paintColor = gameModel.surfaceModel.surfaceType.color
  }
  updateRampColor()

  private var _currentBead: Bead = null

  override def useVectorNodeInPlayArea = false

  def init(bead: Bead, a: ScalaRampObject) = {
    val lastBead = _currentBead
    _currentBead = bead

    val beadNode = new DraggableBeadNode(bead, transform, a.imageFilename)
    addNode(beadNode)

    val roboBead = model.createBead(-10 - a.width / 2, 1)

    val pusherNode = new RobotPusherNode(transform, bead, roboBead)
    addNode(pusherNode)

    bead.removalListeners += (() => {
      removeNode(beadNode)
      removeNode(pusherNode)
    })

    fbdNode.clearVectors()
    windowFBDNode.clearVectors()

    val removeListenerFunction = if (lastBead == null) gameModel.removeListener _ else lastBead.removeListener _
    def setter(x: Double) = if (gameModel.robotEnergy > 0) bead.parallelAppliedForce = x else {}
    appliedForceControl.setModel(() => bead.parallelAppliedForce, setter, removeListenerFunction, bead.addListener)

    addAllVectors(bead)
    //todo: remove vector nodes when bead is removed
    //todo: switch to removalListeners paradigm
  }

  def changeY(dy: Double) = {
    val result = model.rampSegments(0).startPoint + new Vector2D(0, dy)
    if (result.y < 1E-8)
      new Vector2D(result.x, 1E-8)
    else
      result
  }

  def updatePosition(dy: Double) = {
    model.rampSegments(0).startPoint = changeY(dy)
    model.bead.setPosition(-model.rampSegments(0).length)
  }

  override def addWallsAndDecorations() = {}

  def createLeftSegmentNode = new ReverseRotatableSegmentNode(model.rampSegments(0), transform)

  def createRightSegmentNode = new RampSegmentNode(model.rampSegments(1), transform)

  def addHeightAndAngleIndicators() = {
    addNode(new RampHeightIndicator(new Reverse(model.rampSegments(0)).reverse, transform))
    addNode(new RampAngleIndicator(new Reverse(model.rampSegments(0)).reverse, transform))
  }

  def createEarthNode = new EarthNodeWithCliff(transform, model.rampSegments(1).length, gameModel.airborneFloor)

}

class ItemReadout(text: String, gameModel: RobotMovingCompanyGameModel, counter: () => Int) extends PNode {
  val textNode = new PText(text)
  textNode.setFont(new PhetFont(18, true))
  addChild(textNode)
  gameModel.addListenerByName(update())
  def update() = {
    textNode.setText(text + ": " + counter())
  }
  update()
}

class RobotEnergyMeter(transform: ModelViewTransform2D, gameModel: RobotMovingCompanyGameModel) extends PNode {
  val barNode = new PhetPPath(Color.blue)
  addChild(barNode)
  val label = new PText("Robot Energy")
  label.setFont(new PhetFont(24, true))
  addChild(label)

  defineInvokeAndPass(gameModel.addListenerByName) {
    barNode.setPathTo(new RoundRectangle2D.Double(0, 0, gameModel.robotEnergy / 10, 25, 10, 10))
    label.setOffset(barNode.getFullBounds.getX, barNode.getFullBounds.getMaxY)
  }
}

class ScoreboardNode(transform: ModelViewTransform2D, gameModel: RobotMovingCompanyGameModel) extends PNode {
  val background = new PhetPPath(new RoundRectangle2D.Double(0, 0, 600, 100, 20, 20), Color.lightGray)
  addChild(background)

  val layoutNode = new SwingLayoutNode
  val pText = new PText()

  def update = pText.setText("Score: " + gameModel.score)
  gameModel.addListenerByName(update)
  update

  pText.setFont(new PhetFont(32, true))
  layoutNode.addChild(new Spacer)
  layoutNode.addChild(pText)
  class Spacer extends PNode {
    setBounds(0, 0, 20, 20)
  }
  layoutNode.addChild(new Spacer)
  layoutNode.addChild(new Spacer)
  layoutNode.addChild(new ItemReadout("Moved Items", gameModel, () => gameModel.movedItems))
  layoutNode.addChild(new Spacer)
  layoutNode.addChild(new ItemReadout("Lost Items", gameModel, () => gameModel.lostItems))
  layoutNode.addChild(new Spacer)

  addChild(layoutNode)
  val insetX = 5
  val insetY = 5
  updateBackground()
  def updateBackground() = {
    background.setPathTo(new RoundRectangle2D.Double(layoutNode.getFullBounds.x - insetX, layoutNode.getFullBounds.y - insetY, layoutNode.getFullBounds.width + insetX * 2, layoutNode.getFullBounds.height + insetY * 2, 20, 20))
  }
  gameModel.addListener(() => updateBackground())

  setOffset(transform.getViewBounds.getCenterX - getFullBounds.getWidth / 2, 0)
}

class SummaryScreenNode(gm: RobotMovingCompanyGameModel, scalaRampObject: ScalaRampObject, result: Result, okPressed: SummaryScreenNode => Unit, okButtonText: String) extends PNode {
  val background = new PhetPPath(new RoundRectangle2D.Double(0, 0, 400, 400, 20, 20), new Color(192, 192, 192, 245), new BasicStroke(2), Color.darkGray)
  addChild(background)
  val text = result match {
    case Result(_, true, _, _) => "Crashed"
    case Result(true, false, _, _) => "Delivered Successfully"
    case Result(false, false, _, _) => "Missed the House"
    case _ => "Disappeared?"
  }
  val pText = new PText(scalaRampObject.name + " " + text)
  pText.setFont(new PhetFont(22, true))
  addChild(pText)
  pText.setOffset(background.getFullBounds.getCenterX - pText.getFullBounds.width / 2, 20)

  val image = new PImage(BufferedImageUtils.rescaleYMaintainAspectRatio(RampResources.getImage(scalaRampObject.imageFilename), 150))
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

  layoutNode.addChild(new SummaryText(result.objectPoints + " points x " + result.scoreMultiplier), constraints(0, 0, 2))
  layoutNode.addChild(new SummaryText(result.robotEnergy + " Joules x " + result.pointsPerJoule + " points/Joule"), constraints(0, 1, 2))
  layoutNode.addChild(new SummaryText("= " + result.totalObjectPoints + " points"), constraints(2, 0, 1))
  layoutNode.addChild(new SummaryText("= " + result.totalEnergyPoints + " points"), constraints(2, 1, 1))
  layoutNode.addChild(new PhetPPath(new Line2D.Double(0, 0, 100, 0), new BasicStroke(2), Color.black), constraints(2, 2, 1))
  layoutNode.addChild(new SummaryText("Total"), constraints(0, 3, 2))
  layoutNode.addChild(new SummaryText("= " + result.score + " points"), constraints(2, 3, 1))
  layoutNode.setOffset(background.getFullBounds.getCenterX - layoutNode.getFullBounds.width / 2, image.getFullBounds.getMaxY + 10)
  addChild(layoutNode)

  addChild(donePSwing)
}

object TestSummaryScreen {
  def main(args: Array[String]) {
    val summaryScreenNode = new SummaryScreenNode(new RobotMovingCompanyGameModel(new RampModel(5, true,RampDefaults.defaultRampAngle), new ScalaClock(30, 30 / 1000.0)), RampDefaults.objects(0), new Result(true, false, 64, 100), a => {
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