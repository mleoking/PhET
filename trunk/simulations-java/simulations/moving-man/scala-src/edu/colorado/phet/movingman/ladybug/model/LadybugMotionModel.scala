package edu.colorado.phet.movingman.ladybug.model

import java.awt.geom.Rectangle2D
import LadybugUtil._
import java.lang.Math._

abstract case class MotionType(name: String) {
  def update(dt: Double, model: LadybugModel)

  def init(model: LadybugModel) = {}
}
object LadybugMotionModel {
  val MANUAL = new MotionType("manual") {
    def update(dt: Double, model: LadybugModel) = {}

    override def init(model: LadybugModel) = {
      model.initManual
    }
  }
  val LINEAR = new MotionType("linear") {
    val speed = 0.3 * 30 *0.7

    override def init(model: LadybugModel) = {
      model.ladybug.setVelocity(new Vector2D(model.ladybug.getAngle) * speed)
    }

    def update(dt: Double, model: LadybugModel) = {
      val angle = model.ladybug.getAngle

      val lastSample = if (model.samplePath.length > 0) model.samplePath(model.samplePath.length - 1).location else model.ladybug.getPosition
      //      val proposedPoint=new Vector2D(model.ladybug.getVelocity.getAngle) * speed+lastSample


      def step() = {
        model.ladybug.setVelocity(new Vector2D(model.ladybug.getVelocity.getAngle) * speed)
        model.ladybug.translate(model.ladybug.getVelocity * dt)
      }
      step
      var x = model.ladybug.getPosition.x
      var y = model.ladybug.getPosition.y
      var vx = model.ladybug.getVelocity.x
      var vy = model.ladybug.getVelocity.y
      var changed = false
      val bounds = model.getBounds()
      if (x > bounds.getMaxX && vx > 0) {
        vx = -abs(vx)
        x = bounds.getMaxX
      }
      if (x < bounds.getMinX && vx < 0) {
        vx = abs(vx)
        x = bounds.getMinX
      }

      if (y > bounds.getMaxY && vy > 0) {
        vy = -abs(vy)
        y = bounds.getMaxY
      }
      if (y < bounds.getMinY && vy < 0) {
        vy = abs(vy)
        y = bounds.getMinY
      }

      //      model.addSamplePoint(model.ladybug.getPosition+new Vector2D(vx,vy)*dt)
      //      model.setSamplePoint(new Vector2D(x,y))
      //      model.ladybug.setVelocity(new Vector2D(vx,vy))
      //      model.positionMode(dt)
      //      model.ladybug.setVelocity(new Vector2D(vx,vy))


      model.ladybug.setPosition(new Vector2D(x, y))
      model.ladybug.setVelocity(new Vector2D(vx, vy))
      model.ladybug.setAngle(model.ladybug.getVelocity.getAngle)
      model.ladybug.setAcceleration(model.average(model.getHistory.length - 15, model.getHistory.length - 1, model.estimateAcceleration))
      model.setSamplePoint(model.ladybug.getPosition)
    }
  }
  val CIRCULAR = new MotionType("circular") {
    def update(dt: Double, model: LadybugModel) = {
      val distFromCenter = model.ladybug.getPosition.magnitude
      val radius = 7.5
      val distFromRing = abs(distFromCenter - radius)

      val dx = radius - distFromCenter;
      val speed = 0.12 *0.7
      if (distFromRing > speed + 1E-6) {
        val velocity = new Vector2D(model.ladybug.getPosition.getAngle) * speed * (if (dx < 0) -1 else 1)
        //        model.ladybug.translate(velocity)
        model.setPenDown(true)
        model.setSamplePoint(model.ladybug.getPosition + velocity / dt)
        model.positionMode(dt)
      } else {
        model.setPenDown(false)
        //move in a circle
        val angle = model.ladybug.getPosition.getAngle
        val r = model.ladybug.getPosition.magnitude

        val delta0 = PI / 64 * 1.3 * dt * 30.0*0.7 //desired approximate deltaTheta
        val n = (PI * 2 / delta0).toInt //n deltaTheta=2 PI
        val newAngle = angle + 2 * PI / n
        model.ladybug.setPosition(new Vector2D(newAngle) * r)
        val velocity = new Vector2D(newAngle + PI / 2) * (newAngle - angle) / dt * r
        model.ladybug.setVelocity(velocity)
        model.ladybug.setAngle(velocity.getAngle)

        val accel = new Vector2D(newAngle + PI) * velocity.magnitude * velocity.magnitude / r
        model.ladybug.setAcceleration(accel)
        model.setSamplePoint(model.ladybug.getPosition)
      }


    }

    override def init(model: LadybugModel) = {
      model.clearSampleHistory
      model.resetMotion2DModel
    }
  }
  val ELLIPSE = new MotionType("ellipse") {
    var t = 0.0

    def update(dt: Double, model: LadybugModel) = {
      val a = 8
      val b = 5
      val pos = model.ladybug.getPosition
      val ladybugC = pos.x * pos.x / a * a + pos.y * pos.y / b * b

      val n = 79 * dt / 0.015*0.7
      t = t + 2 * PI / n.toInt

      def getPosition(t: Double) = new Vector2D(a * cos(t), b * sin(t))
      model.ladybug.setPosition(getPosition(t))

      val nextPos = getPosition(t + dt)
      val prevPos = getPosition(t - dt)

      def getVelocity(t: Double) = {
        val nextPos = getPosition(t + dt)
        val prevPos = getPosition(t - dt)
        (nextPos - prevPos) / dt
      }

      model.ladybug.setVelocity(getVelocity(t))
      model.ladybug.setAngle(model.ladybug.getVelocity.getAngle)

      model.ladybug.setAcceleration((getVelocity(t + dt) - getVelocity(t - dt)) / dt)
    }
  }
}

class LadybugMotionModel(model: LadybugModel) extends Observable {
  private var _motionType = LadybugMotionModel.MANUAL

  def motion: MotionType = _motionType

  def motion_=(x: MotionType) = {
    if (_motionType != x) {
      _motionType = x
      _motionType.init(model)
      notifyListeners
    }
  }

  def isExclusive() = _motionType != LadybugMotionModel.MANUAL

  def update(dt: Double, model: LadybugModel) = {
    _motionType.update(dt, model)
  }

  def resetAll() {
    motion = LadybugMotionModel.MANUAL
  }
}