package edu.colorado.phet.cavendishexperiment


import collection.mutable.ArrayBuffer
import common.phetcommon.application.{PhetApplicationConfig, PhetApplicationLauncher, Module}
import common.piccolophet.PiccoloPhetApplication
import java.awt.event.{ComponentAdapter, ComponentEvent}
import java.awt.geom.{Ellipse2D, Rectangle2D, Point2D}
import java.text.{DecimalFormat, NumberFormat}
import scalacommon.ScalaClock
import common.phetcommon.view.ControlPanel
import scalacommon.util.Observable
import common.phetcommon.view.util.{DoubleGeneralPath, PhetFont}
import common.piccolophet.nodes.{PhetPPath, RulerNode, ArrowNode, SphericalNode}
import java.awt.{BasicStroke, Font, Color}
import common.phetcommon.view.graphics.RoundGradientPaint
import umd.cs.piccolo.nodes.{PImage, PText}
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.util.PDimension
import common.piccolophet.event.CursorHandler
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import umd.cs.piccolo.PNode

import scalacommon.math.Vector2D

class ForceLabelNode(mass: Mass, transform: ModelViewTransform2D, model: CavendishExperimentModel, color: Color, scale: Double, format: NumberFormat) extends PNode {
  val arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(1, 1), 20, 20, 8, 0.5, true)
  arrowNode.setPaint(color)
  val label = new PText
  label.setTextPaint(color)
  label.setFont(new PhetFont(18, true))

  defineInvokeAndPass(model.addListenerByName) {
    label.setOffset(transform.modelToView(mass.position) - new Vector2D(0, label.getFullBounds.getHeight + 100))
    label.setText("Force on " + model.m1.name + " by " + model.m2.name + " = " + format.format(model.getForce.magnitude) + " N")
    val tip = label.getOffset + new Vector2D(model.getForce.magnitude * scale, -20)
    val tail = label.getOffset + new Vector2D(0, -20)
    arrowNode.setTipAndTailLocations(tip, tail)
  }

  addChild(arrowNode)
  addChild(label)
}
class MassNode(mass: Mass, transform: ModelViewTransform2D, color: Color) extends PNode {
  val image = new SphericalNode(mass.radius * 2, color, false)

  val label = new PText(mass.name)
  label.setTextPaint(Color.white)
  label.setFont(new PhetFont(16, true))

  defineInvokeAndPass(mass.addListenerByName) {
    image.setOffset(transform.modelToView(mass.position))
    val viewRadius = transform.modelToViewDifferentialXDouble(mass.radius)
    image.setDiameter(viewRadius * 2)
    image.setPaint(new RoundGradientPaint(viewRadius, -viewRadius, Color.WHITE,
      new Point2D.Double(-viewRadius, viewRadius), color))
    label.setOffset(transform.modelToView(mass.position) - new Vector2D(label.getFullBounds.getWidth / 2, label.getFullBounds.getHeight / 2))

    //    println("updated mass node, radius=" + mass.radius + ", viewRadius=" + viewRadius + ", globalfullbounds=" + image.getGlobalFullBounds)
  }

  addChild(image)
  addChild(label)
}
class DraggableMassNode(mass: Mass, transform: ModelViewTransform2D, color: Color) extends MassNode(mass, transform, color) {
  var dragging = false
  var initialDrag = false //don't show a pushpin on startup
  val pushPinNode = new PImage(CavendishExperimentResources.getImage("push-pin.png"))
  addChild(pushPinNode)
  pushPinNode.setPickable(false)
  pushPinNode.setChildrenPickable(false)
  mass.addListenerByName(
    pushPinNode.setOffset(transform.modelToView(mass.position) - new Vector2D(pushPinNode.getFullBounds.getWidth * 0.8, pushPinNode.getFullBounds.getHeight * 0.8))
    )

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
  def draggingChanged() = pushPinNode.setVisible(initialDrag && !dragging)
}

class CavendishExperimentCanvas(model: CavendishExperimentModel, modelWidth: Double, mass1Color: Color, mass2Color: Color, backgroundColor: Color,
                                rulerLength: Long, numTicks: Long, rulerLabel: String, tickToString: Long => String,
                                forceLabelScale: Double, forceArrowNumberFormat: NumberFormat) extends DefaultCanvas(modelWidth, modelWidth) {
  setBackground(backgroundColor)

  val tickIncrement = rulerLength / numTicks

  val ticks = new ArrayBuffer[Long]
  private var _x = 0L
  while (_x <= rulerLength) { //no high level support for 1L to 5L => Range[Long]
    ticks += _x
    _x += tickIncrement
  }

  val rulerNode = {
    val maj = for (i <- ticks) yield tickToString(i)
    val dx = transform.modelToViewDifferentialX(rulerLength)
    new RulerNode(dx, 14, 40, maj.toArray, new PhetFont(Font.PLAIN, 14), rulerLabel, new PhetFont(Font.PLAIN, 10), 4, 10, 6);
  }
  rulerNode.setOffset(150, 500)

  def opposite(c: Color) = new Color(255 - c.getRed, 255 - c.getGreen, 255 - c.getBlue)
  addNode(new MassNode(model.m1, transform, mass1Color))
  addNode(new SpringNode(model, transform, opposite(backgroundColor)))
  addNode(new DraggableMassNode(model.m2, transform, mass2Color))
  addNode(new ForceLabelNode(model.m1, transform, model, opposite(backgroundColor), forceLabelScale, forceArrowNumberFormat))
  addNode(rulerNode)
  rulerNode.addInputEventListener(new PBasicInputEventHandler {
    override def mouseDragged(event: PInputEvent) = {
      rulerNode.translate(event.getDeltaRelativeTo(rulerNode.getParent).width, event.getDeltaRelativeTo(rulerNode.getParent).height)
    }
  })
  rulerNode.addInputEventListener(new CursorHandler)

  addNode(new WallNode(model.wall, transform, opposite(backgroundColor)))
}

class MyDoubleGeneralPath(pt: Point2D) extends DoubleGeneralPath(pt) {
  def curveTo(control1: Vector2D, control2: Vector2D, dest: Vector2D) = super.curveTo(control1.x, control1.y, control2.x, control2.y, dest.x, dest.y)
}

class SpringNode(model: CavendishExperimentModel, transform: ModelViewTransform2D, color: Color) extends PNode {
  val path = new PhetPPath(new BasicStroke(2), color)
  addChild(path)
  defineInvokeAndPass(model.addListenerByName) {
    val startPt = transform.modelToView(model.wall.maxX, 0)
    val endPt = transform.modelToView(model.m1.position)
    val unitVector = (startPt - endPt).normalize
    val distance = (startPt - endPt).magnitude
    val p = new MyDoubleGeneralPath(startPt)
    val springCurveHeight = 60
    p.lineTo(startPt + new Vector2D(distance / 6, 0))
    for (i <- 1 to 5) {
      p.curveTo(p.getCurrentPoint + new Vector2D(0, -springCurveHeight), p.getCurrentPoint + new Vector2D(distance / 6, -springCurveHeight), p.getCurrentPoint + new Vector2D(distance / 6, 0))
      p.curveTo(p.getCurrentPoint + new Vector2D(0, springCurveHeight), p.getCurrentPoint + new Vector2D(-distance / 12, springCurveHeight), p.getCurrentPoint + new Vector2D(-distance / 12, 0))
    }
    p.lineTo(endPt - new Vector2D(transform.modelToViewDifferentialXDouble(model.m1.radius), 0))

    path.setPathTo(p.getGeneralPath)
  }
}
class Wall(width: Double, height: Double, _maxX: Double) {
  def getShape = new Rectangle2D.Double(-width + _maxX, -height / 2, width, height)

  def maxX: Double = getShape.getBounds2D.getMaxX
}
class WallNode(wall: Wall, transform: ModelViewTransform2D, color: Color) extends PNode {
  val wallPath = new PhetPPath(transform.createTransformedShape(wall.getShape), color, new BasicStroke(2f), Color.gray)
  addChild(wallPath)
  //  println("wallpathbounds=" + wallPath.getGlobalFullBounds)
}
class CavendishExperimentControlPanel(model: CavendishExperimentModel) extends ControlPanel {
  add(new ScalaValueControl(0.01, 100, "m1", "0.00", "kg", model.m1.mass, model.m1.mass = _, model.m1.addListener))
  add(new ScalaValueControl(0.01, 100, "m2", "0.00", "kg", model.m2.mass, model.m2.mass = _, model.m2.addListener))
}
class Mass(private var _mass: Double, private var _position: Vector2D, val name: String, massToRadius: Double => Double) extends Observable {
  def mass = _mass

  def mass_=(m: Double) = {_mass = m; notifyListeners()}

  def position = _position

  def position_=(p: Vector2D) = {_position = p; notifyListeners()}

  def radius = massToRadius(_mass)
}
class Spring(val k: Double, val restingLength: Double)
class CavendishExperimentModel(mass1: Double, mass2: Double,
                               mass1Position: Double, mass2Position: Double,
                               mass1Radius: Double => Double, mass2Radius: Double => Double,
                               k: Double, springRestingLength: Double,
                               wallWidth: Double, wallHeight: Double, wallMaxX: Double,
                               mass1Name: String, mass2Name: String
        ) extends Observable {
  val wall = new Wall(wallWidth, wallHeight, wallMaxX)
  val m1 = new Mass(mass1, new Vector2D(mass1Position, 0), mass1Name, mass1Radius)
  val m2 = new Mass(mass2, new Vector2D(mass2Position, 0), mass2Name, mass2Radius)
  val spring = new Spring(k, springRestingLength)
  val G = 6.67E-11
  m1.addListenerByName(notifyListeners())
  m2.addListenerByName(notifyListeners())

  //causes dynamical problems, e.g. oscillations if you use the full model
  def rFake = m2.position - new Vector2D(wall.maxX + spring.restingLength, 0)

  def rFull = m1.position - m2.position

  def r = rFull

  def rMin = if (m1.position.x + m1.radius < m2.position.x - m2.radius) r else m2.position - new Vector2D(m2.radius, 0)

  def getForce = r * G * m1.mass * m2.mass / pow(r.magnitude, 3)

  def update(dt: Double) = {
    //    println("force magnitude=" + getForce.magnitude + ", from r=" + r + ", m1.x=" + m1.position.x + ", m2.position.x=" + m2.position.x)
    val xDesired = wall.maxX + spring.restingLength + getForce.magnitude / spring.k
    val x = if (xDesired + m1.radius > m2.position.x - m2.radius)
      m2.position.x - m2.radius - m1.radius
    else
      xDesired
    m1.position = new Vector2D(x, 0)
  }
}
class CavendishExperimentModule(clock: ScalaClock) extends Module("Cavendish Experiment", clock) {
  val model = new CavendishExperimentModel(10, 25, 0, 1, mass => mass / 30, mass => mass / 30, 1E-8, 1, 50, 50, -4, "m1", "m2")
  val canvas = new CavendishExperimentCanvas(model, 10, Color.blue, Color.blue, Color.white, 5, 5, "m", _.toString, 1E10, new DecimalFormat("0.000000000000"))
  setSimulationPanel(canvas)
  clock.addClockListener(model.update(_))
  setControlPanel(new CavendishExperimentControlPanel(model))
  setClockControlPanel(null)
}

object CavendishExperimentDefaults {
  val sunEarthDist = 1.496E11 //  sun earth distace in m
  val earthRadius = 6.371E6
  val sunRadius = 6.955E8
}

class SolarCavendishModule(clock: ScalaClock) extends Module("Sun-Planet System", clock) {
  import CavendishExperimentDefaults._
  val model = new CavendishExperimentModel(5.9742E24, //earth mass in kg
    1.9891E30, // sun mass in kg
    -sunEarthDist / 2,
    sunEarthDist / 2,
    mass => earthRadius * 1.6E3, //latter term is a fudge factor to make things visible on the same scale
    mass => sunRadius * 5E1,
//    1.5E14, sunEarthDist / 2, // this version puts the spring resting length so that default position is distEarthSun
//    0.98E12, sunEarthDist / 4,  //this requires the sun to tug on the earth to put it in the right spot
    1.42E12, sunEarthDist / 3,  //this one too
    1E13,
    1E12,
    -sunEarthDist, "Earth", "Sun"
    )

  def metersToLightMinutes(a: Double) = a * 5.5594E-11

  val canvas = new CavendishExperimentCanvas(model, sunEarthDist * 2.05, Color.blue, Color.red, Color.black,
    CavendishExperimentDefaults.sunEarthDist.toLong, 4, "light minutes", dist => {
      new DecimalFormat("0.0").format(metersToLightMinutes(dist.toDouble))
    }, 3.2E-22 * 10, new DecimalFormat("0.0"))
  val disclaimerNode = new ScaleDisclaimerNode(model, canvas.transform)
  canvas.addComponentListener(new ComponentAdapter() {
    override def componentResized(e: ComponentEvent) = updateDisclaimerLocation()
  })
  updateDisclaimerLocation()
  def updateDisclaimerLocation() = disclaimerNode.setOffset(canvas.canonicalBounds.width / 2 - disclaimerNode.getFullBounds.getWidth / 2, canvas.canonicalBounds.height- disclaimerNode.getFullBounds.getHeight)
  canvas.addNode(disclaimerNode)
  setSimulationPanel(canvas)
  clock.addClockListener(model.update(_))
  setClockControlPanel(null)
}

class Circle(center: Vector2D, radius: Double) extends Ellipse2D.Double(center.x - radius, center.y - radius, radius * 2, radius * 2)
class ScaleDisclaimerNode(model: CavendishExperimentModel, transform: ModelViewTransform2D) extends PNode {
  val text = new PText("* Radii not to scale.  If they were to scale they'd be this big:")
  text.setFont(new PhetFont(18))
  text.setTextPaint(Color.lightGray)
  addChild(text)
  import CavendishExperimentDefaults._
  val m1Icon = new PhetPPath(new Circle(new Vector2D, transform.modelToViewDifferentialXDouble(earthRadius)), Color.blue)
  val m2Icon = new PhetPPath(new Circle(new Vector2D, transform.modelToViewDifferentialXDouble(sunRadius)), Color.red)

  addChild(m1Icon)
  addChild(m2Icon)
  m1Icon.setOffset(text.getFullBounds.getMaxX + 5, text.getFullBounds.getCenterY - m1Icon.getFullBounds.getHeight / 2)
  m2Icon.setOffset(m1Icon.getFullBounds.getMaxX + 5, text.getFullBounds.getCenterY - m2Icon.getFullBounds.getHeight / 2)

}

class CavendishExperimentApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new CavendishExperimentModule(new ScalaClock(30, 30 / 1000.0)))
  addModule(new SolarCavendishModule(new ScalaClock(30, 30 / 1000.0)))
}

object CavendishExperimentApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "cavendish-experiment", classOf[CavendishExperimentApplication])
}