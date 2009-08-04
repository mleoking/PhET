package edu.colorado.phet.therampscala

import java.awt.Color

object RampDefaults {
  val defaultRampAngle = 30.0.toRadians
  //how far away the vector labels can be from the tip, in world coordinates
  val FBD_LABEL_MAX_OFFSET = 500
  val BODY_LABEL_MAX_OFFSET = 3

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
  import RampResources._
  val objects = new ScalaRampObject("object.file-cabinet".translate, 50, 0.2, 0.5, 2.25, "cabinet.gif".literal, 100) ::
          new ScalaRampObject("object.dog".translate, 25, 0.5, 0.5, 1.25, "ollie.gif".literal, 500) ::
          new ScalaRampObject("object.small-crate".translate, 100, 0.3, 0.5, 1.5, "crate.gif".literal, 200) ::
          new MutableRampObject("object.custom-crate".translate, 150, 0.3, 0.5, -1, "crate.gif".literal, 300, "crate_custom.gif".literal, true) :: //height is determined dynamically in MutableRampObject
          new ScalaRampObject("object.refrigerator".translate, 200, 0.2, 0.5, 2.75, "fridge.gif".literal, 650) ::
          new ScalaRampObject("object.textbook".translate, 10, 0.2, 0.4, 1, "phetbook.gif".literal, 20) ::
          new ScalaRampObject("object.piano".translate, 400, 0.3, 0.5, 3.5, "piano.png".literal, 1000) ::
          new CustomTextRampObject("object.mystery-object".translate, 300, 0.5, 0.5, 2, "mystery-box.png".literal, 600, "mystery-box.png".literal, false) ::
          Nil
  val iconsPerRow = 4

  val wall = new ScalaRampObject("wall".literal, 1000, 1000, 1000, 3.5, "wall.jpg".literal, 100)
  val house = new ScalaRampObject("house".literal, 1000, 1000, 1000, 5, "robotmovingcompany/house.gif".literal, 100)

  def wallWidth = wall.width

  import Color._

  val myGold = new Color(255,235,0)
  val myBrickRed = new Color(185,80,50)

  val myGreen = new Color(0.0f, 0.8f, 0.1f);
  val lightBlue = new Color(160, 220, 255);
  val drabYellow = new Color(190, 190, 0);
  val myOrange = new Color(236, 153, 55)

  val appliedForceColor = myOrange
  val gravityForceColor = new Color(50, 130, 215)
  val normalForceColor = myGold
  val frictionForceColor = red
  val totalForceColor = pink //used to be myGreen
  val wallForceColor = myBrickRed

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