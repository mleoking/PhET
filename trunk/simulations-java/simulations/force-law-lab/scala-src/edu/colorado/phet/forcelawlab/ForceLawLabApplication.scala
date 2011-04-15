package edu.colorado.phet.forcelawlab

import collection.mutable.ArrayBuffer
import java.lang.Math._
import edu.umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import edu.umd.cs.piccolo.nodes.PText
import edu.colorado.phet.common.phetcommon.model.Resettable
import edu.colorado.phet.common.phetcommon.view.{VerticalLayoutPanel, PhetFrame, ControlPanel}
import edu.colorado.phet.common.phetcommon.view.util.{DoubleGeneralPath, PhetFont}
import java.awt.{Font, Color}
import edu.colorado.phet.common.piccolophet.nodes._
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.umd.cs.piccolo.util.PDimension
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint
import java.awt.geom.{Ellipse2D, Point2D}
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform
import java.text.{DecimalFormatSymbols, DecimalFormat, FieldPosition, NumberFormat}
import edu.colorado.phet.scalacommon.swing.MyRadioButton
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.common.phetcommon.application.{PhetApplicationLauncher, PhetApplicationConfig, Module}
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication
import javax.swing.border.TitledBorder
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D

class ForceLabelNode(target: Mass, source: Mass, transform: ModelViewTransform, model: ForceLawLabModel,
                     color: Color, scale: Double, format: NumberFormat, offsetY: Double, right: Boolean) extends PNode {
  val arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(1, 1), 20, 20, 8, 0.5, true) {
    setPaint(color)
  }
  val label = new PText {
    setTextPaint(color)
    setFont(new PhetFont(18, true))
  }

  defineInvokeAndPass(model.addListenerByName) {
                                                 label.setOffset(transform.modelToView(target.position) - new Vector2D(0, label.getFullBounds.getHeight + offsetY))
                                                 val str = ForceLawLabResources.format("force-description-pattern-target_source_value", target.name, source.name, format.format(model.getGravityForce.magnitude))
                                                 label.setText(str)
                                                 val sign = if ( right ) {
                                                   1
                                                 }
                                                 else {
                                                   -1
                                                 }
                                                 val tip = label.getOffset + new Vector2D(sign * model.getGravityForce.magnitude * scale, -20)
                                                 val tail = label.getOffset + new Vector2D(0, -20)
                                                 arrowNode.setTipAndTailLocations(tip, tail)
                                                 if ( !right ) {
                                                   label.translate(-label.getFullBounds.getWidth, 0)
                                                 }
                                               }

  addChild(arrowNode)
  addChild(label)
}

class MassNode(mass: Mass, transform: ModelViewTransform, color: Color, magnification: Magnification, textOffset: () => Double) extends PNode {
  val image = new SphericalNode(mass.radius * 2, color, false)
  val label = new ShadowPText(mass.name, Color.white, new PhetFont(16, true))
  val w = 6
  val centerIndicator = new PhetPPath(new Ellipse2D.Double(-w / 2, -w / 2, w, w), Color.black)

  defineInvokeAndPass(mass.addListenerByName) {
                                                image.setOffset(transform.modelToView(mass.position))
                                                val viewRadius = transform.modelToViewDeltaX(mass.radius)
                                                image.setDiameter(viewRadius * 2)
                                                image.setPaint(new RoundGradientPaint(viewRadius, -viewRadius, Color.WHITE, new Point2D.Double(-viewRadius, viewRadius), color))
                                                label.setOffset(transform.modelToView(mass.position) - new Vector2D(label.getFullBounds.getWidth / 2, label.getFullBounds.getHeight / 2))
                                                label.translate(0, textOffset())
                                                centerIndicator.setOffset(transform.modelToView(mass.position))
                                                centerIndicator.setVisible(centerIndicator.getFullBounds.getWidth < image.getFullBounds.getWidth)
                                              }


  addChild(image)
  addChild(label)
  addChild(centerIndicator)
}

class DragHandler(mass: Mass,
                  transform: ModelViewTransform,
                  minDragX: () => Double, maxDragX: () => Double, node: PNode) extends PBasicInputEventHandler {
  var dragging = false

  override def mouseDragged(event: PInputEvent) {
    implicit def toPoint2D(dim: PDimension) = new Point2D.Double(dim.width, dim.height)
    val delta = event.getDeltaRelativeTo(node.getParent)
    val x: Double = transform.viewToModelDelta(new ImmutableVector2D(delta.width, delta.height)).getX
    mass.position = mass.position + new Vector2D(x, 0)
    if ( mass.position.x < minDragX() ) {
      mass.position = new Vector2D(minDragX(), 0)
    }
    if ( mass.position.x > maxDragX() ) {
      mass.position = new Vector2D(maxDragX(), 0)
    }
  }

  override def mousePressed(event: PInputEvent) {
    dragging = true
  }

  override def mouseReleased(event: PInputEvent) {
    dragging = false
  }
}

class DraggableMassNode(mass: Mass, transform: ModelViewTransform,
                        color: Color, minDragX: () => Double, maxDragX: () => Double,
                        magnification: Magnification, textOffset: () => Double)
        extends MassNode(mass, transform, color, magnification, textOffset) {
  addInputEventListener(new DragHandler(mass, transform, minDragX, maxDragX, this))
  addInputEventListener(new CursorHandler)
}

class ForceLawLabCanvas(model: ForceLawLabModel, modelWidth: Double, mass1Color: Color, mass2Color: Color, backgroundColor: Color,
                        rulerLength: Long, numTicks: Long, rulerLabel: String, tickToString: Long => String,
                        forceLabelScale: Double, forceArrowNumberFormat: NumberFormat, magnification: Magnification, units: UnitsContainer) extends DefaultCanvas(modelWidth, modelWidth) {
  setBackground(backgroundColor)

  val tickIncrement = rulerLength / numTicks

  val ticks = new ArrayBuffer[Long]
  private var _x = 0L
  while ( _x <= rulerLength ) {
    //no high level support for 1L to 5L => Range[Long]
    ticks += _x
    _x += tickIncrement
  }

  def maj = for ( i <- 0 until ticks.size ) yield {
    if ( i == 1 && tickToString(ticks(i)).length > 1 ) {
      ""
    } //todo improve heuristic for overlapping text
    else if ( i == ticks.size - 1 && tickToString(ticks(i)).length > 5 ) {
      ""
    } //skip last tick label in km if it is expected to go off the ruler
    else {
      tickToString(ticks(i))
    }
  }

  val rulerNode = {
    val dx = transform.modelToViewDeltaX(rulerLength)
    new RulerNode(dx, 14, 40, maj.toArray, new PhetFont(Font.BOLD, 16), rulerLabel, new PhetFont(Font.BOLD, 16), 4, 10, 6);
  }
  units.addListenerByName {
                            rulerNode.setUnits(units.units.name)
                            rulerNode.setMajorTickLabels(maj.toArray)
                          }

  def resetRulerLocation() {
    rulerNode.setOffset(150, 500)
  }

  resetRulerLocation()

  def updateRulerVisible() {}

  //rulerNode.setVisible(!magnification.magnified)
  magnification.addListenerByName(updateRulerVisible())
  updateRulerVisible()

  def opposite(c: Color) = new Color(255 - c.getRed, 255 - c.getGreen, 255 - c.getBlue)

  val minDragX = () => transform.viewToModelX(getVisibleModelBounds.getMinX)
  val maxDragX = () => transform.viewToModelX(getVisibleModelBounds.getMaxX)

  addNode(new CharacterNode(model.m1, model.m2, transform, true, () => model.getGravityForce.magnitude, minDragX, model.mass1MaxX))
  addNode(new CharacterNode(model.m2, model.m1, transform, false, () => model.getGravityForce.magnitude, model.mass2MinX, maxDragX))
  addNode(new DraggableMassNode(model.m1, transform, mass1Color, minDragX, model.mass1MaxX, magnification, () => 10))
  addNode(new DraggableMassNode(model.m2, transform, mass2Color, model.mass2MinX, maxDragX, magnification, () => -10))
  addNode(new ForceLabelNode(model.m1, model.m2, transform, model, opposite(backgroundColor), forceLabelScale, forceArrowNumberFormat, 100, true))
  addNode(new ForceLabelNode(model.m2, model.m1, transform, model, opposite(backgroundColor), forceLabelScale, forceArrowNumberFormat, 200, false))
  rulerNode.addInputEventListener(new PBasicInputEventHandler {
    override def mouseDragged(event: PInputEvent) {
      rulerNode.translate(event.getDeltaRelativeTo(rulerNode.getParent).width, event.getDeltaRelativeTo(rulerNode.getParent).height)
    }
  })
  rulerNode.addInputEventListener(new CursorHandler)
  addNode(rulerNode)
}

class MyDoubleGeneralPath(pt: Point2D) extends DoubleGeneralPath(pt) {
  def curveTo(control1: Vector2D, control2: Vector2D, target: Vector2D) {
    super.curveTo(control1.x, control1.y, control2.x, control2.y, target.x, target.y)
  }
}

class ForceLawLabControlPanel(model: ForceLawLabModel, resetFunction: () => Unit) extends ControlPanel {

  import ForceLawLabResources._

  val m1Update = (x: Double) => {
    model.m1.mass = x
    model.m1.position = new Vector2D(java.lang.Math.min(model.mass1MaxX(), model.m1.position.x), model.m1.position.y)
  }
  val m2Update = (x: Double) => {
    model.m2.mass = x
    model.m2.position = new Vector2D(java.lang.Math.max(model.mass2MinX(), model.m2.position.x), model.m2.position.y)
  }
  add(new ForceLawLabScalaValueControl(0.01, 100, model.m1.name, "0.00", getLocalizedString("units.kg"), model.m1.mass, m1Update, model.m1.addListener))
  add(new ForceLawLabScalaValueControl(0.01, 100, model.m2.name, "0.00", getLocalizedString("units.kg"), model.m2.mass, m2Update, model.m2.addListener))
  addResetAllButton(new Resettable() {
    def reset() {
      model.reset()
      resetFunction()
    }
  })
}

class ForceLawLabScalaValueControl(min: Double, max: Double, name: String, decimalFormat: String, units: String,
                                   getter: => Double, setter: Double => Unit, addListener: ( () => Unit ) => Unit) extends ScalaValueControl(min, max, name, decimalFormat, units,
                                                                                                                                             getter, setter, addListener) {
  getTextField.setFont(new PhetFont(16, true))
}

class UnitsControl(units: UnitsContainer, phetFrame: PhetFrame) extends VerticalLayoutPanel {
  setBorder(ForceLawBorders.createTitledBorder("units")) //todo: translate when used
  for ( u <- UnitsCollection.values ) {
    add(new MyRadioButton(u.name, units.units = u, units.units == u, units.addListener))
  }
}

case class Units(name: String, scale: Double) {
  def metersToUnits(m: Double) = m * scale

  def unitsToMeters(u: Double) = u / scale
}

object UnitsCollection {
  val values = new Units(ForceLawLabResources.getLocalizedString("units.light-minutes"), 5.5594E-11) :: //new Units("meters", 1.0) ::
               new Units(ForceLawLabResources.getLocalizedString("units.kilometers"), 1 / 1000.0) :: Nil
}

class UnitsContainer(private var _units: Units) extends Observable {
  private val initialState = _units

  def units = _units

  def units_=(u: Units) {
    _units = u
    notifyListeners()
  }

  def metersToUnits(m: Double) = _units.metersToUnits(m)

  def unitsToMeters(u: Double) = _units.unitsToMeters(u)

  def reset() {
    units = initialState
  }
}

class Magnification(private var _magnified: Boolean) extends Observable {
  private val initialState = _magnified

  def magnified_=(b: Boolean) {
    _magnified = b
    notifyListeners()
  }

  def magnified = _magnified

  def reset() {
    magnified = initialState
  }
}

class ScaleControl(m: Magnification) extends VerticalLayoutPanel {
  setBorder(ForceLawBorders.createTitledBorder("object.size"))
  add(new MyRadioButton(ForceLawLabResources.getLocalizedString("object.size.cartoon"), m.magnified = true, m.magnified, m.addListener))
  add(new MyRadioButton(ForceLawLabResources.getLocalizedString("object.size.actual-size"), m.magnified = false, !m.magnified, m.addListener))
}

class Mass(private var _mass: Double, private var _position: Vector2D, val name: String, private var _massToRadius: Double => Double) extends Observable {
  def setState(m: Double, p: Vector2D, mtr: Double => Double) {
    mass = m
    position = p
    setMassToRadiusFunction(mtr)
  }

  def mass = _mass

  def mass_=(m: Double) {
    _mass = m;
    notifyListeners()
  }

  def position = _position

  def position_=(p: Vector2D) {
    _position = p;
    notifyListeners()
  }

  def radius = _massToRadius(_mass)

  def setMassToRadiusFunction(massToRadius: Double => Double) {
    _massToRadius = massToRadius
    notifyListeners()
  }
}

class ForceLawLabModel(mass1: Double,
                       mass2: Double,
                       mass1Position: Double,
                       mass2Position: Double,
                       mass1Radius: Double => Double,
                       mass2Radius: Double => Double,
                       springConstant: Double,
                       springRestingLength: Double,
                       wallWidth: Double,
                       wallHeight: Double,
                       wallMaxX: Double,
                       mass1Name: String,
                       mass2Name: String) extends Observable {
  val m1 = new Mass(mass1, new Vector2D(mass1Position, 0), mass1Name, mass1Radius)
  val m2 = new Mass(mass2, new Vector2D(mass2Position, 0), mass2Name, mass2Radius)

  val minDistanceBetweenMasses = 0.1
  //so that they won't be touching at their closest point
  //todo: turn into defs
  val mass1MaxX = () => m2.position.x - m2.radius - m1.radius - minDistanceBetweenMasses
  val mass2MinX = () => m1.position.x + m1.radius + m2.radius + minDistanceBetweenMasses

  private var isDraggingControl = false
  m1.addListenerByName(notifyListeners())
  m2.addListenerByName(notifyListeners())

  def reset() {
    m1.setState(mass1, new Vector2D(mass1Position, 0), mass1Radius)
    m2.setState(mass2, new Vector2D(mass2Position, 0), mass2Radius)
  }

  def rFull = m1.position - m2.position

  def r = rFull

  def distance = m2.position.x - m1.position.x

  //set the location of the m2 based on the total separation radius
  def distance_=(d: Double) {
    m2.position = new Vector2D(m1.position.x + d, 0)
  }

  def rMin = if ( m1.position.x + m1.radius < m2.position.x - m2.radius ) {
    r
  }
  else {
    m2.position - new Vector2D(m2.radius, 0)
  }

  def getGravityForce = r * ForceLawLabDefaults.G * m1.mass * m2.mass / pow(r.magnitude, 3)

  def setDragging(b: Boolean) {
    this.isDraggingControl = b
  }

  def update(dt: Double) {}
}

class TinyDecimalFormat extends DecimalFormat("0.00000000000") {
  override def format(number: Double, result: StringBuffer, fieldPosition: FieldPosition) = {
    val s = super.format(number, result, fieldPosition).toString
    //start at the end, and insert spaces after every 3 elements, may not work for every locale
    var str = ""
    for ( ch <- s ) {
      str = str + ch
      //4 instead of 3 because space adds another element
      if ( str.length % 4 == 0 ) {
        str = str + " "
      }
    }
    new StringBuffer(str)
  }
}

class SunPlanetDecimalFormat extends DecimalFormat("#,###,###,###,###,###,##0.0", {
  val x = new DecimalFormatSymbols()
  x.setGroupingSeparator(' ')
  x
})

class ForceLawsModule(clock: ScalaClock) extends Module(ForceLawLabResources.getLocalizedString("module.force-laws.name"), clock) {
  def massToRadiusFn(m: Double) = pow(m, 1 / 3.0) / 10.0 * 4.0

  val model = new ForceLawLabModel(38, 25, -2, 2, massToRadiusFn, massToRadiusFn, 9E-10, 0.0, 50, 50, -4, ForceLawLabResources.getLocalizedString("mass-1"), ForceLawLabResources.getLocalizedString("mass-2"))
  val canvas = new ForceLawLabCanvas(model, 10, Color.blue, Color.red, Color.white, 10, 10,
                                     ForceLawLabResources.getLocalizedString("units.m"), _.toString, 1E10,
                                     new TinyDecimalFormat(), new Magnification(false), new UnitsContainer(new Units("meters", 1)))
  setSimulationPanel(canvas)
  clock.addClockListener(model.update(_))
  setControlPanel(new ForceLawLabControlPanel(model, () => canvas.resetRulerLocation()))
  setClockControlPanel(null)
  model.notifyListeners() //this workaround ensures all view components update; this is necessary because one of them has an incorrect transform on startup, and is not observing the transform object
}

object ForceLawLabDefaults {
  //  sun earth distance in m
  val sunMercuryDist = 5.791E10
  //  sun earth distance in m
  val sunEarthDist = 1.496E11
  val earthRadius = 6.371E6
  val sunRadius = 6.955E8
  //masses are in kg
  val earthMass = 5.9742E24
  val sunMass = 1.9891E30
  //Gravitational constant in MKS
  val G = 6.67E-11
  val metersPerLightMinute = 5.5594E-11

  def metersToLightMinutes(a: Double) = a * metersPerLightMinute

  def lightMinutesToMeters(a: Double) = a / metersPerLightMinute

  def kgToEarthMasses(a: Double) = a / earthMass

  def earthMassesToKg(a: Double) = a * earthMass
}

class Circle(center: Vector2D, radius: Double) extends Ellipse2D.Double(center.x - radius, center.y - radius, radius * 2, radius * 2)

object ForceLawBorders {
  def createTitledBorder(key: String) =
    new TitledBorder(ForceLawLabResources.getLocalizedString(key)) {
      setTitleFont(new PhetFont(14, true))
    }
}

class ForceLawLabApplication

class GravityForceLabApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new ForceLawsModule(new ScalaClock(30, 30 / 1000.0)))
}

object GravityForceLabApplication {
  def main(args: Array[String]) {
    new PhetApplicationLauncher().launchSim(args, "force-law-lab", "gravity-force-lab", classOf[GravityForceLabApplication])
  }
}