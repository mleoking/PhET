package edu.colorado.phet.ladybugmotion2d.canvas

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{GeneralPath, Point2D}
import java.awt.{BasicStroke, Color}

import model.LadybugModel
import scalacommon.util.Observable
import umd.cs.piccolo.PNode

abstract class LadybugTraceNode(model: LadybugModel, transform: ModelViewTransform2D, 
                                shouldBeVisible: () => Boolean, observable: Observable) extends PNode {
  var clearPt = 0
  setPickable(false)
  setChildrenPickable(false)
  observable.addListener(() => {
    setVisible(shouldBeVisible())
    doUpdate()
  })
  setVisible(shouldBeVisible())
  model.addListener(doUpdate)

  def doUpdate() = {
    if (shouldBeVisible()) {
      update
    }
  }

  def update()

  def clamp(a: Double, value: Double, c: Double) = (a max value) min c

  def toColor(dt: Double, maxFade: Double) = {
    val c = clamp(0, dt / 3.0, maxFade).toFloat
    val color = new Color(c, c, 1.toFloat, 1 - c)
    color
  }

  def clearTrace = {
    clearPt = model.getRecordingHistory.length
    update()
  }

  def getHistoryToShow() = model.getRecordingHistory
}