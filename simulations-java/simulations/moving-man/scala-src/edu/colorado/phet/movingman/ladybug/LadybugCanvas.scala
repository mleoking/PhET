package edu.colorado.phet.movingman.ladybug

import _root_.edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.geom.Rectangle2D
import java.awt.Rectangle

class LadybugCanvas extends PhetPCanvas {
  val transform: ModelViewTransform2D = new ModelViewTransform2D(new Rectangle2D.Double(-10, -10, 20, 20), new Rectangle(0, 0, 100, 100),false)
}