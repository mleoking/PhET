package edu.colorado.phet.movingman.ladybug

import model.Vector2D
import scala.swing.Component
import java.awt.geom.Point2D

object LadybugUtil {
  implicit def vector2DToPoint(vector: Vector2D) = new Point2D.Double(vector.x, vector.y)

  implicit def pointToVector2D(point: Point2D) = new Vector2D(point.getX, point.getY)

  implicit def scalaSwingToAWT(component: Component) = component.peer
}