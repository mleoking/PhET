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

  }
  val LINEAR = new MotionType("linear") {
    val speed = 0.3 * 30

    override def init(model: LadybugModel) = {
      model.ladybug.setVelocity(new Vector2D(model.ladybug.getAngle) * speed)
    }

    def update(dt: Double, model: LadybugModel) = {
      val angle = model.ladybug.getAngle


      def step = {
        model.ladybug.setVelocity(new Vector2D(model.ladybug.getVelocity.getAngle) * speed)
        model.ladybug.translate(model.ladybug.getVelocity * dt)
      }
      step
      var x = model.ladybug.getPosition.x
      var y = model.ladybug.getPosition.y
      var vx = model.ladybug.getVelocity.x
      var vy = model.ladybug.getVelocity.y
      var changed = false
      val bounds=model.getBounds()
      if (x > bounds.getMaxX && vx > 0) {
        vx = -abs(vx)
        x = bounds.getMaxX
      }
      if (x < bounds.getMinX && vx < 0) {
        vx = abs(vx)
        x = bounds.getMinX
      }

      if (y > bounds.getMaxY&& vy > 0) {
        vy = -abs(vy)
        y = bounds.getMaxY
      }
      if (y < bounds.getMinY && vy < 0) {
        vy = abs(vy)
        y = bounds.getMinY
      }

      model.ladybug.setPosition(new Vector2D(x, y))
      model.ladybug.setVelocity(new Vector2D(vx, vy))
      model.ladybug.setAngle(model.ladybug.getVelocity.getAngle)
      model.ladybug.setAcceleration(model.average(model.getHistory.length - 15, model.getHistory.length - 1, model.estimateAcceleration))
    }
  }
  val CIRCULAR = new MotionType("circular") {
    def update(dt: Double, model: LadybugModel) = {
      val distFromCenter = model.ladybug.getPosition.magnitude
      val radius = 7.5
      val distFromRing = abs(distFromCenter - radius)

      val dx = radius - distFromCenter;
      val speed = 0.3
      if (distFromRing > speed + 1E-6) {
        val velocity = new Vector2D(model.ladybug.getPosition.getAngle) * speed * (if (dx < 0) -1 else 1)
        model.ladybug.translate(velocity)
        //        println("tx")
      } else {
        //move in a circle
        val angle = model.ladybug.getPosition.getAngle
        val r = model.ladybug.getPosition.magnitude
        //        println("r="+r)

        val delta0 = PI / 64 * 1.3 //desired approximate deltaTheta
        val n = (PI * 2 / delta0).toInt //n deltaTheta=2 PI
        val newAngle = angle + 2 * PI / n
        //        println(model.getTime+"\t"+newAngle)
        model.ladybug.setPosition(new Vector2D(newAngle) * r)
        model.ladybug.setVelocity(model.average(model.getHistory.length - 3, model.getHistory.length - 1, model.estimateVelocity))
        model.ladybug.setAngle(model.ladybug.getVelocity.getAngle)
        model.ladybug.setAcceleration(model.average(model.getHistory.length - 15, model.getHistory.length - 1, model.estimateAcceleration))
      }
    }
  }
  val ELLIPSE = new MotionType("ellipse") {
    var t = 0.0

    def update(dt: Double, model: LadybugModel) = {
      val a = 8
      val b = 5
      val pos = model.ladybug.getPosition
      val ladybugC = pos.x * pos.x / a * a + pos.y * pos.y / b * b

      //      t = t + 0.08
      t = t + 2 * PI / 79
      model.ladybug.setPosition(new Vector2D(a * cos(t), b * sin(t)))

      model.ladybug.setVelocity(model.average(model.getHistory.length - 3, model.getHistory.length - 1, model.estimateVelocity))
      model.ladybug.setAngle(model.ladybug.getVelocity.getAngle)
      model.ladybug.setAcceleration(model.average(model.getHistory.length - 15, model.getHistory.length - 1, model.estimateAcceleration))
    }
  }
}

class LadybugMotionModel(model: LadybugModel) extends ObservableS {
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