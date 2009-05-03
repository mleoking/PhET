package edu.colorado.phet.therampscala.graphics


import common.phetcommon.resources.PhetCommonResources
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.{PhetFont, SwingUtils}
import common.phetcommon.view.VerticalLayoutPanel
import common.piccolophet.event.CursorHandler
import common.piccolophet.nodes.layout.SwingLayoutNode
import common.piccolophet.nodes.PhetPPath
import java.awt.geom.{RoundRectangle2D, Point2D, Rectangle2D}
import java.awt.{Dimension, Color}
import javax.swing.{Box, JButton, JFrame, JDialog}
import swing.{ScalaValueControl, ScalaButton}
import umd.cs.piccolo.nodes.{PImage, PText}
import common.piccolophet.PhetPCanvas
import java.awt.event._

import model._
import scalacommon.math.Vector2D
import scalacommon.Predef._
import scalacommon.util.Observable
import theramp.model.ValueAccessor.ParallelForceAccessor
import umd.cs.piccolo.event.{PInputEvent, PBasicInputEventHandler}
import umd.cs.piccolo.PNode
import java.lang.Math._
import umd.cs.piccolox.pswing.PSwing

abstract class AbstractRampCanvas(model: RampModel, coordinateSystemModel: CoordinateSystemModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                                  vectorViewModel: VectorViewModel, frame: JFrame) extends DefaultCanvas(22, 20) {
  setBackground(RampDefaults.SKY_GRADIENT_BOTTOM)

  addNode(new SkyNode(transform))

  def createEarthNode: PNode
  addNode(createEarthNode)

  def createLeftSegmentNode: HasPaint

  val leftSegmentNode = createLeftSegmentNode
  addNode(leftSegmentNode)

  def createRightSegmentNode: HasPaint

  val rightSegmentNode = createRightSegmentNode
  addNode(rightSegmentNode)

  def addHeightAndAngleIndicators()
  addHeightAndAngleIndicators()

  def addWallsAndDecorations()
  addWallsAndDecorations()

  val beadNode = new DraggableBeadNode(model.bead, transform, "cabinet.gif")
  model.addListenerByName(beadNode.setImage(RampResources.getImage(model.selectedObject.imageFilename)))
  addNode(beadNode)

  val pusherNode = new PusherNode(transform, model.bead, model.manBead)
  addNode(pusherNode)

  addNode(new CoordinateFrameNode(model, coordinateSystemModel, transform))

  private def compositeListener(listener: () => Unit) = {
    model.rampSegments(0).addListener(listener)
    model.rampSegments(1).addListener(listener)
  }

  val tickMarkSet = new TickMarkSet(transform, model.positionMapper, compositeListener) //todo: listen to both segments for game
  addNode(tickMarkSet)

  val fbdWidth = RampDefaults.freeBodyDiagramWidth
  val fbdNode = new FreeBodyDiagramNode(freeBodyDiagramModel, 200, 200, fbdWidth, fbdWidth, model.coordinateFrameModel, coordinateSystemModel.adjustable, PhetCommonResources.getImage("buttons/maximizeButton.png"))
  val fbdListener = (pt: Point2D) => {model.bead.parallelAppliedForce = pt.getX}
  fbdNode.addListener(fbdListener)
  fbdNode.setOffset(10, 10)
  addNode(fbdNode)
  defineInvokeAndPass(freeBodyDiagramModel.addListenerByName) {
    fbdNode.setVisible(freeBodyDiagramModel.visible && !freeBodyDiagramModel.windowed)
  }

  val fbdWindow = new JDialog(frame, "Free Body Diagram", false)
  fbdWindow.setSize(600, 600)

  //create FBD canvas
  val windowFBDNode = new FreeBodyDiagramNode(freeBodyDiagramModel, 600, 600, fbdWidth, fbdWidth, model.coordinateFrameModel, coordinateSystemModel.adjustable, PhetCommonResources.getImage("buttons/minimizeButton.png"))
  windowFBDNode.addListener(fbdListener)
  val canvas = new PhetPCanvas
  canvas.addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent) = updateNodeSize()
  })
  updateNodeSize()
  def updateNodeSize() = {
    if (canvas.getWidth > 0 && canvas.getHeight > 0) {
      val w = Math.min(canvas.getWidth, canvas.getHeight)
      val inset = 40
      windowFBDNode.setSize(w - inset * 2, w - inset * 2)
      windowFBDNode.setOffset(inset, inset)
    }
  }
  canvas.addScreenChild(windowFBDNode)
  fbdWindow.setContentPane(canvas)

  var initted = false
  defineInvokeAndPass(freeBodyDiagramModel.addListenerByName) {
    val wasVisible = fbdWindow.isVisible
    fbdWindow.setVisible(freeBodyDiagramModel.visible && freeBodyDiagramModel.windowed)
    if (fbdWindow.isVisible && !wasVisible && !initted) {
      initted = true
      SwingUtils.centerDialogInParent(fbdWindow)
    }
    updateNodeSize()
  }
  fbdWindow.addWindowListener(new WindowAdapter {
    override def windowClosing(e: WindowEvent) = {
      if (!freeBodyDiagramModel.closable) {
        freeBodyDiagramModel.windowed = false
      } else {
        freeBodyDiagramModel.visible = false
      }
    }
  })

  class VectorSetNode(transform: ModelViewTransform2D, bead: Bead) extends PNode {
    def addVector(a: Vector, offset: VectorValue) = {
      val node = new BodyVectorNode(transform, a, offset)
      addChild(node)
    }
  }

  class BodyVectorNode(transform: ModelViewTransform2D, vector: Vector, offset: VectorValue) extends VectorNode(transform, vector, offset) {
    model.bead.addListenerByName {
      setOffset(model.bead.position2D)
      update()
    }
  }

  val vectorNode = new VectorSetNode(transform, model.bead)
  addNode(vectorNode)
  def addVectorAllComponents(bead: Bead, beadVector: BeadVector with PointOfOriginVector, offsetFBD: VectorValue,
                             offsetPlayArea: Double, selectedVectorVisible: () => Boolean) = {
    addVector(bead, beadVector, offsetFBD, offsetPlayArea)
    val parallelComponent = new ParallelComponent(beadVector, bead)
    val perpComponent = new PerpendicularComponent(beadVector, bead)
    val xComponent = new XComponent(beadVector, bead, model.coordinateFrameModel)
    val yComponent = new YComponent(beadVector, bead, model.coordinateFrameModel)
    def update() = {
      yComponent.visible = vectorViewModel.xyComponentsVisible && selectedVectorVisible()
      xComponent.visible = vectorViewModel.xyComponentsVisible && selectedVectorVisible()
      beadVector.visible = vectorViewModel.originalVectors && selectedVectorVisible()
      parallelComponent.visible = vectorViewModel.parallelComponents && selectedVectorVisible()
      perpComponent.visible = vectorViewModel.parallelComponents && selectedVectorVisible()
    }
    vectorViewModel.addListenerByName(update())
    update()

    addVector(bead, xComponent, offsetFBD, offsetPlayArea)
    addVector(bead, yComponent, offsetFBD, offsetPlayArea)
    addVector(bead, parallelComponent, offsetFBD, offsetPlayArea)
    addVector(bead, perpComponent, offsetFBD, offsetPlayArea)
  }

  def addVector(bead: Bead, vector: Vector with PointOfOriginVector, offsetFBD: VectorValue, offsetPlayArea: Double) = {
    fbdNode.addVector(vector, offsetFBD)
    windowFBDNode.addVector(vector, offsetFBD)

    val tailLocationInPlayArea = new VectorValue() {
      def addListenerByName(listener: => Unit) = {
        bead.addListenerByName(listener)
        vectorViewModel.addListenerByName(listener)
      }

      def getValue = {
        val defaultCenter = bead.height / 2.0
        bead.position2D + new Vector2D(bead.getAngle + PI / 2) *
                (offsetPlayArea + (if (vectorViewModel.centered) defaultCenter else vector.getPointOfOriginOffset(defaultCenter)))
      }

    }
    //todo: make sure this adapter overrides other methods as well, such as getPaint and addListener
    val playAreaAdapter = new Vector(vector.color, vector.name, vector.abbreviation, () => vector.getValue * RampDefaults.PLAY_AREA_VECTOR_SCALE, vector.painter) {
      override def visible = vector.visible

      override def visible_=(vis: Boolean) = vector.visible = vis

      override def getPaint = vector.getPaint
    }
    vectorNode.addVector(playAreaAdapter, tailLocationInPlayArea)
//todo: switch to removalListeners paradigm
//    bead.removalListeners += (()=>{
//      fbdNode.removeVector(vector)
//      windowFBDNode.removeVector(vector)
//      vectorNode.removeVector(playAreaAdapter)
//    })

  }

  def addVectorAllComponents(bead: Bead, a: BeadVector): Unit = addVectorAllComponents(bead, a, new ConstantVectorValue, 0, () => true)

  def addAllVectors(bead: Bead) = {
    addVectorAllComponents(bead, bead.appliedForceVector)
    addVectorAllComponents(bead, bead.gravityForceVector)
    addVectorAllComponents(bead, bead.normalForceVector)
    addVectorAllComponents(bead, bead.frictionForceVector)
    addVectorAllComponents(bead, bead.wallForceVector)
    addVectorAllComponents(bead, bead.totalForceVector, new ConstantVectorValue(new Vector2D(0, fbdWidth / 4)), 2, () => vectorViewModel.sumOfForcesVector) //no need to add a separate listener, since it is already contained in vectorviewmodel
  }
  addAllVectors(model.bead)
}

class RampCanvas(model: RampModel, coordinateSystemModel: CoordinateSystemModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                 vectorViewModel: VectorViewModel, frame: JFrame) extends AbstractRampCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel, frame) {
  addNode(new ObjectSelectionNode(transform, model))
  addNode(new AppliedForceSliderNode(model.bead, transform))

  override def addWallsAndDecorations() = {
    addNode(new BeadNode(model.leftWall, transform, "wall.jpg") with CloseButton {
      def model = RampCanvas.this.model
    })
    addNode(new BeadNode(model.rightWall, transform, "wall.jpg") with CloseButton {
      def model = RampCanvas.this.model
    })
  }

  def createLeftSegmentNode = new RampSegmentNode(model.rampSegments(0), transform)

  def createRightSegmentNode = new RotatableSegmentNode(model.rampSegments(1), transform)

  def addHeightAndAngleIndicators() = {
    addNode(new RampHeightIndicator(model.rampSegments(1), transform))
    addNode(new RampAngleIndicator(model.rampSegments(1), transform))
  }

  def createEarthNode = new EarthNode(transform)

}

class RMCCanvas(model: RampModel, coordinateSystemModel: CoordinateSystemModel, freeBodyDiagramModel: FreeBodyDiagramModel,
                vectorViewModel: VectorViewModel, frame: JFrame, gameModel: RobotMovingCompanyGameModel)
        extends AbstractRampCanvas(model, coordinateSystemModel, freeBodyDiagramModel, vectorViewModel, frame) {
  beadNode.setVisible(false)
  vectorNode.setVisible(false)
  pusherNode.setVisible(false)

  val controlPanel = new VerticalLayoutPanel
  controlPanel.setFillNone()
  val robotGoButton = new ScalaButton("Robot Go!", () => {
    gameModel.launched = true
    model.bead.parallelAppliedForce = 0 //leave the pusher behind
    model.setPaused(false)
  })
  gameModel.addListener(() => {robotGoButton.setEnabled(!gameModel.launched)})

  val appliedForceControl = new ScalaValueControl(-RampDefaults.MAX_APPLIED_FORCE, RampDefaults.MAX_APPLIED_FORCE, "Applied Force X", "0.0", "N",
    () => 0, value => 0, gameModel.addListener) //todo: last param is a dummy
  controlPanel.add(appliedForceControl)
  controlPanel.add(robotGoButton)

  controlPanel.add(Box.createRigidArea(new Dimension(10, 10)))
  val nextObjectButton = new ScalaButton("Next Object", () => gameModel.nextObject())
  val updateNextObjectButtonEnabled = () => {nextObjectButton.setEnabled(gameModel.readyForNext)}
  updateNextObjectButtonEnabled()
  gameModel.addListener(updateNextObjectButtonEnabled)
  controlPanel.add(nextObjectButton)

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
    rightSegmentNode.paint = gameModel.surfaceModel.surfaceType.color
    leftSegmentNode.paint = gameModel.surfaceModel.surfaceType.color
  }
  updateRampColor()

  private var _currentBead: Bead = null

  def init(bead: Bead, a: ScalaRampObject) = {
    val lastBead = _currentBead
    _currentBead = bead

    val beadNode = new DraggableBeadNode(bead, transform, a.imageFilename)
    addNode(beadNode)

    val roboBead = model.createBead(-10 - a.width / 2, 1)

    val pusherNode = new RobotPusherNode(transform, bead, roboBead)
    addNode(pusherNode)

    gameModel.nextObjectListeners += ((prevObject: ScalaRampObject) => {
      if (prevObject == a) { //todo: get rid of this lookup
        removeNode(beadNode)
        removeNode(pusherNode)
//        removeAllVectors(bead)  //todo: switch to removalListeners paradigm
      }
    })
    fbdNode.clearVectors()
    windowFBDNode.clearVectors()
    //todo: clear play area vectors (or never add them in the first place)
    //    println("adding vectors for bead: " + bead + ", a=" + a)
    //

    val removeListenerM = if (lastBead == null) gameModel.removeListener _ else lastBead.removeListener _
    def setter(x: Double) = if (gameModel.robotEnergy > 0) bead.parallelAppliedForce = x else {}
    appliedForceControl.setModel(() => bead.parallelAppliedForce, setter, removeListenerM, bead.addListener)

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
trait PointOfOriginVector {
  def getPointOfOriginOffset(defaultCenter: Double): Double
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