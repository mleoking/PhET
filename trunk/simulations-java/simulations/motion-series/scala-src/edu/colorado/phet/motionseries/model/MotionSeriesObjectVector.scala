package edu.colorado.phet.motionseries.model

import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.colorado.phet.motionseries.graphics.{Vector, PointOfOriginVector}
import java.awt.geom.Rectangle2D
import java.awt.{Paint, TexturePaint, Color, Graphics2D}
import java.awt.image.BufferedImage
import java.lang.Math._
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

  override def toString = "Vector2DModel: "+_value
}

class MotionSeriesObjectVector(color: Color,
                               name: String,
                               override val abbreviation: String,
                               val bottomPO: Boolean, //shows point of origin at the bottom when in that mode
                               __vector2DModel: Vector2DModel,
                               painter: (Vector2D, Color) => Paint,
                               labelAngle: Double)
        extends Vector(color, name, abbreviation, __vector2DModel, painter, labelAngle) with PointOfOriginVector {
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

object Paints {
  def generateTemplate(painter: (Graphics2D, Int, Int) => Unit) = {
    val imageWidth = 6
    val imageHeight = 6
    val texture = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)
    val graphics2D = texture.createGraphics
    val background = new Color(255, 255, 255)

    graphics2D.setColor(background)
    graphics2D.fillRect(0, 0, imageWidth, imageHeight)

    painter(graphics2D, imageWidth, imageHeight)
    new TexturePaint(texture, new Rectangle2D.Double(0, 0, texture.getWidth, texture.getHeight))
  }

  def horizontalStripes(a: Vector2D, color: Color) = {
    generateTemplate((g: Graphics2D, imageWidth: Int, imageHeight: Int) => {
      g.setColor(color)
      val stripeSize = 2
      g.fillRect(0, 0, imageWidth, stripeSize)
    })
  }

  def verticalStripes(a: Vector2D, color: Color) = {
    generateTemplate((g: Graphics2D, imageWidth: Int, imageHeight: Int) => {
      g.setColor(color)
      val stripeSize = 2
      g.fillRect(0, 0, stripeSize, imageHeight)
    })
  }
}

//Vector Components

class XComponent(target: MotionSeriesObjectVector,
                 motionSeriesObject: MotionSeriesObject,
                 coordinateFrame: CoordinateFrameModel,
                 labelAngle: Double)
        extends VectorComponent(target, motionSeriesObject, new Vector2DModel(new Vector2D(1, 0).rotate(coordinateFrame.angle)), Paints.horizontalStripes, "coordinates.x".translate, labelAngle) {
  //TODO: When the object changes its angle, we should notify our listeners, since they depend on the angle
}
class YComponent(target: MotionSeriesObjectVector,
                 motionSeriesObject: MotionSeriesObject,
                 coordinateFrame: CoordinateFrameModel,
                 labelAngle: Double)
        extends VectorComponent(target, motionSeriesObject, new Vector2DModel(new Vector2D(0, 1).rotate(coordinateFrame.angle)), Paints.verticalStripes, "coordinates.y".translate, labelAngle) {
  //TODO: When the object changes its angle, we should notify our listeners, since they depend on the angle
}

class AngleBasedComponent(target: MotionSeriesObjectVector,
                          motionSeriesObject: MotionSeriesObject,
                          getComponentUnitVector: Vector2DModel,
                          painter: (Vector2D, Color) => Paint,
                          modifier: String,
                          labelAngle: Double) extends VectorComponent(target, motionSeriesObject, getComponentUnitVector, painter, modifier, labelAngle) {
  //TODO: When the object changes its angle, we should notify our listeners, since they depend on the angle
}

class ParallelComponent(target: MotionSeriesObjectVector,
                        motionSeriesObject: MotionSeriesObject)
        extends AngleBasedComponent(target, motionSeriesObject, new Vector2DModel(new Vector2D(motionSeriesObject.getAngle)), (a, b) => b, "symbols.parallel".translate, target.labelAngle) //http://www.fileformat.info/info/unicode/char/2225/index.htm

class PerpendicularComponent(target: MotionSeriesObjectVector,
                             motionSeriesObject: MotionSeriesObject)
        extends AngleBasedComponent(target, motionSeriesObject, new Vector2DModel(new Vector2D(motionSeriesObject.getAngle + PI / 2)), (a, b) => b, "symbols.perpendicular".translate, target.labelAngle) //http://www.fileformat.info/info/unicode/char/22a5/index.htm