package edu.colorado.phet.movingman.ladybug

case class MotionType(name: String)
object LadybugMotionModel {
  val MANUAL = new MotionType("manual")
  val LINEAR = new MotionType("linear")
  val CIRCULAR = new MotionType("circular")
  val ELLIPSE = new MotionType("ellipse")
}

class LadybugMotionModel extends ObservableS {
  private var _motionType = LadybugMotionModel.MANUAL

  def motion: MotionType = _motionType

  def motion_=(x: MotionType) = {
    _motionType = x
    println("motion type: " + _motionType)
    notifyListeners
  }
}