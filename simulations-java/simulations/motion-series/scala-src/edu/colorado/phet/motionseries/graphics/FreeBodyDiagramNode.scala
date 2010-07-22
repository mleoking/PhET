package edu.colorado.phet.motionseries.graphics

import collection.mutable.ArrayBuffer
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.colorado.phet.common.piccolophet.nodes._
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.geom.{Point2D, Rectangle2D}
import javax.swing.JFrame
import layout.SwingLayoutNode
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.motionseries.MotionSeriesDefaults

import edu.umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import edu.umd.cs.piccolo.nodes.PImage
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.scalacommon.Predef._
import java.lang.Math._
import edu.colorado.phet.motionseries.MotionSeriesResources._
import java.awt.{Paint, Color, Image, BasicStroke}
import edu.colorado.phet.motionseries.model._

class Vector(val color: Color,
             val name: String,
             val abbreviation: String,
             val valueAccessor: () => Vector2D,
             val painter: (Vector2D, Color) => Paint,
             val labelAngle: Double) extends Observable with VectorValue {
  private var _visible = true

  def getValue = valueAccessor()

  def angle = getValue.angle

  def visible = _visible

  def visible_=(vis: Boolean) = {
    _visible = vis
    notifyListeners()
  }

  def getPaint = painter(getValue, color)

  def html = "force.abbreviation.html.pattern.abbrev".messageformat(abbreviation)
}

class AxisModel(private var _angle: Double,
                val length: Double,
                tail: Boolean)
        extends Observable with Rotatable {
  override def angle = _angle

  def getEndPoint = new Vector2D(angle) * length

  def startPoint = if (tail) getEndPoint * -1 else new Vector2D

  def endPoint = getEndPoint

  def getUnitVector = (getEndPoint - startPoint).normalize

  def endPoint_=(newPt: Vector2D) = {
    angle = newPt.angle
    notifyListeners()
  }

  override def angle_=(a: Double) = {
    if (this._angle != a) {
      this._angle = a
      notifyListeners()
    }
  }

  def dropped() = {}

  override def getPivot = new Vector2D

  def startPoint_=(newPt: Vector2D) = {}
}

class FreeBodyDiagramNode(freeBodyDiagramModel: FreeBodyDiagramModel,
                          private var _width: Double,
                          private var _height: Double,
                          val modelWidth: Double,
                          val modelHeight: Double,
                          coordinateFrameModel: CoordinateFrameModel,
                          adjustableCoordinateModel: AdjustableCoordinateModel,
                          toggleWindowedButton: Image,
                          rampAngle: () => Double,
                          vectors: Vector*)
        extends PNode with VectorDisplay {
  private val cursorHandler = new CursorHandler
  addInputEventListener(cursorHandler)
  def removeCursorHand() = {
    removeInputEventListener(cursorHandler)
  }

  def addVector(vector: Vector with PointOfOriginVector, offsetFBD: VectorValue, maxOffset: Int, offsetPlayArea: Double): Unit =
    addVector(vector, offsetFBD, maxOffset)

  val transform = new ModelViewTransform2D(new Rectangle2D.Double(-modelWidth / 2, -modelHeight / 2, modelWidth, modelHeight),
    new Rectangle2D.Double(0, 0, _width, _height), true)

  val background = new PhetPPath(Color.white, new BasicStroke(2), Color.darkGray)
  addChild(background)

  val closeButton = new PImage(PhetCommonResources.getImage("buttons/closeButton.png".literal))
  closeButton.addInputEventListener(new CursorHandler)
  closeButton.addInputEventListener(new PBasicInputEventHandler {
    override def mousePressed(event: PInputEvent) = {
      freeBodyDiagramModel.visible = false
    }
  })
  defineInvokeAndPass(freeBodyDiagramModel.addListenerByName) {
    closeButton.setVisible(freeBodyDiagramModel.closable)
    closeButton.setPickable(freeBodyDiagramModel.closable)
    closeButton.setChildrenPickable(freeBodyDiagramModel.closable)
  }

  val windowedButton = new PImage(toggleWindowedButton) {
    addInputEventListener(new CursorHandler)
    addInputEventListener(new PBasicInputEventHandler {
      override def mousePressed(event: PInputEvent) = {
        freeBodyDiagramModel.windowed = !freeBodyDiagramModel.windowed
      }
    })
  }

  val buttonPanel = new SwingLayoutNode() {
    if (!freeBodyDiagramModel.popupDialogOnly)
      addChild(windowedButton)
    addChild(closeButton)
  }
  addChild(buttonPanel)

  private val listeners = new ArrayBuffer[Point2D => Unit]

  def addListener(listener: Point2D => Unit) = {listeners += listener}

  override def setVisible(visible: Boolean) = {
    super.setVisible(visible)
    setPickable(visible)
    setChildrenPickable(visible)
  }

  background.addInputEventListener(new PBasicInputEventHandler {
    def sendEvent(event: PInputEvent) = {
      val viewPt = event.getPositionRelativeTo(FreeBodyDiagramNode.this)
      val modelPt = transform.viewToModel(viewPt)
      listeners.foreach(_(modelPt))
    }

    override def mouseDragged(event: PInputEvent) = sendEvent(event)

    override def mousePressed(event: PInputEvent) = sendEvent(event)

    override def mouseReleased(event: PInputEvent) = listeners.foreach(_(new Point2D.Double(0, 0)))
  })

  val xAxisModel = new SynchronizedAxisModel(0.0, 0.0, 3.0, modelWidth / 2 * 0.9, true, coordinateFrameModel)
  val yAxisModel = new SynchronizedAxisModel(PI / 2, PI / 2, PI, modelWidth / 2 * 0.9, true, coordinateFrameModel)
  addChild(new AxisNodeWithModel(transform, "coordinates.x".translate, xAxisModel, adjustableCoordinateModel))
  addChild(new AxisNodeWithModel(transform, "coordinates.y".translate, yAxisModel, adjustableCoordinateModel))
  for (vector <- vectors) addVector(vector, MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET)

  updateSize()

  def addVector(vector: Vector, maxDistToLabel: Double): Unit = addVector(vector, new ConstantVectorValue, maxDistToLabel: Double)

  def addVector(vector: Vector, offset: VectorValue, maxDistToLabel: Double) = {
    addChild(new VectorNode(transform, vector, offset, maxDistToLabel))
  }

  def removeVector(vector: Vector) = {
    clearVectors(vector eq _)
  }

  def getVectorCount = {
    var count = 0
    for (i <- 0 until getChildrenCount) {
      getChild(i) match {
        case x: VectorNode => count = count + 1
        case _ => {}
      }
    }
    count
  }

  def clearVectors() {
    clearVectors(x => true)
  }

  def clearVectors(query: Vector => Boolean) = {
    val removeList = new ArrayBuffer[PNode]
    for (i <- 0 until getChildrenCount) {
      getChild(i) match {
        case x: VectorNode => removeList += x
        case _ => {}
      }
    }
    for (node <- removeList) {
      removeChildByIndex(node)
    }
  }

  def removeChildByIndex(node: PNode) {
    val toRemove = new ArrayBuffer[Int]
    for (i <- 0 until getChildrenCount) {
      if (getChild(i) eq node) {
        toRemove += i
      }
    }
    val nodeToRemove = getChild(toRemove(0))
    nodeToRemove.removeAllChildren()
    nodeToRemove match {
      case a: VectorNode => a.deleting()
      case _ => {}
    }
    removeChild(toRemove(0))
  }

  def setSize(width: Double, height: Double) = {
    this._width = width
    this._height = height
    updateSize()
  }

  def updateSize() = {
    background.setPathTo(new Rectangle2D.Double(0, 0, _width, _height))
    transform.setViewBounds(new Rectangle2D.Double(0, 0, _width, _height))
    xAxisModel.notifyListeners()
    yAxisModel.notifyListeners()
    for (i <- 0 until getChildrenCount) {
      val node = getChild(i)
      node match {
        case a: VectorNode => a.update()
        case _ => {}
      }
    }
    buttonPanel.setOffset(background.getFullBounds.getMaxX - buttonPanel.getFullBounds.getWidth - 10, background.getFullBounds.getY)
  }
}

class ConstantVectorValue(val getValue: Vector2D) extends VectorValue {
  def this() = this (new Vector2D)

  def addListener(listener: () => Unit) = {}

  def removeListener(listener: () => Unit) = {}
}

trait VectorValue {
  def getValue: Vector2D

  def addListener(listener: () => Unit): Unit

  def removeListener(listener: () => Unit): Unit

  def apply() = getValue
}

object TestFBD extends Application {
  val frame = new JFrame
  val canvas = new PhetPCanvas
  val vector = new Vector(Color.blue, "Test Vector".literal, "Fv".literal, () => new Vector2D(5, 5), (a, b) => b, PI / 2)
  canvas.addScreenChild(new FreeBodyDiagramNode(new FreeBodyDiagramModel(false), 200, 200, 20, 20, new CoordinateFrameModel(new RampSegment(new Point2D.Double(0, 0), new Point2D.Double(10, 10))), new AdjustableCoordinateModel,
    PhetCommonResources.getImage("buttons/maximizeButton.png".literal), () => PI / 4, vector))
  frame.setContentPane(canvas)
  frame.setSize(800, 600)
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.setVisible(true)
}