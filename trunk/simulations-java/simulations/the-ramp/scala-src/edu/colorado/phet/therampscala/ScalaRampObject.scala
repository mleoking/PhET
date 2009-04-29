package edu.colorado.phet.therampscala


import java.awt.Color
import scalacommon.util.Observable

class ScalaRampObject(_name: String,
                      protected var _mass: Double,
                      protected var _kineticFriction: Double,
                      protected var _staticFriction: Double,
                      _height: Double,
                      _imageFilename: String,
                      val iconFilename: String,
                      _customizable: Boolean) {
  val customizable = _customizable
  val name = _name

  def kineticFriction = _kineticFriction

  def staticFriction = _staticFriction

  def mass: Double = _mass

  val imageFilename = _imageFilename

  def getDisplayText = name + " (" + mass + " kg)"

  def getDisplayTextHTML = <html>{name}<br> </br>{mass}kg</html>

  def this(name: String, mass: Double, kineticFriction: Double, staticFriction: Double, height: Double, imageFilename: String) = this (name, mass, kineticFriction, staticFriction, height, imageFilename, imageFilename, false)

  def height = _height

  private val bufferedImage = RampResources.getImage(imageFilename)

  def width = bufferedImage.getWidth * height / bufferedImage.getHeight.toDouble

  def displayTooltip = true

  override def equals(obj: Any) = {
    obj match {
      case a: ScalaRampObject => a.name == name && a.mass == mass && a.height == height && a.kineticFriction == kineticFriction
      case _ => false
    }
  }

  override def hashCode = mass.hashCode + name.hashCode * 17
}

class CustomTextRampObject(name: String, mass: Double, kineticFriction: Double, staticFriction: Double, height: Double, imageFilename: String, iconFilename: String, customizable: Boolean)
        extends ScalaRampObject(name, mass, kineticFriction, staticFriction, height, imageFilename, iconFilename, customizable) {
  override def getDisplayText = name

  override def getDisplayTextHTML = <html>{name}</html>

  override def displayTooltip = false
}

class MutableRampObject(name: String, __mass: Double, kineticFriction: Double, staticFriction: Double, height: Double, imageFilename: String, iconFilename: String, customizable: Boolean)
        extends CustomTextRampObject(name, __mass, kineticFriction, staticFriction, height, imageFilename, iconFilename, customizable) with Observable {
  def mass_=(m: Double) = {
    _mass = m
    notifyListeners()
  }

  override def height = mass / 20 / 2

  def kineticFriction_=(k: Double) = {
    _kineticFriction = k
    notifyListeners()
  }

  def staticFriction_=(s: Double) = {
    _staticFriction = s
    notifyListeners()
  }
}