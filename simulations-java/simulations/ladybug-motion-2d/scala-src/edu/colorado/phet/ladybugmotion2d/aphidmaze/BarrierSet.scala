package edu.colorado.phet.ladybugmotion2d.model.aphidmaze

import _root_.edu.colorado.phet.common.phetcommon.math.MathUtil
import _root_.edu.colorado.phet.common.phetcommon.view.util.RectangleUtils
import edu.colorado.phet.ladybugmotion2d.aphidmaze.MazeGenerator
import edu.colorado.phet.ladybugmotion2d.aphidmaze.Wall
import java.awt.geom.{Line2D, Rectangle2D}
import scala.collection.mutable.ArrayBuffer
import edu.colorado.phet.scalacommon.Predef._
import java.lang.Math._
import scalacommon.util.Observable
import scalacommon.math.Vector2D

class BarrierSet extends Observable {
  val rectangles = new ArrayBuffer[Rectangle2D]
  val lines = new ArrayBuffer[Line2D.Double]
  private var _dim = 0

  def getDim = _dim

  def getBounds = {
    val lineBounds = for (line <- lines) yield line.getBounds2D
    val bounds = RectangleUtils.union(lineBounds.toArray)
    if (bounds != null)
      bounds
    else
      new Rectangle2D.Double(0, 0, 1, 1)
  }

  def update(ladybug: Ladybug) = {
    //    lines.foreach((line:Line2D.Double)=>{
    //      line.x1 =line.x1+Math.random*0.01
    //      line.y1 =line.y1+Math.random*0.01
    //    })
  }

  def crossedBarrier(start: Vector2D, end: Vector2D): Boolean = {
    lines.foldLeft(false)((value: Boolean, cur: Line2D.Double) => crossed(cur, start, end) || value)
  }

  def crossed(line: Line2D.Double, start: Vector2D, end: Vector2D) = {
    val intersection = MathUtil.getLineSegmentsIntersection(line, new Line2D.Double(start, end))
    !intersection.getX.isNaN
  }

  def containsPoint(pt: Vector2D) = {
    rectangles.foldLeft(false)((value: Boolean, cur: Rectangle2D) => cur.contains(pt) || value)
  }
  setDim(10)

  def setDim(dim: Int) = {
    if (dim != _dim) {
      lines.clear()

      val mg = new MazeGenerator(dim)

      for (w <- mg.walls) {
        lines += toLine(w)
      }
      _dim = dim
      notifyListeners
    }
  }

  def toRectangle(w: Wall): Rectangle2D = {
    new Rectangle2D.Double(w.x, w.y, w.dx + 0.1, w.dy + 0.1)
  }

  def toLine(w: Wall): Line2D.Double = {
    new Line2D.Double(w.x, w.y, w.x + w.dx, w.y + w.dy)
  }

}