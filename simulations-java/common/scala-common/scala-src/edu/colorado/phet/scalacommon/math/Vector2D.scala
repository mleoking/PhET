package edu.colorado.phet.scalacommon.math

import java.awt.geom.{Point2D, Line2D}

/**
 * Immutable Vector class for simplified vector arithmetic.
 * @author Sam Reid
 */
class Vector2D(val x: Double, val y: Double) {

  def this() = this (0, 0)

  /**
   * Creates a vector in the direction of the given line segment
   */
  def this(line: Line2D) = this (
    line.getP2.getX - line.getP1.getX,
    line.getP2.getY - line.getP1.getY)

  /**
   * Returns a unit vector in the specified direction
   */
  def this(angle: Double) = this (Math.cos(angle), Math.sin(angle))

  def +(vector: Vector2D) = new Vector2D(vector.x + x, vector.y + y)

  def -(vector: Vector2D) = new Vector2D(x - vector.x, y - vector.y)

  def *(scale: Double) = new Vector2D(x * scale, y * scale)

  def /(scale: Double) = new Vector2D(x / scale, y / scale)

  def rotate(angle: Double) = new Vector2D(this.angle + angle)*magnitude

  def dot(vector: Vector2D) = x * vector.x + y * vector.y

  lazy val angle = java.lang.Math.atan2(y, x)
  lazy val magnitude = java.lang.Math.sqrt(x * x + y * y)
  lazy val normalize = this / this.magnitude
  override lazy val toString = "x=" + x + ", y=" + y
  override lazy val hashCode = new Point2D.Double(x,y).hashCode

  override def equals(obj: Any) = {
    obj match {
      case a:Vector2D => a.x==x && a.y==y
      case _ => false
    }
  }
}

object TestVector2D {
  def main(args: Array[String]) = {
    val a = new Vector2D(3, 2)
    val b = new Vector2D(5, 5)
    println(a)
    println(a - b)
  }
}