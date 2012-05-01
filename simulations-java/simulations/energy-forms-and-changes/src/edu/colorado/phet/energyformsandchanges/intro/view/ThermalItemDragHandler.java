// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.RelativeDragHandler;
import edu.colorado.phet.energyformsandchanges.intro.model.UserMovableModelElement;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for model elements that can be moved by the user and that can
 * be heated and cooled.
 *
 * @author John Blanco
 */
public class ThermalItemDragHandler extends RelativeDragHandler {

    private final UserMovableModelElement modelElement;

    /**
     * Constructor.
     *
     * @param modelElement
     * @param node
     * @param transform
     */
    public ThermalItemDragHandler( UserMovableModelElement modelElement, PNode node, ModelViewTransform transform, Function1<Point2D, Point2D> constraint ) {
        super( node, transform, modelElement.position, constraint );
        this.modelElement = modelElement;
    }

    @Override public void mousePressed( PInputEvent event ) {
        super.mousePressed( event );
        // The user is moving this, so they have control.
        modelElement.userControlled.set( true );
    }

    @Override public void mouseReleased( PInputEvent event ) {
        super.mouseReleased( event );
        // The user has released the element and no longer controls is.
        modelElement.userControlled.set( false );
    }
}
