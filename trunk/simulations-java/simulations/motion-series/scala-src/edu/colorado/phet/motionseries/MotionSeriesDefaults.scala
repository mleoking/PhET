package edu.colorado.phet.motionseries

import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import java.awt.Color
import java.awt.geom.Rectangle2D
import edu.colorado.phet.motionseries.model.{MutableMotionSeriesObject, CustomTextMotionSeriesObject, MotionSeriesObject}

object MotionSeriesDefaults {
  val CLEAR_BUTTON_VISIBILITY_THRESHOLD_JOULES = 4000 * 1.5

  val FBD_DIALOG_WIDTH = 300
  val FBD_DIALOG_HEIGHT = FBD_DIALOG_WIDTH
  val FBD_DIALOG_NODE_WIDTH = FBD_DIALOG_WIDTH
  val FBD_DIALOG_NODE_HEIGHT = FBD_DIALOG_NODE_WIDTH
  val FBD_INSET = 10

  val MAX_ANGLE = 80.toRadians

  def rampIndicatorFont = new PhetFont(13, true)

  val dialogBackground = new Color(192, 192, 192, 245)
  val dialogBorder = Color.darkGray

  val MAX_CHART_DISPLAY_TIME = 20.0
  val MAX_RECORD_TIME = MAX_CHART_DISPLAY_TIME
  val defaultRampAngle = 30.0.toRadians
  val rampRobotForce = 500
  val forcesAndMotionRobotForce = 500

  val fullScreenArea = new StageContainerArea() {
    def getBounds(w: Double, h: Double) = new Rectangle2D.Double(0, 0, w, h)
  }
  val oneGraphArea = new StageContainerArea() {
    def getBounds(w: Double, h: Double) = new Rectangle2D.Double(0, 0, w, h / 2)
  }
  val forceEnergyGraphArea = new StageContainerArea() {
    def getBounds(w: Double, h: Double) = new Rectangle2D.Double(0, 0, w, h / 3)
  }
  val forceMotionArea = fullScreenArea
  val forceMotionFrictionArea = fullScreenArea
  val robotMovingCompanyRampArea = fullScreenArea

  val VIEWPORT_X = -11
  val VIEWPORT_W_RAMP = 23
  val VIEWPORT_W_FORCES = 22

  val rampIntroViewport = new Rectangle2D.Double(VIEWPORT_X, -6, VIEWPORT_W_RAMP, 16)
  val oneGraphViewport = new Rectangle2D.Double(VIEWPORT_X, -1, VIEWPORT_W_RAMP, 8)
  val forceEnergyGraphViewport = new Rectangle2D.Double(VIEWPORT_X, -1, VIEWPORT_W_RAMP, 6)
  val robotMovingCompanyRampViewport = new Rectangle2D.Double(VIEWPORT_X, -11, VIEWPORT_W_RAMP, 21)

  val forceMotionGraphViewport = new Rectangle2D.Double(VIEWPORT_X, -1, VIEWPORT_W_FORCES, 4)
  val forceMotionViewport = rampIntroViewport
  val forceMotionFrictionViewport = new Rectangle2D.Double(VIEWPORT_X, -7, VIEWPORT_W_FORCES, 16.5)
  val movingManIntroViewport = new Rectangle2D.Double(VIEWPORT_X, -1, VIEWPORT_W_FORCES, 5)

  //how far away the vector labels can be from the tip, in world coordinates
  val FBD_LABEL_MAX_OFFSET = 500
  val BODY_LABEL_MAX_OFFSET = 3

  val FRICTIONLESS_DEFAULT = false
  val BOUNCE_DEFAULT = false

  val MIN_X = -10.0
  val MAX_X = 10.0

  val worldDefaultScale = 1.0

  val worldWidth = (1024 * worldDefaultScale).toInt
  val worldHeight = (768 * worldDefaultScale).toInt

  val MAX_APPLIED_FORCE = 500.0 * 2

  val freeBodyDiagramWidth = 2000 // Full width (not distance from origin to edge) in Newtons
  val PLAY_AREA_FORCE_VECTOR_SCALE = 0.005 //scale factor when converting from Newtons to meters in the play area
  val PLAY_AREA_VELOCITY_VECTOR_SCALE = 0.5
  val PLAY_AREA_ACCELERATION_VECTOR_SCALE = 0.03

  val DT_DEFAULT = 30 / 1000.0
  val DELAY = 25

  val SKY_GRADIENT_BOTTOM = new Color(250, 250, 255)
  val EARTH_COLOR = new Color(200, 240, 200)

  val earthGravity = 9.8
  val moonGravity = 1.0 / 6.0 * earthGravity
  val jupiterGravity = earthGravity * 2.5
  val sliderMaxGravity = 30.0

  val crateMass = 100.0
  val crateHeight = 1.5

  import edu.colorado.phet.motionseries.MotionSeriesResources._

  val crate = new MotionSeriesObject("object.small-crate".translate, crateMass, 0.3, 0.5, crateHeight, "crate.gif".literal, "crate_crashed.gif".literal, 200)
  val cabinet = new MotionSeriesObject("object.file-cabinet".translate, 50, 0.2, 0.5, 2.25, "cabinet.gif".literal, "cabinet_crashed.gif".literal, 100)
  val ollie = new MotionSeriesObject("object.dog".translate, 25, 0.5, 0.5, 1.25, "ollie.gif".literal, "ollie.gif".literal, 500) //ollie doesn't crash, use same image
  val fridge = new MotionSeriesObject("object.refrigerator".translate, 200, 0.2, 0.5, 2.75, "fridge.gif".literal, "fridge_crashed.gif".literal, 650)
  val book = new MotionSeriesObject("object.textbook".translate, 10, 0.2, 0.4, 1, "phetbook.gif".literal, "phetbook_crashed.gif".literal, 20)
  val mystery = new CustomTextMotionSeriesObject("object.mystery-object".translate, 123, 0.2, 0.3, 2, "mystery-box.png".literal, "mystery-box_crashed.png".literal, 600, "mystery-box.png".literal, false)
  val custom = new MutableMotionSeriesObject("object.custom-crate".translate, 150.0, 0.3, 0.5, -1.0, "crate.gif".literal, "crate_crashed.gif".literal, 300, "crate_custom.gif".literal, true) //height is determined dynamically in MutableRampObject

  val objects = crate :: cabinet :: ollie :: fridge ::
          book :: mystery ::
          Nil
  val iconsPerRow = 4

  val objectsForForce1DGame = cabinet :: ollie :: book :: Nil

  lazy val movingMan = new MotionSeriesObject("object.moving-man".translate, 85, 0.3, 0.5, 2.8, //is some empty padding in the image? looks better at large size, with a 20m wide play area
    "moving-man/moving-man-standing.gif".literal, 1000)

  val wall = new MotionSeriesObject("wall".literal, 1000, 1000, 1000, 3.5, "wall.jpg".literal, 100)
  val SPRING_HEIGHT = 0.6
  val SPRING_WIDTH = 1.0
  val houseBack = new MotionSeriesObject("house".literal, 1000, 1000, 1000, 5, "robotmovingcompany/house-back.png".literal, 100) //back layer of house graphic
  val house = new MotionSeriesObject("house".literal, 1000, 1000, 1000, 5, "robotmovingcompany/house.png".literal, 100)
  val door = new MotionSeriesObject("door".literal, 1000, 1000, 1000, 2, "robotmovingcompany/door.gif".literal, 100)
  val doorBackground = new MotionSeriesObject("door.background".literal, 1000, 1000, 1000, 2, "robotmovingcompany/door-background.gif".literal, 100)

  def wallWidth = wall.width

  import java.awt.Color._

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
  val sumForceColor = pink //used to be myGreen
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

trait StageContainerArea {
  def getBounds(w: Double, h: Double): Rectangle2D
}