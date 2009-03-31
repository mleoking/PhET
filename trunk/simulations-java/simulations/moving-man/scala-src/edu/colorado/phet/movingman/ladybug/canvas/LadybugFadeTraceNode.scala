package edu.colorado.phet.movingman.ladybug.canvas

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{Line2D, GeneralPath, Point2D}
import java.awt.{Point, BasicStroke, Color}
import java.lang.Math._
import model.{LadybugState, LadybugModel}

import phet.common.phetcommon.util.QuickProfiler
import scalacommon.math.Vector2D
import scalacommon.record.DataPoint
import scalacommon.util.Observable
import umd.cs.piccolo.PNode

class LadybugFadeTraceNode(model: LadybugModel, transform: ModelViewTransform2D, shouldBeVisible: () => Boolean, observable: Observable, maxFade: Double) extends LadybugTraceNode(model, transform, shouldBeVisible, observable) {
  update()
  val stroke = new BasicStroke(6, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f)

  val segmentCache = scala.collection.mutable.Map.empty[Key, PhetPPath]

  case class Key(x1: Int, y1: Int, x2: Int, y2: Int) {
    //default hash code might be expensive, it's showing up as a bottleneck in the profiler
    //using our own bumps it out of the top spot in the profiler
    override val hashCode = x1 + y1 * 123 + x2 * 7769 + y2 * 973612

    //this override saves a little bit of time according to the profiler
    override def equals(obj: Any) = {
      obj match {
        case a:Key=>a.x1==x1 && a.y1==y1 && a.x2==x2 && a.y2==y2
        case _ => false
      }
    }
  }

  def update() = {
    if (segmentCache != null) {

      val prof = new QuickProfiler("LadybugFadeTraceNode.update")
      //    removeAllChildren()
      implicit def historyToPoint(dataPoint: DataPoint[LadybugState]) = new Point2D.Float(dataPoint.state.position.x.toFloat, dataPoint.state.position.y.toFloat)

      val historyToShow = getHistoryToShow
      var hits = 0
      var misses = 0

      var unusedKeys = scala.collection.mutable.Set.empty[Key]
      unusedKeys ++= segmentCache.keySet.elements
      if (historyToShow.length >= 2) {

        val t = transform.modelToView(historyToShow(0))
        for (i <- 0 to (historyToShow.length - 2)) {
          val a = transform.modelToView(historyToShow(i))
          val b = transform.modelToView(historyToShow(i + 1))

          val key = Key(a.x, a.y, b.x, b.y)
          val dt = abs(model.getTime - historyToShow(i).time)

          unusedKeys -= key
          val color = toColor(dt, maxFade)
          try {
            //cache checks are very expensive, only do it once
            val cached = segmentCache(key)
            cached.setStrokePaint(color)
            //          println("Cache hit")
            hits = hits + 1
          } catch {
            case e: Exception => {
              val segment = new PhetPPath(new Line2D.Double(a, b), stroke, color)
              segmentCache(key) = segment
              addChild(segment)
              //          println("Cache miss")
              misses = misses + 1
            }
            case _ => {}
          }
        }

      }
      segmentCache --= unusedKeys

      //    prof.println() //up to 5ms at half-time up before caching
      //                     around 2.5ms at half-time after caching, without cache clearing
      // around 6.5ms at half time with cache clearing
      // around 1.5ms after changing tuple of Vector2D Vector2D to explicit Key case class
      //      println("prof: " + prof + ": hits=" + hits + ", misses=" + misses + ", keys=" + segmentCache.keySet)
      //      if (hits == 0 && misses >= 3)
      //        {
      //          println("why so many misses?")
      //        }
    }
  }

}
