package edu.colorado.phet.cavendishexperiment


import common.phetcommon.application.Module
import common.phetcommon.view.controls.valuecontrol.LinearValueControl
import common.phetcommon.view.graphics.RoundGradientPaint
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.{DoubleGeneralPath, PhetFont}
import common.phetcommon.view.{ControlPanel, VerticalLayoutPanel}
import common.piccolophet.nodes.{PhetPPath, ArrowNode, SphericalNode, RulerNode}
import java.awt._
import java.awt.geom.{Point2D, Rectangle2D}
import common.piccolophet.event.CursorHandler
import common.piccolophet.PhetPCanvas
import java.text.DecimalFormat
import javax.swing.event.{ChangeListener, ChangeEvent}

import scalacommon.math.Vector2D
import scalacommon.util.Observable
import scalacommon.{CenteredBoxStrategy, ScalaApplicationLauncher, ScalaClock}
import umd.cs.piccolo.event.{PInputEvent, PBasicInputEventHandler}
import umd.cs.piccolo.nodes.{PImage, PText}
import umd.cs.piccolo.PNode
import scalacommon.Predef._
import umd.cs.piccolo.util.PDimension
import java.lang.Math._

//TODO: move to scalacommon
class ScalaValueControl(min: Double, max: Double, name: String, decimalFormat: String, units: String,
                        getter: => Double, setter: Double => Unit, addListener: (() => Unit) => Unit) extends LinearValueControl(min, max, name, decimalFormat, units) {
  addListener(update)
  update()
  addChangeListener(new ChangeListener {
    def stateChanged(e: ChangeEvent) = setter(getValue)
  });
  def update() = setValue(getter)
}
class ForceLabelNode(mass: Mass, transform: ModelViewTransform2D, model: CavendishExperimentModel) extends PNode {
  val arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(1, 1), 20, 20, 8, 0.5, true)
  addChild(arrowNode)
  val label = new PText
  label.setFont(new PhetFont(18, true))

  defineInvokeAndPass(model.addListenerByName) {
    label.setOffset(transform.modelToView(mass.position) - new Vector2D(0, label.getFullBounds.getHeight + 100))
    label.setText("Force on m1 = " + new DecimalFormat("0.000000000000").format(model.getForce.magnitude) + " N")
    val scale = 1E10
    arrowNode.setTipAndTailLocations(label.getOffset + new Vector2D(model.getForce.magnitude * scale, -20), label.getOffset + new Vector2D(0, -20))
  }

  addChild(label)
}
class MassNode(mass: Mass, transform: ModelViewTransform2D) extends PNode {
  val image = new SphericalNode(mass.radius * 2, Color.blue, false)

  val label = new PText(mass.name)
  label.setTextPaint(Color.white)
  label.setFont(new PhetFont(16, true))

  defineInvokeAndPass(mass.addListenerByName) {
    image.setOffset(transform.modelToView(mass.position))
    val viewRadius = transform.modelToViewDifferentialXDouble(mass.radius)
    image.setDiameter(viewRadius * 2)
    image.setPaint(new RoundGradientPaint(viewRadius, -viewRadius, Color.WHITE,
      new Point2D.Double(-viewRadius, viewRadius), Color.blue))
    label.setOffset(transform.modelToView(mass.position) - new Vector2D(label.getFullBounds.getWidth / 2, label.getFullBounds.getHeight / 2))
  }

  addChild(image)
  addChild(label)
}
class DraggableMassNode(mass: Mass, transform: ModelViewTransform2D) extends MassNode(mass, transform) {
  var dragging = false
  var initialDrag = false //don't show a pushpin on startup
  val pushPinNode = new PImage(CavendishExperimentResources.getImage("push-pin.png"))
  addChild(pushPinNode)
  pushPinNode.setPickable(false)
  pushPinNode.setChildrenPickable(false)
  mass.addListenerByName(
    {
      pushPinNode.setOffset(transform.modelToView(mass.position) - new Vector2D(pushPinNode.getFullBounds.getWidth * 0.8, pushPinNode.getFullBounds.getHeight * 0.8))
    })

  addInputEventListener(new PBasicInputEventHandler {
    override def mouseDragged(event: PInputEvent) = {
      implicit def pdimensionToPoint2D(dim: PDimension) = new Point2D.Double(dim.width, dim.height)
      mass.position = mass.position + new Vector2D(transform.viewToModelDifferential(event.getDeltaRelativeTo(DraggableMassNode.this.getParent)).x, 0)
    }

    override def mousePressed(event: PInputEvent) = {
      dragging = true
      initialDrag = true
      draggingChanged()
    }

    override def mouseReleased(event: PInputEvent) = {
      dragging = false
      draggingChanged()
    }
  })
  addInputEventListener(new CursorHandler)

  draggingChanged()
  def draggingChanged() = {
    pushPinNode.setVisible(initialDrag && !dragging)
  }
}

class CavendishExperimentCanvas(model: CavendishExperimentModel) extends DefaultCanvas(10, 10) {
  def newRulerNode() = {
    val maj = for (i <- 0 to 5) yield "" + i
    val dx = transform.modelToViewDifferentialX(5)
    new RulerNode(dx, 14, 40, maj.toArray, new PhetFont(Font.PLAIN, 14), "m", new PhetFont(Font.PLAIN, 10), 4, 10, 6);
  }

  val rulerNode = newRulerNode()
  rulerNode.setOffset(150, 500)
  addNode(new SpringNode(model, transform))
  addNode(new MassNode(model.m1, transform))
  addNode(new DraggableMassNode(model.m2, transform))
  addNode(new ForceLabelNode(model.m1, transform, model))
  addNode(rulerNode)
  rulerNode.addInputEventListener(new PBasicInputEventHandler {
    override def mouseDragged(event: PInputEvent) = {
      rulerNode.translate(event.getDeltaRelativeTo(rulerNode.getParent).width, event.getDeltaRelativeTo(rulerNode.getParent).height)
    }
  })
  rulerNode.addInputEventListener(new CursorHandler)

  addNode(new WallNode(model.wall, transform))

}
class SpringNode(model: CavendishExperimentModel, transform: ModelViewTransform2D) extends PNode {
  val path = new PhetPPath(new BasicStroke(2), Color.black)
  addChild(path)
  defineInvokeAndPass(model.addListenerByName) {
    val startPt = transform.modelToView(model.wall.maxX, 0)
    val endPt = transform.modelToView(model.m1.position)
    val unitVector = (startPt - endPt).normalize
    val distance = (startPt - endPt).magnitude
    val p = new DoubleGeneralPath(startPt) {
      def curveTo(control1: Vector2D, control2: Vector2D, dest: Vector2D) = super.curveTo(control1.x, control1.y, control2.x, control2.y, dest.x, dest.y)
    }

    val springCurveHeight = 60
    p.lineTo(startPt + new Vector2D(distance / 6, 0))
    for (i <- 1 to 5) {
      p.curveTo(p.getCurrentPoint + new Vector2D(0, -springCurveHeight), p.getCurrentPoint + new Vector2D(distance / 6, -springCurveHeight), p.getCurrentPoint + new Vector2D(distance / 6, 0))
      p.curveTo(p.getCurrentPoint + new Vector2D(0, springCurveHeight), p.getCurrentPoint + new Vector2D(-distance / 12, springCurveHeight), p.getCurrentPoint + new Vector2D(-distance / 12, 0))
    }
    p.lineTo(endPt)

    path.setPathTo(p.getGeneralPath)
  }
}
class Wall {
  def getShape = new Rectangle2D.Double(-50, -50, 46, 100)

  def maxX = getShape.getBounds2D.getMaxX
}
class WallNode(wall: Wall, transform: ModelViewTransform2D) extends PNode {
  addChild(new PhetPPath(transform.createTransformedShape(wall.getShape), Color.black, new BasicStroke(2f), Color.gray))
}
class CavendishExperimentControlPanel(model: CavendishExperimentModel) extends ControlPanel {
  add(new ScalaValueControl(0.01, 100, "m1", "0.00", "kg", model.m1.mass, model.m1.mass = _, model.m1.addListener))
  add(new ScalaValueControl(0.01, 100, "m2", "0.00", "kg", model.m2.mass, model.m2.mass = _, model.m2.addListener))
}
class Mass(private var _mass: Double, private var _position: Vector2D, val name: String) extends Observable {
  def mass = _mass

  def mass_=(m: Double) = {_mass = m; notifyListeners()}

  def position = _position

  def position_=(p: Vector2D) = {_position = p; notifyListeners()}

  def radius = mass / 30
}
class Spring {
  val k = 1E-8
  val restingLength = 1
}
class CavendishExperimentModel extends Observable {
  val wall = new Wall
  val m1 = new Mass(10, new Vector2D(0, 0), "m1")
  val m2 = new Mass(25, new Vector2D(1, 0), "m2")
  val spring = new Spring
  m1.addListenerByName(notifyListeners)
  m2.addListenerByName(notifyListeners)

  //causes dynamical problems, e.g. oscillations if you use the full model
  def rFake = m2.position - new Vector2D(wall.maxX + spring.restingLength, 0)

  def rFull = m1.position - m2.position

  def r = rFull

  val G = 6.67E-11

  def rMin = if (m1.position.x + m1.radius < m2.position.x - m2.radius) r else m2.position - new Vector2D(m2.radius, 0)

  def getForce = r * G * m1.mass * m2.mass / pow(r.magnitude, 3)

  def update(dt: Double) = {
    val xDesired = wall.maxX + spring.restingLength + getForce.magnitude / spring.k
    val x = if (xDesired + m1.radius > m2.position.x - m2.radius) {
      //      println("xd=" + xD + ", m1.rad=" + m1.radius + ", m2.x=" + m2.position.x + ", m2.rad=" + m2.radius)
      m2.position.x - m2.radius - m1.radius
    } else xDesired
    m1.position = new Vector2D(x, 0)
  }
}
class CavendishExperimentModule(clock: ScalaClock) extends Module("Cavendish Experiment", clock) {
  val model = new CavendishExperimentModel
  val canvas = new CavendishExperimentCanvas(model)
  setSimulationPanel(canvas)
  clock.addClockListener(model.update(_))
  setControlPanel(new CavendishExperimentControlPanel(model))
  setClockControlPanel(null)
}

object CavendishExperimentApplication {
  def main(args: Array[String]) = {
    ScalaApplicationLauncher.launchApplication(args, "cavendish-experiment", "cavendish-experiment", () => new CavendishExperimentModule(new ScalaClock(30, 30 / 1000.0)))
  }
}