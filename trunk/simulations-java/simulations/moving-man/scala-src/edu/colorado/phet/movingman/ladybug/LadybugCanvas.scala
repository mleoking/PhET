package edu.colorado.phet.movingman.ladybug

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.event.{ComponentAdapter, ComponentEvent}
import java.awt.geom.Rectangle2D
import java.awt.Rectangle

class LadybugCanvas extends PhetPCanvas {
  val transform: ModelViewTransform2D = new ModelViewTransform2D(new Rectangle2D.Double(-10, -10, 20, 20), new Rectangle(0, 0, 1024, 768), false)
  addComponentListener(new ComponentAdapter() {
    override def componentResized(e: ComponentEvent) = updateViewBounds

    override def componentShown(e: ComponentEvent) = updateViewBounds
  })
  updateViewBounds
  def updateViewBounds = if (getWidth * getHeight > 0) transform.setViewBounds(new Rectangle(0, 0, getWidth, getHeight))
}