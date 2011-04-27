// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.drag;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.WorldLocationProperty;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler that modify a WorldLocationProperty,
 * typically used to model an object's 3D location.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WorldLocationDragHandler extends PDragSequenceEventHandler {

    private final WorldLocationProperty locationProperty;
    private final PNode dragNode;
    private final CLModelViewTransform3D mvt;

    private double clickXOffset, clickYOffset;

    /**
     * Constructor.
     *
     * @param locationProperty the location we're controlling
     * @param dragNode         the node being dragged
     * @param mvt              transform between model and view coordinate frames
     */
    public WorldLocationDragHandler( WorldLocationProperty locationProperty, PNode dragNode, CLModelViewTransform3D mvt ) {
        this.locationProperty = locationProperty;
        this.dragNode = dragNode;
        this.mvt = mvt;
    }

    // When we start the drag, compute offset from dragNode's origin.
    @Override
    protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        Point2D pOrigin = mvt.modelToViewDelta( locationProperty.getValue() );
        clickXOffset = pMouse.getX() - pOrigin.getX();
        clickYOffset = pMouse.getY() - pOrigin.getY();
    }

    // As we drag, compute the new location and update the model.
    @Override
    protected void drag( final PInputEvent event ) {
        super.drag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        double xView = pMouse.getX() - clickXOffset;
        double yView = pMouse.getY() - clickYOffset;
        locationProperty.setValue( mvt.viewToModel( xView, yView ) );
    }
}
