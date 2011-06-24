package edu.colorado.phet.ladybugmotion2d.canvas

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import java.awt.Color
import edu.colorado.phet.ladybugmotion2d.model.LadybugModel
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty
import java.lang.Boolean
import edu.colorado.phet.common.phetcommon.util.SimpleObserver

abstract class LadybugTraceNode(model: LadybugModel, transform: ModelViewTransform2D, visible: ObservableProperty[Boolean]) extends PNode {
  var clearPt = 0
  setPickable(false)
  setChildrenPickable(false)
  visible.addObserver(new SimpleObserver {
    def update() {
      setVisible(visible.get)
      doUpdate()
    }
  })
  model.addListener(doUpdate)

  def doUpdate() {
    if ( visible.get ) {
      update()
    }
  }

  def update()

  def clamp(a: Double, value: Double, c: Double) = ( a max value ) min c

  def toColor(dt: Double, maxFade: Double) = {
    val c = clamp(0, dt / 3.0, maxFade).toFloat
    val color = new Color(c, c, 1.toFloat, 1 - c)
    color
  }

  def clearTrace() {
    clearPt = model.getNumRecordedPoints
    update()
  }

  def getHistoryToShow = model.getRecordingHistory
}