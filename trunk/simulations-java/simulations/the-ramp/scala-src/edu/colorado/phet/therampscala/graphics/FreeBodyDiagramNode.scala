package edu.colorado.phet.therampscala.graphics

import collection.mutable.ArrayBuffer
import common.phetcommon.resources.PhetCommonResources
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.PhetFont
import common.piccolophet.event.CursorHandler
import common.piccolophet.nodes._
import common.piccolophet.PhetPCanvas
import java.awt._
import image.BufferedImage
import java.awt.geom.{Point2D, Rectangle2D}
import javax.swing.JFrame
import layout.SwingLayoutNode
import model.CoordinateFrameModel
import scalacommon.math.Vector2D
import scalacommon.util.Observable
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEventListener, PInputEvent}
import umd.cs.piccolo.nodes.{PImage, PText, PPath}
import umd.cs.piccolo.PNode
import scalacommon.Predef._
import java.lang.Math._

class Vector(val color: Color, val name: String, val abbreviation: String, val valueAccessor: () => Vector2D, val painter: (Vector2D, Color) => Paint) extends Observable with VectorValue {
  private var _visible = true

  def getValue = valueAccessor()

  def visible = _visible

  def visible_=(vis: Boolean) = {
    _visible = vis
    notifyListeners()
  }

  def getPaint = painter(getValue, color)
}

class AxisNode(val transform: ModelViewTransform2D, x0: Double, y0: Double, x1: Double, y1: Double, label: String) extends PNode {
  val axisNode = new ArrowNode(transform.modelToViewDouble(x0, y0), transform.modelToViewDouble(x1, y1), 5, 5, 2)
  axisNode.setStroke(null)
  axisNode.setPaint(Color.black)
  addChild(axisNode)
  val text = new PText(label)
  text.setFont(new PhetFont(16, true))
  addChild(text)

  updateTextNodeLocation()
  def updateTextNodeLocation() = {
    val viewDst = axisNode.getTipLocation
    text.setOffset(viewDst.x - text.getFullBounds.getWidth * 1.5, viewDst.y)
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
class AxisNodeWithModel(transform: ModelViewTransform2D, label: String, val axisModel: AxisModel, isInteractive: => Boolean)
        extends AxisNode(transform,
          transform.modelToViewDouble(axisModel.startPoint).x, transform.modelToViewDouble(axisModel.startPoint).y,
          transform.modelToViewDouble(axisModel.getEndPoint).x, transform.modelToViewDouble(axisModel.getEndPoint).y, label) {
  defineInvokeAndPass(axisModel.addListenerByName) {
    axisNode.setTipAndTailLocations(transform.modelToViewDouble(axisModel.getEndPoint), transform.modelToViewDouble(axisModel.startPoint))
    updateTextNodeLocation()
  }
  axisNode.addInputEventListener(new ToggleListener(new CursorHandler(Cursor.E_RESIZE_CURSOR), isInteractive))
  axisNode.addInputEventListener(new ToggleListener(new RotationHandler(transform, axisNode, axisModel, -1000, 1000), isInteractive))
  axisNode.addInputEventListener(new ToggleListener(new PBasicInputEventHandler {
    override def mouseReleased(event: PInputEvent) = axisModel.dropped()
  }, isInteractive))
}

class FreeBodyDiagramNode(freeBodyDiagramModel: FreeBodyDiagramModel, private var _width: Double, private var _height: Double, val modelWidth: Double, val modelHeight: Double,
                          coordinateFrameModel: CoordinateFrameModel, isInteractive: => Boolean, toggleWindowedButton: Image, vectors: Vector*) extends PNode {
  val transform = new ModelViewTransform2D(new Rectangle2D.Double(-modelWidth / 2, -modelHeight / 2, modelWidth, modelHeight),
    new Rectangle2D.Double(0, 0, _width, _height), true)

  val background = new PhetPPath(Color.white, new BasicStroke(2), Color.darkGray)
  addChild(background)

  val closeButton = new PImage(PhetCommonResources.getImage("buttons/closeButton.png"))
  closeButton.addInputEventListener(new CursorHandler)
  closeButton.addInputEventListener(new PBasicInputEventHandler {
    override def mousePressed(event: PInputEvent) = {
      freeBodyDiagramModel.visible = false
    }
  })

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
  addChild(new AxisNodeWithModel(transform, "x", xAxisModel, isInteractive))
  addChild(new AxisNodeWithModel(transform, "y", yAxisModel, isInteractive))
  for (vector <- vectors) addVector(vector)

  updateSize()

  def addVector(vector: Vector): Unit = addVector(vector, new ConstantVectorValue)

  def addVector(vector: Vector, offset: VectorValue) = addChild(new VectorNode(transform, vector, offset))

  def clearVectors() = {
    val removeList = new ArrayBuffer[PNode]
    for (i <- 0 until getChildrenCount) {
      getChild(i) match {
        case x: VectorNode => removeList += x
        case _ => {}
      }
    }
    for (node <- removeList) {
      removeChild(node)
    }
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

  def addListenerByName(listener: => Unit) = {}
}

trait VectorValue {
  def getValue: Vector2D

  def addListenerByName(listener: => Unit): Unit
}

class VectorNode(val transform: ModelViewTransform2D, val vector: Vector, val tailLocation: VectorValue) extends PNode {
  val arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(0, 1), 20, 20, 10, 0.5, true)
  arrowNode.setPaint(vector.getPaint)
  addChild(arrowNode)
  val abbreviatonTextNode = new ShadowHTMLNode(vector.abbreviation, vector.color)
  abbreviatonTextNode.setFont(new PhetFont(18))
  addChild(abbreviatonTextNode)

  def update() = {
    val viewTip = transform.modelToViewDouble(vector.getValue + tailLocation.getValue)
    arrowNode.setTipAndTailLocations(viewTip, transform.modelToViewDouble(tailLocation.getValue))
    abbreviatonTextNode.setOffset(viewTip)
    abbreviatonTextNode.setVisible(vector.getValue.magnitude > 1E-2)
    setVisible(vector.visible)
  }
  update()
  vector.addListenerByName(update())
  tailLocation.addListenerByName(update())

  setPickable(false)
  setChildrenPickable(false)

  override def setVisible(isVisible: Boolean) = {
    super.setVisible(isVisible)
  }
}

object TestFBD extends Application {
  val frame = new JFrame
  val canvas = new PhetPCanvas
  val vector = new Vector(Color.blue, "Test Vector", "Fv", () => new Vector2D(5, 5), (a, b) => b)
  canvas.addScreenChild(new FreeBodyDiagramNode(new FreeBodyDiagramModel, 200, 200, 20, 20, new CoordinateFrameModel(Nil), true,
    PhetCommonResources.getImage("buttons/maximizeButton.png"), vector))
  frame.setContentPane(canvas)
  frame.setSize(800, 600)
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.setVisible(true)
}