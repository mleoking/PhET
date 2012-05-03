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
 * Drag handler for objects that can be moved by the user.  This is constructed
 * with a constraint function that defines where the model object can go.
 */
class ThermalElementDragHandler extends RelativeDragHandler {

    private UserMovableModelElement modelElement;

    /**
     * Constructor.  The node must be property positioned before calling
     * this, or it won't work correctly.
     *
     * @param modelElement
     * @param node
     * @param mvt
     */
    public ThermalElementDragHandler( UserMovableModelElement modelElement, PNode node, ModelViewTransform mvt, Function1<Point2D, Point2D> constraint ) {
        super( node, mvt, modelElement.position, constraint );
        this.modelElement = modelElement;
    }

    @Override public void mousePressed( PInputEvent event ) {
        super.mousePressed( event );
        modelElement.userControlled.set( true );
    }

    @Override public void mouseReleased( PInputEvent event ) {
        super.mouseReleased( event );
        modelElement.userControlled.set( false );
    }

    protected void setControlledModelElement( UserMovableModelElement modelElement ) {
        this.modelElement = modelElement;
    }
}
