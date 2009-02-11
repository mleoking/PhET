package edu.colorado.phet.movingman.ladybug.canvas

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{Line2D, GeneralPath, Point2D}
import java.awt.{BasicStroke, Color}
import model.{DataPoint, Observable, LadybugModel}
import java.lang.Math._

import umd.cs.piccolo.PNode

class LadybugFadeTraceNode(model: LadybugModel, transform: ModelViewTransform2D, shouldBeVisible: () => Boolean, observable: Observable, maxFade: Double) extends LadybugTraceNode(model, transform, shouldBeVisible, observable) {
  update()

  def update() = {
    removeAllChildren
    implicit def historyToPoint(dataPoint: DataPoint) = new Point2D.Float(dataPoint.state.position.x.toFloat, dataPoint.state.position.y.toFloat)

    val historyToShow = getHistoryToShow()
    if (historyToShow.length >= 2) {
      val t = transform.modelToView(historyToShow(0))
      for (i <- 0 to (historyToShow.length - 2)) {
        val a = transform.modelToView(historyToShow(i))
        val b = transform.modelToView(historyToShow(i + 1))
        val dt = abs(model.getTime - historyToShow(i).time)

        val color = toColor(dt, maxFade)
        if (color.getTransparency != 0)
          addChild(new PhetPPath(new Line2D.Double(a, b), new BasicStroke(6, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f), color))
      }
    }
  }

}
