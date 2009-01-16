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
        model.ladybug.setAngle(model.ladybug.getAngle + PI+(random*0.8-0.4)*PI/2)
        step
        step
      }
    }
  }
  val CIRCULAR = new MotionType("circular") {
    def update(dt: Double, model: LadybugModel) = {}
  }
  val ELLIPSE = new MotionType("ellipse") {
    def update(dt: Double, model: LadybugModel) = {}
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