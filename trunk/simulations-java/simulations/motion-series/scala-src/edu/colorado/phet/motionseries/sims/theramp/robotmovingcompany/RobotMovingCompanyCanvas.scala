package edu.colorado.phet.motionseries.sims.theramp.robotmovingcompany

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.phetcommon.view.util.{BufferedImageUtils, PhetFont}
import edu.colorado.phet.common.piccolophet.nodes.layout.SwingLayoutNode
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt._
import java.awt.event.{KeyEvent, KeyAdapter}
import edu.colorado.phet.scalacommon.ScalaClock
import edu.umd.cs.piccolo.nodes.{PImage, PText}
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.motionseries.graphics._
import edu.colorado.phet.motionseries.model._
import edu.umd.cs.piccolox.pswing.PSwing
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.motionseries.MotionSeriesResources._
import geom.{Point2D, AffineTransform, Line2D, RoundRectangle2D}
import edu.colorado.phet.scalacommon.util.Observable
import javax.swing._
import edu.colorado.phet.motionseries.{StageContainerArea, MotionSeriesDefaults, MotionSeriesResources}

class RobotMovingCompanyCanvas(model: MotionSeriesModel,
                               coordinateSystemModel: AdjustableCoordinateModel,
                               freeBodyDiagramModel: FreeBodyDiagramModel,
                               vectorViewModel: VectorViewModel,
                               frame: JFrame,
                               gameModel: RobotMovingCompanyGameModel,
                               stageContainerArea: StageContainerArea,
                               energyScale: Double)
        extends RampCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel,
          frame, false, false, true, MotionSeriesDefaults.robotMovingCompanyRampViewport, stageContainerArea) {
  beadNode.setVisible(false)
  playAreaVectorNode.setVisible(false)
  pusherNode.setVisible(false)

  def showGameSummary() = {
    JOptionPane.showMessageDialog(RobotMovingCompanyCanvas.this, "game.summary.pattern.score".messageformat(gameModel.score))
    gameModel.resetAll()
  }

  gameModel.itemFinishedListeners += ((scalaRampObject: MotionSeriesObject, result: Result) => {
    val summaryScreen = new SummaryScreenNode(gameModel, scalaRampObject, result, node => {
      removeStageNode(node)
      requestFocus()
      if (gameModel.isLastObject(scalaRampObject))
        showGameSummary()
      else
        gameModel.nextObject()
    }, if (gameModel.isLastObject(scalaRampObject)) "game.show-summary".translate else "game.ok".translate)
    summaryScreen.centerWithin(stage.getWidth, stage.getHeight)
    addStageNode(summaryScreen)
    summaryScreen.requestFocus()
  })

  override def updateFBDLocation() = {
    if (fbdNode != null) {
      val pt = transform.modelToView(0, -1)
      fbdNode.setOffset(pt.x - fbdNode.getFullBounds.getWidth / 2, pt.y)
    }
  }
  updateFBDLocation()

  vectorViewModel.sumOfForcesVector = true
  freeBodyDiagramModel.visible = true
  freeBodyDiagramModel.closable = false

  val houseNode = new BeadNode(gameModel.house, transform, MotionSeriesDefaults.house.imageFilename)
  addStageNode(houseNode)

  //layer that shows what's behind the door.
  val doorBackgroundNode = new BeadNode(gameModel.doorBackground, transform, MotionSeriesDefaults.doorBackground.imageFilename)
  addStageNode(doorBackgroundNode)

  class InstructionsNode extends PNode {
    val iconSet = new KeyboardButtonIcons {
      scale(0.4)
    }
    addChild(iconSet)
    val textNode = new PText("game.instructions.press-arrow-keys".translate){
      setOffset(iconSet.getFullBounds.getCenterX - getFullBounds.getWidth / 2, iconSet.getFullBounds.getMaxY)
    }
    addChild(textNode)
    setOffset(getStage.getWidth-getFullBounds.getWidth,5)
  }
  addStageNode(new InstructionsNode)

  val doorNode = new PNode() {
    val bead = new BeadNode(gameModel.door, getModelStageTransform, MotionSeriesDefaults.door.imageFilename)
    addChild(bead)

    gameModel.doorListeners += (() => {
      val sx = new edu.colorado.phet.common.phetcommon.math.Function.LinearFunction(0, 1, 1.0, 0.2).evaluate(gameModel.doorOpenAmount)
      val shx = new edu.colorado.phet.common.phetcommon.math.Function.LinearFunction(0, 1, 0, 0.15).evaluate(gameModel.doorOpenAmount)
      val tx = AffineTransform.getScaleInstance(sx, 1.0)
      setTransform(new AffineTransform)
      val point2D = new Point2D.Double(getFullBounds.getX, getFullBounds.getY)

      //see scale about point
      getTransformReference(true).translate(point2D.getX, point2D.getY)
      getTransformReference(true).scale(sx, 1.0)
      getTransformReference(true).shear(0, shx)
      getTransformReference(true).translate(-point2D.getX, -point2D.getY)
    })
  }
  addStageNode(doorNode)

  val scoreboard = new ScoreboardNode(transform, gameModel)
  addStageNode(scoreboard)

  val energyMeter = new RobotEnergyMeter(transform, gameModel, energyScale)
  energyMeter.setOffset(scoreboard.getFullBounds.getX + 5, scoreboard.getFullBounds.getMaxY + 5)
  addStageNode(energyMeter)

  val robotGraphics = new RobotGraphics(transform, gameModel)
  addStageNode(robotGraphics) //TODO: move behind ramp

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

  val F = gameModel.appliedForceAmount
  val NONE = ("none".literal, 0.0)
  val RIGHT = ("right".literal, F)
  val LEFT = ("left".literal, -F)

  object userInputModel extends Observable {
    private var _pressed: (String, Double) = NONE

    def pressed = _pressed

    def pressed_=(p: (String, Double)) = {
      _pressed = p
      notifyListeners()
    }

    def appliedForce = _pressed._2
  }
  userInputModel.addListenerByName {
    gameModel.launched = true
    model.setPaused(false)
  }

  userInputModel.addListener(() => {
    gameModel.bead.parallelAppliedForce = if (gameModel.robotEnergy > 0) userInputModel.appliedForce else 0.0
  }) //todo: when robot energy hits zero, applied force should disappear

  addKeyListener(new KeyAdapter {
    override def keyPressed(e: KeyEvent) = {
      if (gameModel.inputAllowed)
        e.getKeyCode match {
          case KeyEvent.VK_LEFT => userInputModel.pressed = LEFT
          case KeyEvent.VK_RIGHT => userInputModel.pressed = RIGHT
          case _ => userInputModel.pressed = NONE
        }
      else NONE
    }

    override def keyReleased(e: KeyEvent) = {
      e.getKeyCode match {
        case KeyEvent.VK_LEFT => userInputModel.pressed = NONE
        case KeyEvent.VK_RIGHT => userInputModel.pressed = NONE
        case _ => userInputModel.pressed = NONE
      }
    }
  })
  SwingUtilities.invokeLater(new Runnable {
    def run = requestFocus()
  })
  class ObjectIcon(a: MotionSeriesObject) extends PNode {
    val nameLabel = new PText(a.name) {
      setFont(new PhetFont(24, true))
    }
    addChild(nameLabel)
    val pImage = new PImage(BufferedImageUtils.multiScaleToHeight(MotionSeriesResources.getImage(a.imageFilename), 100))
    addChild(pImage)
    pImage.setOffset(0, nameLabel.getFullBounds.getHeight)

    val textNode = new SwingLayoutNode(new GridLayout(3, 1))
    class ValueText(str: String) extends PText(str) {
      setFont(new PhetFont(18))
    }
    textNode.addChild(new ValueText("game.object.mass-equals.pattern.mass".messageformat(a.mass)))
    textNode.addChild(new ValueText("game.object.kinetic-friction-equals.pattern.kinetic-friction".messageformat(a.kineticFriction)))
    textNode.addChild(new ValueText("game.object.points-equals.pattern.points".messageformat(a.points)))
    textNode.setOffset(0, pImage.getFullBounds.getMaxY)
    addChild(textNode)
  }
  def init(bead: ForceBead, a: MotionSeriesObject) = {
    val lastBead = _currentBead
    _currentBead = bead

    val beadNode = new BeadNode(bead, transform, a.imageFilename,a.crashImageFilename)
    addStageNode(beadNode)
    val icon = new ObjectIcon(a)
    val pt = transform.modelToView(-10, -1)
    //      fbdNode.setOffset(pt.x -fbdNode.getFullBounds.getWidth/2, pt.y)
    icon.setOffset(pt)
    addStageNode(icon)

    val roboBead = model.createBead(-10 - a.width / 2, 1, 3)

    val pusherNode = new RobotPusherNode(transform, bead, roboBead)
    addStageNode(pusherNode)

    bead.removalListeners += (() => {
      removeStageNode(beadNode)
      removeStageNode(pusherNode)
      removeStageNode(icon)
    })

    fbdNode.clearVectors()
    windowFBDNode.clearVectors()

    def removeTheListener(listener: () => Unit) {
      if (lastBead == null) gameModel.removeListener(listener) else lastBead.removeListener(listener)
    }
    //    def removeListenerFunction():Unit = if (lastBead == null) gameModel.removeListener _ else lastBead.removeListener _
    def setter(x: Double) = if (gameModel.robotEnergy > 0) bead.parallelAppliedForce = x else {}
    //    appliedForceControl.setModel(() => bead.parallelAppliedForce, setter, removeTheListener, bead.addListener)

    //todo: why are these 2 lines necessary?
    vectorView.addAllVectors(bead, fbdNode)
    vectorView.addAllVectors(bead, windowFBDNode)
    //don't show play area vectors

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

  //  override def createLeftSegmentNode = new ReverseRotatableSegmentNode(model.rampSegments(0), transform, model)
  override def createLeftSegmentNode = new RampSegmentNode(model.rampSegments(0), transform, model)

  override def createRightSegmentNode = new RampSegmentNode(model.rampSegments(1), transform, model)

  override def addHeightAndAngleIndicators() = {
    //    addStageNode(new RampHeightIndicator(new Reverse(model.rampSegments(0)).reverse, transform))
    //    addStageNode(new RampAngleIndicator(new Reverse(model.rampSegments(0)).reverse, transform))
  }

  override def createEarthNode = new EarthNodeWithCliff(transform, model.rampSegments(1).length, gameModel.airborneFloor)
}

class ItemReadout(text: String, gameModel: RobotMovingCompanyGameModel, counter: () => Int) extends PNode {
  val textNode = new PText(text)
  textNode.setFont(new PhetFont(18, true))
  addChild(textNode)
  gameModel.addListenerByName(update())
  def update() = {
    textNode.setText("game.item-readout-counter.pattern.name-count".messageformat(text, counter().toString))
  }
  update()
}

class RobotEnergyMeter(transform: ModelViewTransform2D, gameModel: RobotMovingCompanyGameModel, energyScale: Double) extends PNode {
  val barContainerNode = new PhetPPath(new BasicStroke(2), Color.gray)
  val barNode = new PhetPPath(Color.blue)
  addChild(barNode)
  val label = new PText("game.robot-energy".translate)
  label.setFont(new PhetFont(24, true))
  addChild(label)
  addChild(barContainerNode)
  def energyToBarShape(e: Double) = new RoundRectangle2D.Double(label.getFullBounds.getWidth + 10, 0, e * energyScale, 25, 10, 10)
  barContainerNode.setPathTo(energyToBarShape(gameModel.DEFAULT_ROBOT_ENERGY))

  defineInvokeAndPass(gameModel.addListenerByName) {
    barNode.setPathTo(energyToBarShape(gameModel.robotEnergy))
  }
}

class ScoreboardNode(transform: ModelViewTransform2D, gameModel: RobotMovingCompanyGameModel) extends PNode {
  val background = new PhetPPath(new RoundRectangle2D.Double(0, 0, 600, 100, 20, 20), Color.lightGray)
  addChild(background)

  val layoutNode = new SwingLayoutNode
  val pText = new PText()

  def update = pText.setText("game.scoreboard.score.pattern.score".messageformat(gameModel.score))
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
  layoutNode.addChild(new ItemReadout("game.moved-items".translate, gameModel, () => gameModel.movedItems))
  layoutNode.addChild(new Spacer)
  layoutNode.addChild(new ItemReadout("game.lost-items".translate, gameModel, () => gameModel.lostItems))
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