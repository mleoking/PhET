// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcelawlab.view

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform
import edu.umd.cs.piccolo.PNode
import edu.umd.cs.piccolo.event.{PInputEvent, PBasicInputEventHandler}
import edu.umd.cs.piccolo.util.PDimension
import java.awt.geom.Point2D
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.forcelawlab.model.Mass

//Drag handler used for translating the masses with the mouse
class DragHandler(mass: Mass,
                  transform: ModelViewTransform,
                  minDragX: () => Double, maxDragX: () => Double, node: PNode) extends PBasicInputEventHandler {
  var dragging = false

  override def mouseDragged(event: PInputEvent) {
    implicit def toPoint2D(dim: PDimension) = new Point2D.Double(dim.width, dim.height)
    val delta = event.getDeltaRelativeTo(node.getParent)
    val x: Double = transform.viewToModelDelta(new Vector2D(delta.width, delta.height).toImmutableVector2D).getX
    mass.position = mass.position + new Vector2D(x, 0)
    if ( mass.position.x < minDragX() ) {
      mass.position = new Vector2D(minDragX(), 0)
    }
    if ( mass.position.x > maxDragX() ) {
      mass.position = new Vector2D(maxDragX(), 0)
    }
  }

  override def mousePressed(event: PInputEvent) { dragging = true }

  override def mouseReleased(event: PInputEvent) { dragging = false }
}