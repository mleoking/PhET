package edu.colorado.phet.scalacommon.math

import java.awt.geom.Line2D

class Vector2D(_x: Double, _y: Double) {
  val x = _x
  val y = _y

  override def toString = "x=" + x + ", y=" + y

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

  def +(that: Vector2D) = new Vector2D(that.x + x, that.y + y)

  def -(that: Vector2D) = new Vector2D(x - that.x, y - that.y)

  def *(scale: Double) = new Vector2D(x * scale, y * scale)

  def /(scale: Double) = new Vector2D(x / scale, y / scale)

  def rotate(angle: Double) = new Vector2D(getAngle + angle)

  def getAngle = java.lang.Math.atan2(y, x)

  def magnitude = java.lang.Math.sqrt(x * x + y * y);

  def dot(that: Vector2D) = x * that.x + y * that.y;
}

object TestVector2D {
  def main(args: Array[String]) = {
    val a = new Vector2D(3, 2)
    val b = new Vector2D(5, 5)
    println(a)
    println(a - b)
  }
}