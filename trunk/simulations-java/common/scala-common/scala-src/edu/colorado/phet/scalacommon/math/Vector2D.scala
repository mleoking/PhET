package edu.colorado.phet.scalacommon.math


import java.awt.geom.{Point2D, Line2D}

class Vector2D(val x: Double, val y: Double) {

  override def toString = "x=" + x + ", y=" + y

  def this() = this (0, 0)

  /**
   * Creates a vector in the direction of the given line segment
   */
  def this(line: Line2D) = this (
    line.getP2.getX - line.getP1.getX,
    line.getP2.getY - line.getP1.getY)

  lazy val getAngle = java.lang.Math.atan2(y, x)
  lazy val magnitude = java.lang.Math.sqrt(x * x + y * y)
  lazy val normalize = this / this.magnitude

  /**
   * Returns a unit vector in the specified direction
   */
  def this(angle: Double) = this (Math.cos(angle), Math.sin(angle))

  def +(that: Vector2D) = new Vector2D(that.x + x, that.y + y)

  def -(that: Vector2D) = new Vector2D(x - that.x, y - that.y)

  def *(scale: Double) = new Vector2D(x * scale, y * scale)

  def /(scale: Double) = new Vector2D(x / scale, y / scale)

  def rotate(angle: Double) = new Vector2D(getAngle + angle)*magnitude

  def dot(that: Vector2D) = x * that.x + y * that.y

  override def hashCode = new Point2D.Double(x,y).hashCode

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