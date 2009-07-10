package edu.colorado.phet.forcelawlab


import collection.mutable.ArrayBuffer
import common.phetcommon.application.{PhetApplicationConfig, PhetApplicationLauncher, Module}
import common.phetcommon.math.MathUtil
import common.phetcommon.view.util.{SwingUtils, DoubleGeneralPath, PhetFont}
import common.phetcommon.view.{PhetFrame, VerticalLayoutPanel, ControlPanel}
import common.piccolophet.nodes.layout.SwingLayoutNode
import common.piccolophet.PiccoloPhetApplication
import common.piccolophet.nodes.{PhetPPath, RulerNode, ArrowNode, SphericalNode}
import common.phetcommon.view.graphics.RoundGradientPaint
import common.piccolophet.event.CursorHandler
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import java.awt._
import event.{MouseAdapter, MouseEvent}

import java.text._
import javax.swing.{JTextArea, JDialog, BorderFactory}
import scalacommon.swing.{MyJButton, MyRadioButton}
import umd.cs.piccolo.nodes.{PImage, PText}
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.util.PDimension

import umd.cs.piccolo.PNode
import java.awt.geom.{Ellipse2D, Rectangle2D, Point2D}
import scalacommon.math.Vector2D
import scalacommon.Predef._
import scalacommon.ScalaClock
import scalacommon.util.Observable
import java.lang.Math._

class ForceLabelNode(target: Mass, source: Mass, transform: ModelViewTransform2D, model: ForceLawLabModel,
                     color: Color, scale: Double, format: NumberFormat, offsetY: Double, right: Boolean, wall: Wall) extends PNode {
  val arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(1, 1), 20, 20, 8, 0.5, true)
  arrowNode.setPaint(color)
  val label = new PText
  label.setTextPaint(color)
  label.setFont(new PhetFont(18, true))

  defineInvokeAndPass(model.addListenerByName) {
    label.setOffset(transform.modelToView(target.position) - new Vector2D(0, label.getFullBounds.getHeight + offsetY))
    val str = MessageFormat.format(ForceLawLabResources.getLocalizedString("force-description-pattern-target_source_value"), target.name, source.name, format.format(model.getGravityForce.magnitude))
    label.setText(str)
    val sign = if (right) 1 else -1
    val tip = label.getOffset + new Vector2D(sign * model.getGravityForce.magnitude * scale, -20)
    val tail = label.getOffset + new Vector2D(0, -20)
    arrowNode.setTipAndTailLocations(tip, tail)
    if (!right)
      label.translate(-label.getFullBounds.getWidth, 0)
    val wallViewShape = transform.modelToView(wall.getShape)
    label.setOffset(max(wallViewShape.getMaxX + 5, label.getOffset.getX), label.getOffset.getY)
  }

  addChild(arrowNode)
  addChild(label)
}
class MassNode(mass: Mass, transform: ModelViewTransform2D, color: Color, magnification: Magnification) extends PNode {
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
    if (image.getFullBounds.getHeight < label.getFullBounds.getHeight)
      label.translate(0, -label.getFullBounds.getHeight * 1.2) //gets notification from mass

    //    println("updated mass node, radius=" + mass.radius + ", viewRadius=" + viewRadius + ", globalfullbounds=" + image.getGlobalFullBounds)
  }

  addChild(image)
  addChild(label)
}
class DraggableMassNode(mass: Mass, transform: ModelViewTransform2D, color: Color, minDragX: Double, maxDragX: () => Double, magnification: Magnification) extends MassNode(mass, transform, color, magnification) {
  var dragging = false
  var initialDrag = false //don't show a pushpin on startup
  val pushPinNode = new PImage(ForceLawLabResources.getImage("push-pin.png"))
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
      if (mass.position.x < minDragX)
        mass.position = new Vector2D(minDragX, 0)
      if (mass.position.x > maxDragX())
        mass.position = new Vector2D(maxDragX(), 0)
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

class ForceLawLabCanvas(model: ForceLawLabModel, modelWidth: Double, mass1Color: Color, mass2Color: Color, backgroundColor: Color,
                        rulerLength: Long, numTicks: Long, rulerLabel: String, tickToString: Long => String,
                        forceLabelScale: Double, forceArrowNumberFormat: NumberFormat, magnification: Magnification) extends DefaultCanvas(modelWidth, modelWidth) {
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
    new RulerNode(dx, 14, 40, maj.toArray, new PhetFont(Font.BOLD, 13), rulerLabel, new PhetFont(Font.BOLD, 13), 4, 10, 6);
  }
  rulerNode.setOffset(150, 500)

  def updateRulerVisible() = rulerNode.setVisible(!magnification.magnified)
  magnification.addListenerByName(updateRulerVisible())
  updateRulerVisible()

  def opposite(c: Color) = new Color(255 - c.getRed, 255 - c.getGreen, 255 - c.getBlue)
  addNode(new MassNode(model.m1, transform, mass1Color, magnification))
  addNode(new SpringNode(model, transform, opposite(backgroundColor)))
  addNode(new DraggableMassNode(model.m2, transform, mass2Color, model.wall.maxX, () => transform.viewToModelX(getVisibleModelBounds.getMaxX), magnification))
  addNode(new ForceLabelNode(model.m1, model.m2, transform, model, opposite(backgroundColor), forceLabelScale, forceArrowNumberFormat, 100, true, model.wall))
  addNode(new ForceLabelNode(model.m2, model.m1, transform, model, opposite(backgroundColor), forceLabelScale, forceArrowNumberFormat, 200, false, model.wall))
  rulerNode.addInputEventListener(new PBasicInputEventHandler {
    override def mouseDragged(event: PInputEvent) = {
      rulerNode.translate(event.getDeltaRelativeTo(rulerNode.getParent).width, event.getDeltaRelativeTo(rulerNode.getParent).height)
    }
  })
  rulerNode.addInputEventListener(new CursorHandler)

  addNode(new WallNode(model.wall, transform, opposite(backgroundColor)))
  addNode(rulerNode)
}

class MyDoubleGeneralPath(pt: Point2D) extends DoubleGeneralPath(pt) {
  def curveTo(control1: Vector2D, control2: Vector2D, dest: Vector2D) = super.curveTo(control1.x, control1.y, control2.x, control2.y, dest.x, dest.y)
}

class SpringNode(model: ForceLawLabModel, transform: ModelViewTransform2D, color: Color) extends PNode {
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
}
class ForceLawLabControlPanel(model: ForceLawLabModel) extends ControlPanel {
  add(new ScalaValueControl(0.01, 100, model.m1.name, "0.00", ForceLawLabResources.getLocalizedString("units.kg"), model.m1.mass, model.m1.mass = _, model.m1.addListener))
  add(new ScalaValueControl(0.01, 100, model.m2.name, "0.00", ForceLawLabResources.getLocalizedString("units.kg"), model.m2.mass, model.m2.mass = _, model.m2.addListener))
}

class SunPlanetControlPanel(model: ForceLawLabModel, m: Magnification, units: UnitsContainer,phetFrame:PhetFrame) extends ControlPanel {
  import ForceLawLabDefaults._
  import ForceLawLabResources._
  add(new ScalaValueControl(kgToEarthMasses(model.m1.mass / 10), kgToEarthMasses(model.m1.mass * 5), model.m1.name + " mass", "0.00", "earth masses",
    kgToEarthMasses(model.m1.mass), a => model.m1.mass = earthMassesToKg(a), model.m1.addListener))

  units.addListenerByName {
    distanceSlider.setRangeAndValue(0.01, units.metersToUnits(sunEarthDist * 5), units.metersToUnits(model.distance))
    distanceSlider.setUnits(units.units.name)
  }

  val distanceSlider = new ScalaValueControl(0.01, units.metersToUnits(sunEarthDist * 5), "distance", "0.00", getLocalizedString("units.light-minutes"),
    units.metersToUnits(model.distance), a => model.distance = units.unitsToMeters(a), addDistanceListener)
  distanceSlider.getTextField.setColumns(8) //to show kilometers
  distanceSlider.addTickLabel(0.01, "min") //avoid generating 1E8 tick marks

  distanceSlider.getSlider.addMouseListener(new MouseAdapter() {
    override def mouseReleased(e: MouseEvent) = model.setDragging(false)

    override def mousePressed(e: MouseEvent) = model.setDragging(true)
  })
  add(distanceSlider)

  def addDistanceListener(listener: () => Unit) = {
    model.m1.addListener(listener)
    model.m2.addListener(listener)
    //    units.addListener(listener)
  }

  def addPlanetListener(listener: () => Unit) = {
    model.m1.addListener(listener)
    model.m2.addListener(listener) //since sun location can change
  }
  case class Planet(name: String, mass: Double, dist: Double)
  val planets = new Planet("Earth", earthMass, sunEarthDist) :: Nil

  def setPlanet(p: Planet) = {
    model.m1.mass = p.mass
    model.m2.position = new Vector2D(p.dist / 2, 0)
    model.m1.position = new Vector2D(-p.dist / 2, 0)
  }

  def isPlanet(p: Planet) = {
    MathUtil.isApproxEqual(model.m1.mass, p.mass, p.mass * 0.05) &&
            MathUtil.isApproxEqual(model.distance, p.dist, p.dist * 0.05)
  }

  for (p <- planets)
    add(new MyRadioButton(p.name, setPlanet(p), isPlanet(p), addPlanetListener))

  val none = new MyRadioButton("Custom", () => {}, !planets.foldLeft(false) {(a, b) => {a || isPlanet(b)}}, addPlanetListener)
  add(none)

  add(new ScaleControl(m))
  add(new UnitsControl(units,phetFrame))
}

class UnitsControl(units: UnitsContainer,phetFrame:PhetFrame) extends VerticalLayoutPanel {
  setBorder(BorderFactory.createTitledBorder("Units"))
  for (u <- UnitsCollection.values) add(new MyRadioButton(u.name, units.units = u, units.units == u, units.addListener))
  val unitsExplanation = new UnitsExplanation(phetFrame)
  add(new MyJButton("Compare", () => unitsExplanation.setVisible(true)))
}

class UnitsExplanation(phetFrame:PhetFrame) extends JDialog(phetFrame,"Units Comparison",false) {
  val textArea = new JTextArea("1 light minute = 17 987 547.5 kilometers.\nThat's the distance light travels in 1 minute.\n\nHow far can you travel in 1 minute?")
  textArea.setEditable(false)
  textArea.setFont(new PhetFont(16,true))
  setContentPane(textArea)
  pack()
  override def setVisible(b: Boolean) = {
    SwingUtils.centerDialogInParent(this)
    super.setVisible(b)
  }
}

case class Units(name: String, scale: Double) {
  def metersToUnits(m: Double) = m * scale

  def unitsToMeters(u: Double) = u / scale
}
object UnitsCollection {
  val values = new Units("light-minutes", 5.5594E-11) :: //new Units("meters", 1.0) ::
          new Units("kilometers", 1 / 1000.0) :: Nil
}

class UnitsContainer(private var _units: Units) extends Observable {
  def units = _units

  def units_=(u: Units) = {
    _units = u
    notifyListeners()
  }

  def metersToUnits(m: Double) = _units.metersToUnits(m)

  def unitsToMeters(u: Double) = _units.unitsToMeters(u)

}
class Magnification(private var _magnified: Boolean) extends Observable {
  def magnified_=(b: Boolean) = {
    _magnified = b
    notifyListeners()
  }

  def magnified = _magnified
}
class ScaleControl(m: Magnification) extends VerticalLayoutPanel {
  setBorder(BorderFactory.createTitledBorder("Scale"))
  add(new MyRadioButton("Magnified", m.magnified = true, m.magnified, m.addListener))
  add(new MyRadioButton("Actual Size", m.magnified = false, !m.magnified, m.addListener))
}

class Mass(private var _mass: Double, private var _position: Vector2D, val name: String, private var _massToRadius: Double => Double) extends Observable {
  def mass = _mass

  def mass_=(m: Double) = {_mass = m; notifyListeners()}

  def position = _position

  def position_=(p: Vector2D) = {_position = p; notifyListeners()}

  def radius = _massToRadius(_mass)

  def setMassToRadiusFunction(massToRadius: Double => Double) = {
    _massToRadius = massToRadius
    notifyListeners()
  }
}

class Spring(val k: Double, val restingLength: Double)

class ForceLawLabModel(mass1: Double, mass2: Double,
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
  private var isDraggingControl = false
  m1.addListenerByName(notifyListeners())
  m2.addListenerByName(notifyListeners())

  //causes dynamical problems, e.g. oscillations if you use the full model
  def rFake = m2.position - new Vector2D(wall.maxX + spring.restingLength, 0)

  def rFull = m1.position - m2.position

  def r = rFull

  def distance = m2.position.x - m1.position.x

  //set the location of the m1 based on the total separation radius
  def distance_=(d: Double) = m1.position = new Vector2D(m2.position.x - d, 0)

  def rMin = if (m1.position.x + m1.radius < m2.position.x - m2.radius) r else m2.position - new Vector2D(m2.radius, 0)

  def getGravityForce = r * G * m1.mass * m2.mass / pow(r.magnitude, 3)

  def setDragging(b: Boolean) = this.isDraggingControl = b

  def update(dt: Double) = {
    //    println("force magnitude=" + getForce.magnitude + ", from r=" + r + ", m1.x=" + m1.position.x + ", m2.position.x=" + m2.position.x)
    val xDesired = wall.maxX + spring.restingLength + getGravityForce.magnitude / spring.k
    val x = if (xDesired + m1.radius > m2.position.x - m2.radius)
      m2.position.x - m2.radius - m1.radius
    else
      xDesired
    if (!isDraggingControl)
      m1.position = new Vector2D(x, 0)
  }
}

class TinyDecimalFormat extends DecimalFormat("0.00000000000") {
  override def format(number: Double, result: StringBuffer, fieldPosition: FieldPosition) = {
    val s = super.format(number, result, fieldPosition).toString
    //start at the end, and insert spaces after every 3 elements, may not work for every locale
    var str = ""
    for (ch <- s) {
      str = str + ch
      if (str.length % 4 == 0) //4 instead of 3 because space adds another element
        str = str + " "
    }
    new StringBuffer(str)
  }
}

class SunPlanetDecimalFormat extends DecimalFormat("#,###,###,###,###,###,##0.0", {
  val f = new DecimalFormatSymbols
  f.setGroupingSeparator(' ')
  f
}) {
}

class ForceLawsModule(clock: ScalaClock) extends Module(ForceLawLabResources.getLocalizedString("module.force-laws.name"), clock) {
  val model = new ForceLawLabModel(10, 25, 0, 1, mass => mass / 30, mass => mass / 30, 1E-8, 1, 50, 50, -4, ForceLawLabResources.getLocalizedString("mass-1"), ForceLawLabResources.getLocalizedString("mass-2"))
  val canvas = new ForceLawLabCanvas(model, 10, Color.blue, Color.blue, Color.white, 10, 10,
    ForceLawLabResources.getLocalizedString("units.m"), _.toString, 1E10, new TinyDecimalFormat(), new Magnification(false))
  setSimulationPanel(canvas)
  clock.addClockListener(model.update(_))
  setControlPanel(new ForceLawLabControlPanel(model))
  setClockControlPanel(null)
}

object ForceLawLabDefaults {
  val sunMercuryDist = 5.791E10 //  sun earth distace in m
  val sunEarthDist = 1.496E11 //  sun earth distace in m
  val earthRadius = 6.371E6
  val sunRadius = 6.955E8
  val earthMass = 5.9742E24 //kg

  val metersPerLightMinute = 5.5594E-11

  def metersToLightMinutes(a: Double) = a * metersPerLightMinute

  def lightMinutesToMeters(a: Double) = a / metersPerLightMinute

  def kgToEarthMasses(a: Double) = a / earthMass

  def earthMassesToKg(a: Double) = a * earthMass
}

class SolarModule(clock: ScalaClock,phetFrame:PhetFrame) extends Module(ForceLawLabResources.getLocalizedString("module.sun-planet-system.name"), clock) {
  val magnification = new Magnification(true)
  val units = new UnitsContainer(UnitsCollection.values(0))
  import ForceLawLabDefaults._
  import ForceLawLabResources._
  val model = new ForceLawLabModel(earthMass, //earth mass in kg
    1.9891E30, // sun mass in kg
    -sunEarthDist / 2,
    sunEarthDist / 2,
    mass => {
      val scale = if (magnification.magnified) 1.6E3 else 1.0 //latter term is a fudge factor to make things visible on the same scale
      earthRadius * scale
    },
    mass => {
      val scale = if (magnification.magnified) 5E1 else 1.0
      sunRadius * scale
    },
    //    1.5E14, sunEarthDist / 2, // this version puts the spring resting length so that default position is distEarthSun
    //    0.98E12, sunEarthDist / 4,  //this requires the sun to tug on the earth to put it in the right spot
    1.42E12, sunEarthDist / 3, //this one too
    1E13,
    1E12,
    -sunEarthDist, getLocalizedString("planet"), getLocalizedString("sun")
    )

  val canvas = new ForceLawLabCanvas(model, sunEarthDist * 2.05, Color.blue, Color.red, Color.black,
    (ForceLawLabDefaults.sunEarthDist * 3).toLong, 10, getLocalizedString("units.light-minutes"), dist => {
      new DecimalFormat("0.0").format(metersToLightMinutes(dist.toDouble))
    }, 3.2E-22 * 10, new SunPlanetDecimalFormat(), magnification)

  magnification.addListenerByName {
    model.m1.notifyListeners() //chain events from magnification
    model.m2.notifyListeners()
  }
  val disclaimerNode = new ScaleDisclaimerNode(model, canvas.transform)
  canvas.addComponentListener(new java.awt.event.ComponentAdapter() {
    override def componentResized(e: java.awt.event.ComponentEvent) = updateDisclaimerLocation()
  })
  updateDisclaimerLocation()
  def updateDisclaimerLocation() = disclaimerNode.setOffset(canvas.canonicalBounds.width / 2 - disclaimerNode.getFullBounds.getWidth / 2, canvas.canonicalBounds.height - disclaimerNode.getFullBounds.getHeight * 3)
  //  canvas.addNode(disclaimerNode)//todo: decide whether or not we're using the disclaimer
  setSimulationPanel(canvas)
  clock.addClockListener(model.update(_))
  setControlPanel(new SunPlanetControlPanel(model, magnification, units,phetFrame))
  setClockControlPanel(null)
}

class Circle(center: Vector2D, radius: Double) extends Ellipse2D.Double(center.x - radius, center.y - radius, radius * 2, radius * 2)
class ScaleDisclaimerNode(model: ForceLawLabModel, transform: ModelViewTransform2D) extends PNode {
  val text = new PText(ForceLawLabResources.getLocalizedString("scale.disclaimer.start") + " ")
  text.setFont(new PhetFont(16, true))
  text.setTextPaint(Color.lightGray)
  import ForceLawLabDefaults._
  //  val earthIcon = new PhetPPath(new Circle(new Vector2D, transform.modelToViewDifferentialXDouble(earthRadius)), Color.blue)
  val sunIcon = new PhetPPath(new Circle(new Vector2D, transform.modelToViewDifferentialXDouble(sunRadius)), Color.red)

  val text2 = new PText(ForceLawLabResources.getLocalizedString("scale.disclaimer.end") + " ")
  text2.setFont(new PhetFont(16, true))
  text2.setTextPaint(Color.lightGray)

  val node = new SwingLayoutNode
  node.addChild(text)
  node.addChild(sunIcon)
  node.addChild(text2)
  //  node.addChild(earthIcon)

  addChild(node)
}

class ForceLawLabApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new ForceLawsModule(new ScalaClock(30, 30 / 1000.0)))
  addModule(new SolarModule(new ScalaClock(30, 30 / 1000.0),getPhetFrame))
}

object ForceLawLabApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "force-law-lab", classOf[ForceLawLabApplication])
}