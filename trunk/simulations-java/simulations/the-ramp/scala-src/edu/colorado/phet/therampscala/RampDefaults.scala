package edu.colorado.phet.therampscala

import java.awt.Color

object RampDefaults {
  val MIN_X = -10
  val MAX_X = 10

  val MAX_APPLIED_FORCE = 500.0

  val freeBodyDiagramWidth = 2000 // Full width (not distance from origin to edge) in Newtons
  val PLAY_AREA_VECTOR_SCALE = 0.005 //scale factor when converting from Newtons to meters in the play area

  val DT_DEFAULT = 30 / 1000.0
  val DELAY = 30

  val SKY_GRADIENT_BOTTOM = new Color(250, 250, 255)
  val EARTH_COLOR = new Color(200, 240, 200)

  //ScalaRampObject(name,mass,kineticFriction,staticFriction,image)
  val objects = new ScalaRampObject("File Cabinet", 50, 0.2, 0.5, 2.25, "cabinet.gif") ::
          new ScalaRampObject("Sleepy Dog", 25, 0.5, 0.5, 1.25, "ollie.gif") ::
          new ScalaRampObject("Small Crate", 100, 0.3, 0.5, 1.5, "crate.gif") ::
          new MutableRampObject("Custom Crate", 150, 0.3, 0.5, -1, "crate.gif", "crate_custom.gif", true) :: //height is determined dynamically in MutableRampObject
          new ScalaRampObject("Refrigerator", 200, 0.2, 0.5, 2.75, "fridge.gif") ::
          new ScalaRampObject("Textbook", 10, 0.2, 0.4, 1, "phetbook.gif") ::
          new ScalaRampObject("Piano", 400, 0.3, 0.5, 3.5, "piano.png") ::
          new CustomTextRampObject("Mystery Object", 300, 0.5, 0.5, 2, "mystery-box.png", "mystery-box.png", false) ::
          Nil
  val iconsPerRow = 4

  val wall = new ScalaRampObject("Wall", 1000, 1000, 1000, 3.5, "wall.jpg")
  val house = new ScalaRampObject("Wall", 1000, 1000, 1000, 5, "robotmovingcompany/house.gif")

  def wallWidth = wall.width

  val appliedForceColor = Color.orange
  val gravityForceColor = Color.blue
  val normalForceColor = Color.yellow
  val frictionForceColor = Color.red
  val totalForceColor = Color.pink
  val wallForceColor = new Color(190, 190, 0)
}