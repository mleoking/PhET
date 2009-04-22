package edu.colorado.phet.therampscala.graphics

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.PhetFont
import common.piccolophet.event.CursorHandler
import common.piccolophet.nodes.{HTMLNode, PhetPPath, ArrowNode}
import common.piccolophet.PhetPCanvas
import java.awt.geom.{Point2D, Rectangle2D}
import java.awt.{Cursor, BasicStroke, Color}
import javax.swing.JFrame
import model.CoordinateFrameModel
import scalacommon.math.Vector2D
import scalacommon.util.Observable
import umd.cs.piccolo.nodes.{PText, PPath}
import umd.cs.piccolo.PNode
import scalacommon.Predef._
import java.lang.Math._

abstract class Vector(val color: Color, val name: String, val abbreviation: String) extends Observable {
  def getValue: Vector2D
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
class AxisModel(private var _angle: Double, val length: Double) extends Observable with Rotatable {
  def angle = _angle

  def getEndPoint = new Vector2D(angle) * length

  def startPoint = new Vector2D(0, 0)

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
}

class AxisNodeWithModel(transform: ModelViewTransform2D, label: String, val axisModel: AxisModel)
        extends AxisNode(transform, 0, 0, transform.modelToViewDouble(axisModel.getEndPoint).x, transform.modelToViewDouble(axisModel.getEndPoint).y, label) {
  defineInvokeAndPass(axisModel.addListenerByName) {
    axisNode.setTipAndTailLocations(transform.modelToViewDouble(axisModel.getEndPoint), transform.modelToViewDouble(0, 0))
    updateTextNodeLocation()
  }
  axisNode.addInputEventListener(new CursorHandler(Cursor.E_RESIZE_CURSOR))
  axisNode.addInputEventListener(new RotationHandler(transform, axisNode, axisModel, -1000, 1000))
}

class FreeBodyDiagramNode(val width: Int, val height: Int, val modelWidth: Double, val modelHeight: Double, coordinateFrameModel: CoordinateFrameModel, vectors: Vector*) extends PNode {
  val transformT = new ModelViewTransform2D(new Rectangle2D.Double(-modelWidth / 2, -modelHeight / 2, modelWidth, modelHeight),
    new Rectangle2D.Double(0, 0, width, height), true)
  val background = new PhetPPath(new Rectangle2D.Double(0, 0, width, height), Color.white, new BasicStroke(2), Color.darkGray)
  addChild(background)
  val arrowInset = 4

  val xAxisModel = new SynchronizedAxisModel(0, 7, coordinateFrameModel)
  val yAxisModel = new SynchronizedAxisModel(PI / 2, 7, coordinateFrameModel)
  addChild(new AxisNodeWithModel(transformT, "x", xAxisModel))
  addChild(new AxisNodeWithModel(transformT, "y", yAxisModel))
  for (vector <- vectors) addVector(vector)

  class VectorNode(val vector: Vector) extends PNode {
    val arrowNode = new ArrowNode(transformT.modelToViewDouble(0, 0), transformT.modelToViewDouble(vector.getValue), 20, 20, 10, 0.5, true)
    arrowNode.setPaint(vector.color)
    addChild(arrowNode)
    val abbreviatonTextNode = new HTMLNode(vector.abbreviation, vector.color)
    addChild(abbreviatonTextNode)
    defineInvokeAndPass(vector.addListenerByName) {
      val viewTipLoc = transformT.modelToViewDouble(vector.getValue)
      arrowNode.setTipAndTailLocations(viewTipLoc, transformT.modelToViewDouble(0, 0))
      abbreviatonTextNode.setOffset(viewTipLoc)
      abbreviatonTextNode.setVisible(vector.getValue.magnitude > 1E-2)
    }
  }
  def addVector(vector: Vector) = addChild(new VectorNode(vector))
}

object TestFBD extends Application {
  val frame = new JFrame
  val canvas = new PhetPCanvas
  canvas.addScreenChild(new FreeBodyDiagramNode(200, 200, 20, 20, new CoordinateFrameModel, new Vector(Color.blue, "Test Vector", "Fv") {
    def getValue = new Vector2D(5, 5)
  }))
  frame.setContentPane(canvas)
  frame.setSize(800, 600)
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.setVisible(true)
}