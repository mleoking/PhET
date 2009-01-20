package edu.colorado.phet.movingman.ladybug.canvas

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{GeneralPath, Point2D}
import java.awt.{BasicStroke, Color}
import model.{ObservableS, LadybugModel}

import umd.cs.piccolo.PNode
import java.awt.geom.Path2D

abstract class LadybugTraceNode(model: LadybugModel, transform: ModelViewTransform2D, shouldBeVisible: () => Boolean, observable: ObservableS) extends PNode {
  var clearPt = 0
  setPickable(false)
  setChildrenPickable(false)
  observable.addListener(() => setVisible(shouldBeVisible()))
  setVisible(shouldBeVisible())
  model.addListener(update)

  def update(model: LadybugModel)

  def clearTrace = {
    clearPt = model.getHistory.length
    update(model)
  }

  def getHistoryToShow() = model.getHistory
}