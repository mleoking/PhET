package edu.colorado.phet.motionseries.sims.coordinateframes

import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.motionseries.model._
import java.awt.image.BufferedImage
import java.awt.{TexturePaint, Graphics2D, Color, Paint}
import edu.colorado.phet.motionseries.Predef._
import java.awt.geom.Rectangle2D

/**
 * @author Sam Reid
 */

class CoordinateFrameVectors {
}

//Vector Components
//Will be used in Coordinate Frames tab, and will need updating to match refactors of model if/when used

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
                          componentUnitVector: Vector2DModel,
                          painter: (Vector2D, Color) => Paint,
                          modifier: String,
                          labelAngle: Double) extends VectorComponent(target, motionSeriesObject, componentUnitVector, painter, modifier, labelAngle) {
  //TODO: When the object changes its angle, we should notify our listeners, since they depend on the angle
}

class ParallelComponent(target: MotionSeriesObjectVector,
                        motionSeriesObject: MotionSeriesObject)
        extends AngleBasedComponent(target, motionSeriesObject, new Vector2DModel(new Vector2D(motionSeriesObject.getAngle)), (a, b) => b, "symbols.parallel".translate, target.labelAngle) //http://www.fileformat.info/info/unicode/char/2225/index.htm

class PerpendicularComponent(target: MotionSeriesObjectVector,
                             motionSeriesObject: MotionSeriesObject)
        extends AngleBasedComponent(target, motionSeriesObject, new Vector2DModel(new Vector2D(motionSeriesObject.getAngle + java.lang.Math.PI / 2)), (a, b) => b, "symbols.perpendicular".translate, target.labelAngle) //http://www.fileformat.info/info/unicode/char/22a5/index.htm


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