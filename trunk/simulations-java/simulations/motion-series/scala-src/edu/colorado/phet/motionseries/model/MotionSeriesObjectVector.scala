package edu.colorado.phet.motionseries.model

import edu.colorado.phet.motionseries.graphics.{Vector, PointOfOriginVector}
import java.awt.{Paint, Color}
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.util.Observable

//Observable object in MVC pattern
class Vector2DModel(private var _value: Vector2D) extends Observable {
  //Create a Vector2DModel with x=y=0
  def this(x: Double, y: Double) = this (new Vector2D(x, y))

  def this() = this (0, 0)

  def value = _value

  def setValue(_value: Vector2D) = {
    if (_value != this._value) {
      this._value = _value;
      notifyListeners()
    }
  }

  def magnitude = _value.magnitude

  def apply() = _value

  override def toString = "Vector2DModel: " + _value
}

class MotionSeriesObjectVector(color: Color,
                               name: String,
                               override val abbreviation: String,
                               val bottomPO: Boolean, //shows point of origin at the bottom when in that mode
                               vector2DModel: Vector2DModel,
                               painter: (Vector2D, Color) => Paint,
                               labelAngle: Double)
        extends Vector(color, name, abbreviation, vector2DModel, painter, labelAngle) with PointOfOriginVector {
  def getPointOfOriginOffset(defaultCenter: Double) = if (bottomPO) 0.0 else defaultCenter
}

class VectorComponent(target: MotionSeriesObjectVector,
                      motionSeriesObject: MotionSeriesObject,
                      getComponentUnitVector: Vector2DModel,
                      painter: (Vector2D, Color) => Paint,
                      modifier: String,
                      labelAngle: Double)
        extends MotionSeriesObjectVector(target.color, target.name, target.abbreviation + modifier, target.bottomPO, target._vector2DModel, painter, labelAngle) {
  override def vector2DModel = {
    val d = getComponentUnitVector.value
    new Vector2DModel(d * (super.vector2DModel.apply() dot d))
  }
}