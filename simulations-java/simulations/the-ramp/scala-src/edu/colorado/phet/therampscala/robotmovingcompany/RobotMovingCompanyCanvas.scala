package edu.colorado.phet.therampscala.robotmovingcompany

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.nodes.layout.SwingLayoutNode
import java.awt.event.{MouseEvent, MouseAdapter}
import javax.swing.event.{ChangeEvent, ChangeListener}

import javax.swing.{JOptionPane, JFrame, Box}
import umd.cs.piccolo.nodes.PText
import common.phetcommon.view.util.PhetFont
import common.phetcommon.view.VerticalLayoutPanel
import umd.cs.piccolo.PNode
import common.piccolophet.nodes.PhetPPath
import java.awt.geom.RoundRectangle2D
import java.awt.{Color, Dimension}
import scalacommon.math.Vector2D
import graphics._
import model._
import swing.{ScalaValueControl, ScalaButton}
import umd.cs.piccolox.pswing.PSwing
import scalacommon.Predef._

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

  //removed for robotmovingcompany: automatically go to next object when you score or lose the object (instead of hitting "next object" button)
  //  controlPanel.add(Box.createRigidArea(new Dimension(10, 10)))
  //  val nextObjectButton = new ScalaButton("Next Object", () => gameModel.nextObject())
  //  val updateNextObjectButtonEnabled = () => {nextObjectButton.setEnabled(gameModel.readyForNext)}
  //  updateNextObjectButtonEnabled()
  //  gameModel.addListener(updateNextObjectButtonEnabled)
  //  controlPanel.add(nextObjectButton)

  gameModel.gameFinishListeners += (() => {
    JOptionPane.showMessageDialog(RobotMovingCompanyCanvas.this, "That was the last object to move.  \nYour score is: " + gameModel.score + ".")
    gameModel.resetAll()
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
    rightSegmentNode.paint_=(gameModel.surfaceModel.surfaceType.color)
    leftSegmentNode.paint_=(gameModel.surfaceModel.surfaceType.color)
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