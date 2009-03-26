package edu.colorado.phet.therampscala


import scalacommon.util.Observable

class ScalaRampObject(_name: String, _mass: Double, _imageFilename: String, _customizable: Boolean) {
  val customizable = _customizable
  val name = _name

  def mass: Double = _mass

  val imageFilename = _imageFilename

  def getDisplayText = name + " (" + mass + " kg)"

  def this(name: String, mass: Double, imageFilename: String) = this (name, mass, imageFilename, false)

  def height = mass / 100.0
}

class CustomTextRampObject(name: String, mass: Double, imageFilename: String, customizable: Boolean) extends ScalaRampObject(name, mass, imageFilename, customizable) {
  override def getDisplayText = name
}

class MutableRampObject(name: String, _mass: Double, imageFilename: String, customizable: Boolean)
        extends CustomTextRampObject(name, _mass, imageFilename, customizable) with Observable {
  private var m_mass = _mass

  override def mass = m_mass

  def mass_=(m: Double) = {
    m_mass = m
    notifyListeners()
  }

//  def height = m_mass / 100.0
}

object RampDefaults {
  val MIN_X = -10
  val MAX_X = 10
  val objects = new ScalaRampObject("File Cabinet", 200.0, "cabinet.gif") ::
          new ScalaRampObject("Sleepy Dog", 25.0, "ollie.gif") ::
          new ScalaRampObject("Small Crate", 150, "crate.gif") ::
          new MutableRampObject("Custom Crate", 150, "crate.gif", true) ::
          new ScalaRampObject("Refrigerator", 400, "fridge.gif") ::
          new ScalaRampObject("Textboox", 10, "phetbook.gif") ::
          new ScalaRampObject("Big Crate", 300, "crate.gif") ::
          new CustomTextRampObject("Mystery Object", 300, "mystery-box.png", false) ::
          Nil
  val objectsPerRow = 4


}