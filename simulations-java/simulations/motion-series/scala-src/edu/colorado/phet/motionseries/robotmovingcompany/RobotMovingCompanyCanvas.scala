package edu.colorado.phet.motionseries.sims.rampforcesandmotion.robotmovingcompany

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.phetcommon.view.util.{BufferedImageUtils, PhetFont}
import edu.colorado.phet.common.piccolophet.nodes.layout.SwingLayoutNode
import java.awt.event.{KeyEvent, KeyAdapter}
import edu.umd.cs.piccolo.nodes.{PImage, PText}
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.motionseries.graphics._
import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.motionseries.MotionSeriesResources._
import java.awt.geom.{Point2D, AffineTransform, RoundRectangle2D}
import edu.colorado.phet.scalacommon.util.Observable
import javax.swing._
import edu.colorado.phet.motionseries.{StageContainerArea, MotionSeriesDefaults, MotionSeriesResources}
import edu.colorado.phet.motionseries.javastage.stage.StageNode
import edu.umd.cs.piccolox.pswing.PSwing
import swing.Button
import java.awt.{BasicStroke, GridLayout, Color, Rectangle}

/**
 * This class represents the play area for the Robot Moving Company games for Ramps II and Forces and Motion.
 * @author Sam Reid
 */
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

  //Configure visibility and usability of different features for game mode
  motionSeriesObjectNode.setVisible(false)
  playAreaVectorNode.setVisible(false)
  pusherNode.setVisible(false)
  vectorViewModel.sumOfForcesVector = true
  freeBodyDiagramModel.visible = true
  freeBodyDiagramModel.closable = false
  private var numClockTicksWithUserApplication = 0 //for determining if they need a wiggle me

  private var currentBeadNode: PNode = null //keep track of the current bead graphic for layering purposes
  fbdNode.removeCursorHand() //User is not allowed to create forces by clicking in the FBD area

  gameModel.itemFinishedListeners += ((scalaRampObject: MotionSeriesObjectType, result: Result) => {
    val summaryScreen = new ItemFinishedDialog(gameModel, scalaRampObject, result, node => {
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

    //If successfully delivered to house, move the object into the house by putting it in between the back and front layers
    if (result.success) {
      removeScreenNode(currentBeadNode)
      intermediateNode.addChild(new StageNode(stage, RobotMovingCompanyCanvas.this, currentBeadNode))
    }
  })

  updateFBDLocation()

  val houseBackNode = new MotionSeriesObjectNode(gameModel.house, transform, MotionSeriesDefaults.houseBack.imageFilename)
  addStageNode(houseBackNode)

  val intermediateNode = new PNode
  addScreenNode(intermediateNode)

  val houseNode = new MotionSeriesObjectNode(gameModel.house, transform, MotionSeriesDefaults.house.imageFilename)
  addStageNode(houseNode)

  val doorNode = new PNode() {
    val bead = new MotionSeriesObjectNode(gameModel.door, getModelStageTransform, MotionSeriesDefaults.door.imageFilename)
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
  
  addStageNode(new InstructionsNode {
    val helpButton = new PSwing(Button("Help") {
      val intro = new IntroDialog {
        centerWithin(RobotMovingCompanyCanvas.this.getWidth, RobotMovingCompanyCanvas.this.getHeight)
      }
      addScreenNode(intro)

      addKeyListener(new KeyAdapter {
        override def keyPressed(e: KeyEvent) = {
          removeKeyListener(this)
          removeScreenNode(intro)
        }
      })
      SwingUtilities.invokeLater(new Runnable() {
        def run = requestFocus() //so the button doesn't have focus
      })
    }.peer) {
      setOffset(textNode.getFullBounds.getCenterX - getFullBounds.getWidth / 2, textNode.getFullBounds.getMaxY)
    }
    addChild(helpButton)
    setOffset(getStage.getWidth - getFullBounds.getWidth, 5)
  })

  val robotGraphics = new RobotGraphics(transform, gameModel)
  addStageNode(robotGraphics) //TODO: move behind ramp

  gameModel.beadCreatedListeners += init
  init(gameModel.bead, gameModel.selectedObject)

  private var _currentBead: MotionSeriesObject = null

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
    numClockTicksWithUserApplication = numClockTicksWithUserApplication + 1
    gameModel.bead.parallelAppliedForce = if (gameModel.robotEnergy > 0) userInputModel.appliedForce else 0.0
  }) //todo: when robot energy hits zero, applied force should disappear

  def hasUserAppliedForce = numClockTicksWithUserApplication > 5

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
  class ObjectIcon(a: MotionSeriesObjectType) extends PNode {
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

  def showGameSummary() = {
    //gray out the play area to help focus on the results
    val overlayNode = new PhetPPath(new Rectangle(getWidth, getHeight), new Color(100, 120, 100, 240))
    addScreenNode(overlayNode)

    val gameFinishedDialog = new GameFinishedDialog(gameModel) {
      centerWithin(RobotMovingCompanyCanvas.this.getWidth, RobotMovingCompanyCanvas.this.getHeight)

      override def okButtonPressed() = {
        removeScreenNode(overlayNode)
        removeScreenNode(this)
        gameModel.resetAll()
        RobotMovingCompanyCanvas.this.requestFocus()
      }
    }
    addScreenNode(gameFinishedDialog)
    gameFinishedDialog.requestFocus()
  }

  override def updateFBDLocation() = {
    if (fbdNode != null) {
      val pt = transform.modelToView(0, -1)
      fbdNode.setOffset(pt.x - fbdNode.getFullBounds.getWidth / 2, pt.y)
    }
  }

  def init(bead: ForceMotionSeriesObject, a: MotionSeriesObjectType) = {
    val lastBead = _currentBead
    _currentBead = bead

    val beadNode = new MotionSeriesObjectNode(bead, transform, a.imageFilename, a.crashImageFilename)
    currentBeadNode = beadNode
    addStageNode(beadNode)
    val icon = new ObjectIcon(a)
    val pt = transform.modelToView(-10, -1)
    icon.setOffset(pt)
    addStageNode(icon)

    val roboBead = MovingManMotionSeriesObject(model, -10 - a.width / 2, 1, 3)

    val pusherNode = new RobotPusherNode(transform, bead, roboBead)
    addStageNode(pusherNode)

    bead.removalListeners += (() => {
      removeStageNode(beadNode)
      removeStageNode(pusherNode)
      removeStageNode(icon)
    })

    fbdNode.clearVectors()
    windowFBDNode.clearVectors()

    def setter(x: Double) = if (gameModel.robotEnergy > 0) bead.parallelAppliedForce = x else {}

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
    model.motionSeriesObject.setPosition(-model.rampSegments(0).length)
  }

  override def addWallsAndDecorations() = {}

  override def createRightSegmentNode = new RampSegmentNode(model.rampSegments(1), transform, model)

  override def addHeightAndAngleIndicators() = {}

  override def createEarthNode = new EarthNodeWithCliff(transform, model.rampSegments(1).length, gameModel.airborneFloor)

  def showWiggleMe() {
    val instructionsNode = new InstructionsNode {
      scale(2.0)
      setOffset(RobotMovingCompanyCanvas.this.getWidth / 2 - getFullBounds.getWidth / 2, RobotMovingCompanyCanvas.this.getHeight / 4)
    }
    addScreenNode(instructionsNode)
    addKeyListener(new KeyAdapter() {
      override def keyPressed(e: KeyEvent) = {
        removeKeyListener(this)
        removeScreenNode(instructionsNode)
      }
    })
  }
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