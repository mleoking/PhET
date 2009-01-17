package edu.colorado.phet.movingman.ladybug

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{GeneralPath, Point2D}
import java.awt.{BasicStroke, Color}
import umd.cs.piccolo.PNode
import java.awt.geom.Path2D

abstract class LadybugTraceNode(model: LadybugModel, transform: ModelViewTransform2D, shouldBeVisible: () => Boolean, observable: ObservableS) extends PNode {
  setPickable(false)
  setChildrenPickable(false)
  observable.addListener(() => setVisible(shouldBeVisible()))
  setVisible(shouldBeVisible())
  model.addListener(update)

  def update(model: LadybugModel)

  var clearPt = 0

  def clearTrace = {
    clearPt = model.history.length
  }
}