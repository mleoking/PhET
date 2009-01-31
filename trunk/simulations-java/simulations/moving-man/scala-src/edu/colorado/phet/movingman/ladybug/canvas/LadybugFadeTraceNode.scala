package edu.colorado.phet.movingman.ladybug.canvas

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{Line2D, Path2D, GeneralPath, Point2D}
import java.awt.{BasicStroke, Color}
import model.{DataPoint, ObservableS, LadybugModel}

import umd.cs.piccolo.PNode

class LadybugFadeTraceNode(model: LadybugModel, transform: ModelViewTransform2D, shouldBeVisible: () => Boolean, observable: ObservableS) extends LadybugTraceNode(model, transform, shouldBeVisible, observable) {
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
        val time = historyToShow(i).time
        val dt = model.getHistory()(model.getHistory().length - 1).time - time
        def clamp(a: Double, value: Double, c: Double) = (a max value) min c

        def toColor(dt: Double) = {
          val c = clamp(0, dt / 3.0, 1).toFloat
          val color = new Color(c, c, 1.toFloat, 1 - c)
          color
        }

        val color = toColor(dt)
        if (color.getTransparency != 0)
          addChild(new PhetPPath(new Line2D.Double(a, b), new BasicStroke(6,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,1.0f), color))
      }
    }
  }

}
