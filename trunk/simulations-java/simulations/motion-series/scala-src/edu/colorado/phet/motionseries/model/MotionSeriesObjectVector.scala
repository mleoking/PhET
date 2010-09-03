package edu.colorado.phet.motionseries.model

import edu.colorado.phet.motionseries.graphics.PointOfOriginVector
import java.awt.{Paint, Color}
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.motionseries.Predef._

class Vector(val color: Color,
             val name: String,
             val abbreviation: String,
             private val _vector2DModel: Vector2DModel,
             val painter: (Vector2D, Color) => Paint,
             val labelAngle: Double) {
  val visible = new SMutableBoolean(true)
  if (_vector2DModel == null) throw new RuntimeException("null vector2d model")
  if (painter == null) throw new RuntimeException("null painter")

  def vector2DModel = _vector2DModel

  def angle = vector2DModel().angle

  def setVisible(vis: Boolean) = visible.setValue(vis)

  def getPaint = painter(vector2DModel(), color)

  def html = "force.abbreviation.html.pattern.abbrev".messageformat(abbreviation)
}

//Observable object in MVC pattern
class Vector2DModel(private var _value: Vector2D) extends Observable {
  //Create a Vector2DModel with x=y=0
  def this(x: Double, y: Double) = this (new Vector2D(x, y))

  def this() = this (0, 0)

  def value = _value

  def value_=(_value:Vector2D) = {
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
                               abbreviation: String,
                               val bottomPO: Boolean, //shows point of origin at the bottom when in that mode
                               vector2DModel: Vector2DModel,
                               painter: (Vector2D, Color) => Paint,
                               labelAngle: Double)
        extends Vector(color, name, abbreviation, vector2DModel, painter, labelAngle) with PointOfOriginVector {
  def getPointOfOriginOffset(defaultCenter: Double) = if (bottomPO) 0.0 else defaultCenter
}

class VectorComponent(target: MotionSeriesObjectVector,
                      motionSeriesObject: MotionSeriesObject,
                      componentUnitVector: Vector2DModel, //Can change, e.g., if the ramp rotates
                      painter: (Vector2D, Color) => Paint,
                      modifier: String,
                      labelAngle: Double)
        extends MotionSeriesObjectVector(target.color, target.name, target.abbreviation + modifier, target.bottomPO, target.vector2DModel, painter, labelAngle) {
  override def vector2DModel = {
    val d = componentUnitVector.value
    new Vector2DModel(d * (super.vector2DModel.apply() dot d))
  }
}