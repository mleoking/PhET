package edu.colorado.phet.movingman.ladybug

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.event.{ComponentAdapter, ComponentEvent}
import java.awt.geom.Rectangle2D
import java.awt.{Rectangle, Dimension}
import umd.cs.piccolo.PNode

class LadybugCanvas extends PhetPCanvas(new Dimension(1024, 768)) {
    val transform: ModelViewTransform2D = new ModelViewTransform2D(new Rectangle2D.Double(-10, -10, 20, 20), new Rectangle(0, 0, 1024, 768), false)
    val worldNode = new PNode
    addWorldChild(worldNode)
    def addNode(node: PNode) = worldNode.addChild(node)
}