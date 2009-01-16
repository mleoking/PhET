package edu.colorado.phet.movingman.ladybug

import java.awt.geom.Rectangle2D
import LadybugUtil._
import java.lang.Math._

abstract case class MotionType(name: String) {
  def update(dt: Double, model: LadybugModel)
}
object LadybugMotionModel {
  val MANUAL = new MotionType("manual") {
    def update(dt: Double, model: LadybugModel) = {}
  }
  val LINEAR = new MotionType("linear") {
    def update(dt: Double, model: LadybugModel) = {
      val speed = 0.3
      def step = model.ladybug.translate(new Vector2D(model.ladybug.getAngle) * speed)
      step
      val bounds = new Rectangle2D.Double(-10, -10, 20, 20)
      if (!bounds.contains(model.ladybug.getPosition)) {
        model.ladybug.setAngle(model.ladybug.getAngle + PI + (random * 0.8 - 0.4) * PI / 2)
        step
        step
      }
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
      } else {
        //move in a circle
        val angle = model.ladybug.getPosition.getAngle
        val r = model.ladybug.getPosition.magnitude
        val newAngle = angle + PI / 64 * 1.3;
        model.ladybug.setPosition(new Vector2D(newAngle) * r)
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

      t = t + 0.08
      model.ladybug.setPosition(new Vector2D(a * cos(t), b * sin(t)))
    }
  }
}

class LadybugMotionModel extends ObservableS {
  private var _motionType = LadybugMotionModel.MANUAL

  def motion: MotionType = _motionType

  def motion_=(x: MotionType) = {
    _motionType = x
    println("motion type: " + _motionType)
    notifyListeners
  }

  def update(dt: Double, model: LadybugModel) = {
    _motionType.update(dt, model)
  }
}