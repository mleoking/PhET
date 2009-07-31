package edu.colorado.phet.therampscala

import java.awt.Color

object RampDefaults {
  //how far away the vector labels can be from the tip, in world coordinates
  val FBD_LABEL_MAX_OFFSET=500
  val BODY_LABEL_MAX_OFFSET=3
  
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
  val objects = new ScalaRampObject("File Cabinet", 50, 0.2, 0.5, 2.25, "cabinet.gif", 100) ::
          new ScalaRampObject("Sleepy Dog", 25, 0.5, 0.5, 1.25, "ollie.gif", 500) ::
          new ScalaRampObject("Small Crate", 100, 0.3, 0.5, 1.5, "crate.gif", 200) ::
          new MutableRampObject("Custom Crate", 150, 0.3, 0.5, -1, "crate.gif", 300, "crate_custom.gif", true) :: //height is determined dynamically in MutableRampObject
          new ScalaRampObject("Refrigerator", 200, 0.2, 0.5, 2.75, "fridge.gif", 650) ::
          new ScalaRampObject("Textbook", 10, 0.2, 0.4, 1, "phetbook.gif", 20) ::
          new ScalaRampObject("Piano", 400, 0.3, 0.5, 3.5, "piano.png", 1000) ::
          new CustomTextRampObject("Mystery Object", 300, 0.5, 0.5, 2, "mystery-box.png", 600, "mystery-box.png", false) ::
          Nil
  val iconsPerRow = 4

  val wall = new ScalaRampObject("Wall", 1000, 1000, 1000, 3.5, "wall.jpg", 100)
  val house = new ScalaRampObject("Wall", 1000, 1000, 1000, 5, "robotmovingcompany/house.gif", 100)

  def wallWidth = wall.width

  import Color._

  val myGreen = new Color(0.0f, 0.8f, 0.1f);
  val lightBlue = new Color(160, 220, 255);
  val drabYellow = new Color(190, 190, 0);
  val myOrange = new Color(236, 153, 55)

  val appliedForceColor = myOrange
  val gravityForceColor = new Color(50, 130, 215)
  val normalForceColor = magenta
  val frictionForceColor = red
  val totalForceColor = pink //used to be myGreen
  val wallForceColor = drabYellow

  val appliedWorkColor = appliedForceColor
  val frictionWorkColor = frictionForceColor
  val gravityWorkColor = gravityForceColor
  val totalWorkColor = myGreen

  val totalEnergyColor = appliedWorkColor
  val kineticEnergyColor = totalWorkColor
  val potentialEnergyColor = gravityWorkColor
  val thermalEnergyColor = frictionWorkColor

  /**
   * W_grav and deltaPE should be the same color:  Blue (sky blue, sky-high --get it?)
   * W_fric and deltaThermal should be same color: Red (red hot)
   * W_net and deltaKE should be same color: green (green for go)
   * x-W_app and deltaTotalEnergy should be same color: Yellow (yellow for... I don't know, it just has to be different than blue, red, green).
   */
  val accelval = black
  val velval = black
  val positionval = black

}