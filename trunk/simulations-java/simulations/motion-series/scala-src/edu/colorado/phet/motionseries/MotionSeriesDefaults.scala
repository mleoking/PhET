package edu.colorado.phet.motionseries

import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import java.awt.Color
import java.awt.geom.Rectangle2D
import edu.colorado.phet.motionseries.model.{MutableMotionSeriesObject, CustomTextMotionSeriesObject, MotionSeriesObject}
import edu.colorado.phet.motionseries.sims.theramp.StageContainerArea

object MotionSeriesDefaults {
  def rampIndicatorFont = new PhetFont(13, true)

  val MAX_CHART_DISPLAY_TIME = 20.0
  val MAX_RECORD_TIME = 20.0
  val defaultRampAngle = 30.0.toRadians

  val fullScreenArea = new StageContainerArea() {
    def getBounds(w: Double, h: Double) = new Rectangle2D.Double(0, 0, w, h)
  }
  val forceGraphArea = new StageContainerArea() {
    def getBounds(w: Double, h: Double) = new Rectangle2D.Double(0, 0, w, h / 2)
  }
  val forceEnergyGraphArea = new StageContainerArea() {
    def getBounds(w: Double, h: Double) = new Rectangle2D.Double(0, 0, w, h / 3)
  }
  val forceMotionArea = new StageContainerArea() {
    def getBounds(w: Double, h: Double) = new Rectangle2D.Double(0, 0, w, h)
  }
  val forceMotionFrictionArea = new StageContainerArea() {
    def getBounds(w: Double, h: Double) = new Rectangle2D.Double(0, 0, w, h)
  }

  val defaultViewport = new Rectangle2D.Double(-11, -6, 23, 16)
  val forceGraphViewport = new Rectangle2D.Double(-11, -1, 23, 8)
  val forceEnergyGraphViewport = new Rectangle2D.Double(-11, -1, 23, 6)

  val forceMotionGraphViewport = new Rectangle2D.Double(-11, -1, 22, 4)
  val forceMotionViewport = new Rectangle2D.Double(-11, -7, 22, 11)
  val forceMotionFrictionViewport = new Rectangle2D.Double(-11, -8, 22, 12)
  val movingManIntroViewport = new Rectangle2D.Double(-11, -5, 22, 12)

  //how far away the vector labels can be from the tip, in world coordinates
  val FBD_LABEL_MAX_OFFSET = 500
  val BODY_LABEL_MAX_OFFSET = 3

  val FRICTIONLESS_DEFAULT = false
  val BOUNCE_DEFAULT = false

  val MIN_X = -10.0
  val MAX_X = 10.0

  //  val worldDefaultScale = 0.81
  //  val worldDefaultScale = 0.75
  val worldDefaultScale = 1.0

  val worldWidth = (1024 * worldDefaultScale).toInt
  val worldHeight = (768 * worldDefaultScale).toInt

  val MAX_APPLIED_FORCE = 500.0

  val freeBodyDiagramWidth = 2000 // Full width (not distance from origin to edge) in Newtons
  val PLAY_AREA_FORCE_VECTOR_SCALE = 0.005 //scale factor when converting from Newtons to meters in the play area
  val PLAY_AREA_VELOCITY_VECTOR_SCALE = 0.5
  val PLAY_AREA_ACCELERATION_VECTOR_SCALE = 0.03

  val DT_DEFAULT = 30 / 1000.0
  val DELAY = 0 //there's a wait step, see the top of RampApplication.scala in AbstractRampModule

  val SKY_GRADIENT_BOTTOM = new Color(250, 250, 255)
  val EARTH_COLOR = new Color(200, 240, 200)

  val earthGravity = 9.8
  val moonGravity = 1.0 / 6.0 * earthGravity
  val jupiterGravity = earthGravity * 2.5
  val sliderMaxGravity = 30.0

  //ScalaRampObject(name,mass,kineticFriction,staticFriction,image)
  import edu.colorado.phet.motionseries.MotionSeriesResources._
  val objects = new MotionSeriesObject("object.file-cabinet".translate, 50, 0.2, 0.5, 2.25, "cabinet.gif".literal, 100) ::
          new MotionSeriesObject("object.dog".translate, 25, 0.5, 0.5, 1.25, "ollie.gif".literal, 500) ::
          new MotionSeriesObject("object.small-crate".translate, 100, 0.3, 0.5, 1.5, "crate.gif".literal, 200) ::
          new MutableMotionSeriesObject("object.custom-crate".translate, 150, 0.3, 0.5, -1, "crate.gif".literal, 300, "crate_custom.gif".literal, true) :: //height is determined dynamically in MutableRampObject
          new MotionSeriesObject("object.refrigerator".translate, 200, 0.2, 0.5, 2.75, "fridge.gif".literal, 650) ::
          new MotionSeriesObject("object.textbook".translate, 10, 0.2, 0.4, 1, "phetbook.gif".literal, 20) ::
          new MotionSeriesObject("object.piano".translate, 400, 0.3, 0.5, 3.5, "piano.png".literal, 1000) ::
          new CustomTextMotionSeriesObject("object.mystery-object".translate, 300, 0.5, 0.5, 2, "mystery-box.png".literal, 600, "mystery-box.png".literal, false) ::
          Nil
  val iconsPerRow = 4

  lazy val movingMan = new MotionSeriesObject("object.moving-man".translate, 85, 0.3, 0.5, 2.8, //is some empty padding in the image? looks better at large size, with a 20m wide play area
    "moving-man/moving-man-standing.gif".literal, 1000)

  val wall = new MotionSeriesObject("wall".literal, 1000, 1000, 1000, 3.5, "wall.jpg".literal, 100)
  val SPRING_HEIGHT = 0.6
  val SPRING_WIDTH = 1.0
  val house = new MotionSeriesObject("house".literal, 1000, 1000, 1000, 5, "robotmovingcompany/house.gif".literal, 100)

  def wallWidth = wall.width

  import Color._

  val myGold = new Color(255, 235, 0)
  val myBrickRed = new Color(185, 80, 50)
  val myGreen = new Color(0.0f, 0.8f, 0.1f)
  val myDrabYellow = new Color(190, 190, 0)
  val myOrange = new Color(236, 153, 55)
  val myLightBlue = new Color(50, 130, 215)

  val appliedForceColor = myOrange
  val gravityForceColor = myLightBlue
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

  val accelerationColor = magenta
  val velocityColor = red
  val positionColor = blue

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