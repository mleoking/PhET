package edu.colorado.phet.therampscala

import java.awt.Color

object RampDefaults {
  val MIN_X = -10
  val MAX_X = 10

  val freeBodyDiagramWidth = 2000 // Full width (not distance from origin to edge) in Newtons
  val PLAY_AREA_VECTOR_SCALE = 0.01 //scale factor when converting from Newtons to meters in the play area

  val DT_DEFAULT = 30 / 1000.0

  val SKY_GRADIENT_BOTTOM=new Color(250, 250, 255)

  //ScalaRampObject(name,mass (kg) ,kineticFriction,staticFriction,height (meters) image)
  val objects = new ScalaRampObject("File Cabinet", 75, 0.2, 0.5, 3, "cabinet.gif") ::
          new ScalaRampObject("Sleepy Dog", 20, 0.5, 0.5, 2, "ollie.gif") ::
          new ScalaRampObject("Small Crate", 150, 0.5, 0.5, 2, "crate.gif") ::
          new MutableRampObject("Custom Crate", 50, 0.5, 0.5, "crate.gif", true) ::
          new ScalaRampObject("Refrigerator", 400, 0.5, 0.5, 2, "fridge.gif") ::
          new ScalaRampObject("Textboox", 10, 0.5, 0.5, 0.5, "phetbook.gif") ::
          new ScalaRampObject("Big Crate", 300, 0.5, 0.5, 1, "crate.gif") ::
          new CustomTextRampObject("Mystery Object", 300, 0.5, 0.5, "mystery-box.png", false) ::
          Nil
  val iconsPerRow = 4

  val appliedForceColor = Color.orange
  val gravityForceColor = Color.blue
  val normalForceColor = Color.yellow
  val frictionForceColor = Color.red
  val totalForceColor = Color.pink
  val wallForceColor = Color.green
}