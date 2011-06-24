package edu.colorado.phet.ladybugmotion2d.canvas

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{Ellipse2D, Point2D}
import java.lang.Math._
import edu.colorado.phet.ladybugmotion2d.model.{LadybugState, LadybugModel}
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.Predef._
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.recordandplayback.model.DataPoint
import java.lang.Boolean
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty

class LadybugDotTraceNode(model: LadybugModel, transform: ModelViewTransform2D, visible: ObservableProperty[Boolean], maxFade: Double)
        extends LadybugTraceNode(model, transform, visible) {
  val node = new PNode
  addChild(node)

  //have to cache pnodes for performance reasons
  val nodeCache = scala.collection.mutable.Map.empty[Vector2D, DotNode]

  update()

  var dotNodeIndex = 0

  class DotNode(val point: Vector2D, dt: Double) extends PNode {
    val path = new PhetPPath(new Ellipse2D.Double(point.getX - 5, point.getY - 5, 10, 10), toColor(dt, maxFade))
    addChild(path)

    val index = dotNodeIndex
    dotNodeIndex = dotNodeIndex + 1
    setVisible(index % 2 == 0)

    //only paint every other node
    def setDT(dt: Double) {
      path.setPaint(toColor(dt, maxFade))
    }
  }

  def update() = {
    implicit def historyToPoint(dataPoint: DataPoint[LadybugState]) = new Point2D.Float(dataPoint.getState.position.x.toFloat, dataPoint.getState.position.y.toFloat)

    //todo: some code duplicated with LadbyugFadeTraceNode
    var unusedKeys = scala.collection.mutable.Set.empty[Vector2D]
    unusedKeys ++= nodeCache.keySet.elements
    for ( i <- 0 until getHistoryToShow.size ) {
      val h = getHistoryToShow.get(i)
      val viewPt = transform.modelToView(h)
      val dt = abs(model.getTime - h.getTime)

      unusedKeys -= viewPt
      try {
        //cache checks are very expensive, only do it once
        nodeCache(viewPt).setDT(dt)
      }
      catch {
        case e: NoSuchElementException => {
          val dotNode = new DotNode(viewPt, dt)
          node.addChild(dotNode)
          nodeCache(viewPt) = dotNode
        }
        case _ => {}
      }

    }
    for ( a <- unusedKeys ) {
      node.removeChild(nodeCache(a))
    }
    nodeCache --= unusedKeys
  }
}