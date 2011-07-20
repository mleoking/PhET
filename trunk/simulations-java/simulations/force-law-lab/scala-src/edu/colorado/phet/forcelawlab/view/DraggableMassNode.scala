// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forcelawlab.view

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform
import java.awt.Color
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.colorado.phet.forcelawlab.model.Mass

//Mass node that can be dragged by the mouse
class DraggableMassNode(mass: Mass, transform: ModelViewTransform,
                        color: Color, minDragX: () => Double, maxDragX: () => Double,
                        magnification: Magnification, textOffset: () => Double)
        extends MassNode(mass, transform, color, magnification, textOffset) {
  addInputEventListener(new DragHandler(mass, transform, minDragX, maxDragX, this))
  addInputEventListener(new CursorHandler)
}