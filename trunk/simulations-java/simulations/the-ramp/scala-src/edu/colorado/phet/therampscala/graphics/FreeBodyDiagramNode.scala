package edu.colorado.phet.therampscala.graphics

import collection.mutable.ArrayBuffer
import common.phetcommon.resources.PhetCommonResources
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.PhetFont
import common.piccolophet.event.CursorHandler
import common.piccolophet.nodes._
import common.piccolophet.PhetPCanvas
import java.awt._
import java.awt.geom.{Point2D, Rectangle2D}
import javax.swing.JFrame
import layout.SwingLayoutNode
import model.{FreeBodyDiagramModel, CoordinateFrameModel}
import scalacommon.math.Vector2D
import scalacommon.util.Observable
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEventListener, PInputEvent}
import umd.cs.piccolo.nodes.{PImage, PText}
import umd.cs.piccolo.PNode
import scalacommon.Predef._
import java.lang.Math._
import RampResources._

class Vector(val color: Color,
             val name: String,
             val abbreviation: String, val valueAccessor: () => Vector2D, val painter: (Vector2D, Color) => Paint) extends Observable with VectorValue {
  private var _visible = true

  def getValue = valueAccessor()

  def visible = _visible

  def visible_=(vis: Boolean) = {
    _visible = vis
    notifyListeners()
  }

  def getPaint = painter(getValue, color)

  def html = "force.abbreviation.html.pattern.abbrev".translate.messageformat(abbreviation)
}

class AxisNode(val transform: ModelViewTransform2D, x0: Double, y0: Double, x1: Double, y1: Double, label: String) extends PNode {
  private val axisNode = new ArrowNode(transform.modelToViewDouble(x0, y0), transform.modelToViewDouble(x1, y1), 5, 5, 2)
  axisNode.setStroke(null)
  axisNode.setPaint(Color.black)

  //use an invisible wider hit region for mouse events
  protected val hitNode = new ArrowNode(transform.modelToViewDouble(x0, y0), transform.modelToViewDouble(x1, y1), 10, 10, 10)
  hitNode.setStroke(null)
  hitNode.setPaint(new Color(0, 0, 0, 0))
  axisNode.setPickable(false)
  axisNode.setChildrenPickable(false)

  addChild(hitNode)
  addChild(axisNode)

  val text = new PText(label)
  text.setFont(new PhetFont(16, true))
  addChild(text)

  updateTextNodeLocation()
  def updateTextNodeLocation() = {
    val viewDst = axisNode.getTipLocation
    text.setOffset(viewDst.x - text.getFullBounds.getWidth * 1.5, viewDst.y)
  }

  def setTipAndTailLocations(tip: Point2D, tail: Point2D) = {
    axisNode.setTipAndTailLocations(tip, tail)
    hitNode.setTipAndTailLocations(tip, tail)
  }
}
class AxisModel(private var _angle: Double, val length: Double, tail: Boolean) extends Observable with Rotatable {
  def angle = _angle

  def getEndPoint = new Vector2D(angle) * length

  def startPoint = if (tail) getEndPoint * -1 else new Vector2D

  def endPoint = getEndPoint

  def getUnitVector = (getEndPoint - startPoint).normalize

  def endPoint_=(newPt: Vector2D) = {
    angle = newPt.getAngle
    notifyListeners()
  }

  def angle_=(a: Double) = {
    if (this._angle != a) {
      this._angle = a
      notifyListeners()
    }
  }

  def dropped() = {}

  override def getPivot = new Vector2D

  def startPoint_=(newPt: Vector2D) = {}
}

//todo: coalesce with duplicates after code freeze
class ToggleListener(listener: PInputEventListener, isInteractive: => Boolean) extends PInputEventListener {
  def processEvent(aEvent: PInputEvent, t: Int) = {
    if (isInteractive) {
      listener.processEvent(aEvent, t)
    }
  }
}
class AxisNodeWithModel(transform: ModelViewTransform2D, label: String, val axisModel: AxisModel, isInteractive: => Boolean, minAngle: Double, maxAngle: Double)
        extends AxisNode(transform,
          transform.modelToViewDouble(axisModel.startPoint).x, transform.modelToViewDouble(axisModel.startPoint).y,
          transform.modelToViewDouble(axisModel.getEndPoint).x, transform.modelToViewDouble(axisModel.getEndPoint).y, label) {
  defineInvokeAndPass(axisModel.addListenerByName) {
    setTipAndTailLocations(transform.modelToViewDouble(axisModel.getEndPoint), transform.modelToViewDouble(axisModel.startPoint))
    updateTextNodeLocation()
  }
  hitNode.addInputEventListener(new ToggleListener(new CursorHandler, isInteractive))
  hitNode.addInputEventListener(new ToggleListener(new RotationHandler(transform, hitNode, axisModel, minAngle, maxAngle), isInteractive))
  hitNode.addInputEventListener(new ToggleListener(new PBasicInputEventHandler {
    override def mouseReleased(event: PInputEvent) = axisModel.dropped()
  }, isInteractive))
}

class FreeBodyDiagramNode(freeBodyDiagramModel: FreeBodyDiagramModel, private var _width: Double, private var _height: Double, val modelWidth: Double, val modelHeight: Double,
                          coordinateFrameModel: CoordinateFrameModel, isInteractive: => Boolean, toggleWindowedButton: Image, vectors: Vector*) extends PNode {
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

  val windowedButton = new PImage(toggleWindowedButton)
  windowedButton.addInputEventListener(new CursorHandler)
  windowedButton.addInputEventListener(new PBasicInputEventHandler {
    override def mousePressed(event: PInputEvent) = {
      freeBodyDiagramModel.windowed = !freeBodyDiagramModel.windowed
    }
  })

  val buttonPanel = new SwingLayoutNode()
  buttonPanel.addChild(windowedButton)
  buttonPanel.addChild(closeButton)
  addChild(buttonPanel)

  private val listeners = new ArrayBuffer[Point2D => Unit]

  def addListener(listener: Point2D => Unit) = {listeners += listener}

  override def setVisible(isVisible: Boolean) = {
    super.setVisible(isVisible)
    setPickable(isVisible)
    setChildrenPickable(isVisible)
  }

  background.addInputEventListener(new PBasicInputEventHandler {
    def sendEvent(event: PInputEvent) = {
      val viewPt = event.getPositionRelativeTo(background.getParent.getParent) //todo: do FreeBodyDiagramNode.this instead of background.getParent
      val modelPt = transform.viewToModel(viewPt)
      listeners.foreach(_(modelPt))
    }

    override def mouseDragged(event: PInputEvent) = sendEvent(event)

    override def mousePressed(event: PInputEvent) = sendEvent(event)

    override def mouseReleased(event: PInputEvent) = listeners.foreach(_(new Point2D.Double(0, 0)))
  })

  val xAxisModel = new SynchronizedAxisModel(0, modelWidth / 2 * 0.9, true, coordinateFrameModel)
  val yAxisModel = new SynchronizedAxisModel(PI / 2, modelWidth / 2 * 0.9, true, coordinateFrameModel)
  addChild(new AxisNodeWithModel(transform, "coordinates.x".translate, xAxisModel, isInteractive, 0, PI / 2))
  addChild(new AxisNodeWithModel(transform, "coordinates.y".translate, yAxisModel, isInteractive, PI / 2, PI))
  for (vector <- vectors) addVector(vector, RampDefaults.FBD_LABEL_MAX_OFFSET)

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
}

//todo: could improve performance by passing isContainerVisible:()=>Boolean and addContainerVisibleListener:(()=>Unit)=>Unit
class VectorNode(val transform: ModelViewTransform2D, val vector: Vector, val tailLocation: VectorValue, maxDistToLabel: Double) extends PNode {
  val arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(0, 1), 20, 20, 10, 0.5, true)
  arrowNode.setPaint(vector.getPaint)
  addChild(arrowNode)
  private val abbreviatonTextNode = {
    val html = new OutlineHTMLNode(vector.html, new PhetFont(22, true), vector.color, Color.black)
    //for performance, buffer these outlines; htmlnodes are very processor intensive, each outline is 5 htmlnodes and there are many per sim
    new PImage(html.toImage)
  }
  addChild(abbreviatonTextNode)

  //can't use def since eta-expansion makes == and array -= impossible
  //todo: see if def eta-expansion causes problems elsewhere
  val update = () => {
    setVisible(vector.visible)
    if (vector.visible) { //skip expensive updates if not visible
      val viewTip = transform.modelToViewDouble(vector.getValue + tailLocation.getValue)
      arrowNode.setTipAndTailLocations(viewTip, transform.modelToViewDouble(tailLocation.getValue))

      val proposedLocation = vector.getValue * 0.6
      val vectorToLabel = if (proposedLocation.magnitude > maxDistToLabel) new Vector2D(vector.getValue.getAngle) * maxDistToLabel else proposedLocation
      val textLocation = transform.modelToViewDouble(vectorToLabel + tailLocation.getValue)
      abbreviatonTextNode.setOffset(textLocation.x - abbreviatonTextNode.getFullBounds.getWidth / 2, textLocation.y - abbreviatonTextNode.getFullBounds.getHeight / 2)
      abbreviatonTextNode.setVisible(vectorToLabel.magnitude > 1E-2)
    }
  }
  update()
  vector.addListener(update)
  tailLocation.addListener(update)

  setPickable(false)
  setChildrenPickable(false)

  def deleting() {
    vector.removeListener(update)
    tailLocation.removeListener(update)
  }
}

object TestFBD extends Application {
  val frame = new JFrame
  val canvas = new PhetPCanvas
  val vector = new Vector(Color.blue, "Test Vector".literal, "Fv".literal, () => new Vector2D(5, 5), (a, b) => b)
  canvas.addScreenChild(new FreeBodyDiagramNode(new FreeBodyDiagramModel, 200, 200, 20, 20, new CoordinateFrameModel(Nil), true,
    PhetCommonResources.getImage("buttons/maximizeButton.png".literal), vector))
  frame.setContentPane(canvas)
  frame.setSize(800, 600)
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.setVisible(true)
}