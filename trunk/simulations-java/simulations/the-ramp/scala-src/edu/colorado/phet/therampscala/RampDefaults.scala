package edu.colorado.phet.therampscala

import java.awt.Color

object RampDefaults {
  val MIN_X = -10
  val MAX_X = 10

  //ScalaRampObject(name,mass,kineticFriction,staticFriction,image)
  val objects = new ScalaRampObject("File Cabinet", 5.0, 0.5, 0.5, "cabinet.gif") ::
          new ScalaRampObject("Sleepy Dog", 25.0, 0.5, 0.5, "ollie.gif") ::
          new ScalaRampObject("Small Crate", 150, 0.5, 0.5, "crate.gif") ::
          new MutableRampObject("Custom Crate", 150, 0.5, 0.5, "crate.gif", true) ::
          new ScalaRampObject("Refrigerator", 400, 0.5, 0.5, "fridge.gif") ::
          new ScalaRampObject("Textboox", 10, 0.5, 0.5, "phetbook.gif") ::
          new ScalaRampObject("Big Crate", 300, 0.5, 0.5, "crate.gif") ::
          new CustomTextRampObject("Mystery Object", 300, 0.5, 0.5, "mystery-box.png", false) ::
          Nil
  val iconsPerRow = 4

  val appliedForceColor = Color.orange
  val gravityForceColor = Color.blue
  val normalForceColor = Color.yellow
  val frictionForceColor = Color.red
  val totalForceColor = Color.pink
}