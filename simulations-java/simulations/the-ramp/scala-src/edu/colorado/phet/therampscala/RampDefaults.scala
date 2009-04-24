package edu.colorado.phet.therampscala


import java.awt.Color
import scalacommon.util.Observable

class ScalaRampObject(_name: String, _mass: Double, val kineticFriction: Double, val staticFriction: Double, _imageFilename: String, _customizable: Boolean) {
  val customizable = _customizable
  val name = _name

  def mass: Double = _mass

  val imageFilename = _imageFilename

  def getDisplayText = name + " (" + mass + " kg)"

  def this(name: String, mass: Double, kineticFriction: Double, staticFriction: Double, imageFilename: String) = this (name, mass, kineticFriction, staticFriction, imageFilename, false)

  def height = mass / 100.0
}

class CustomTextRampObject(name: String, mass: Double, kineticFriction: Double, staticFriction: Double, imageFilename: String, customizable: Boolean) extends ScalaRampObject(name, mass, kineticFriction, staticFriction, imageFilename, customizable) {
  override def getDisplayText = name
}

class MutableRampObject(name: String, _mass: Double, kineticFriction: Double, staticFriction: Double, imageFilename: String, customizable: Boolean)
        extends CustomTextRampObject(name, _mass, kineticFriction, staticFriction, imageFilename, customizable) with Observable {
  private var m_mass = _mass

  override def mass = m_mass

  def mass_=(m: Double) = {
    m_mass = m
    notifyListeners()
  }

  //  def height = m_mass / 100.0
}