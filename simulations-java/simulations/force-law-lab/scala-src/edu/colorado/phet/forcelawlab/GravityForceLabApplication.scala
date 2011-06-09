package edu.colorado.phet.forcelawlab

import java.lang.Math._
import edu.colorado.phet.common.phetcommon.model.Resettable
import edu.colorado.phet.common.phetcommon.view.{VerticalLayoutPanel, PhetFrame, ControlPanel}
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import java.awt.Color
import java.awt.geom.Ellipse2D
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.Predef._
import java.text.{DecimalFormat, FieldPosition}
import edu.colorado.phet.scalacommon.swing.MyRadioButton
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.common.phetcommon.application.{PhetApplicationLauncher, PhetApplicationConfig, Module}
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication
import javax.swing.border.TitledBorder

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
  def createTitledBorder(key: String) = new TitledBorder(ForceLawLabResources.getLocalizedString(key)) {
    setTitleFont(new PhetFont(14, true))
  }
}

class GravityForceLabApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new ForceLawsModule(new ScalaClock(30, 30 / 1000.0)))
}

object GravityForceLabApplication extends App {
  new PhetApplicationLauncher().launchSim(args, "force-law-lab", "gravity-force-lab", classOf[GravityForceLabApplication])
}