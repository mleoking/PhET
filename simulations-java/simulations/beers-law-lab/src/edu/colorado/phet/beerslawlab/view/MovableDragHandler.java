// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.beerslawlab.model.Movable;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragSequenceEventHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * A drag handler for anything that is movable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MovableDragHandler extends SimSharingDragSequenceEventHandler {

    private final Movable movable;
    private final PNode dragNode;
    private double clickXOffset, clickYOffset; // offset of mouse click from meter's origin, in parent's coordinate frame

    public MovableDragHandler( final String simSharingObject, final Movable movable, PNode dragNode ) {
        this.movable = movable;
        this.dragNode = dragNode;
        DragFunction function = new DragFunction() {
            public void apply( String action, Parameter xParameter, Parameter yParameter, PInputEvent event ) {
                SimSharingManager.sendEvent( simSharingObject, action,
                                             new Parameter( "locationX", (int) movable.location.get().getX() ),
                                             new Parameter( "locationY", (int) movable.location.get().getY() ) );
            }
        };
        setStartEndDragFunction( function );
        setDraggingFunction( function );
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        clickXOffset = pMouse.getX() - movable.location.get().getX();
        clickYOffset = pMouse.getY() - movable.location.get().getY();
    }

    @Override protected void drag( final PInputEvent event ) {
        super.drag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        double x = pMouse.getX() - clickXOffset;
        double y = pMouse.getY() - clickYOffset;
        //TODO constrain dragging
        movable.location.set( new ImmutableVector2D( x, y ) );
    }
}
