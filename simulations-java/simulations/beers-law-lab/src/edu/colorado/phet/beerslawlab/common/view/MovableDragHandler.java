// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.common.view;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.common.BLLSimSharing.ParameterKeys;
import edu.colorado.phet.beerslawlab.common.model.Movable;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * A drag handler for something that is movable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MovableDragHandler extends SimSharingDragHandler {

    private final Movable movable;
    private final PNode dragNode;
    private final ModelViewTransform mvt;
    private double clickXOffset, clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

    // Use this constructor when there's a 1:1 mapping between model and view coordinate frames.
    public MovableDragHandler( final IUserComponent userComponent, final Movable movable, PNode dragNode ) {
        this( userComponent, movable, dragNode, ModelViewTransform.createIdentity() );
    }

    public MovableDragHandler( final IUserComponent userComponent, final Movable movable, PNode dragNode, ModelViewTransform mvt ) {
        super( userComponent, UserComponentTypes.sprite );
        this.movable = movable;
        this.dragNode = dragNode;
        this.mvt = mvt;
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        clickXOffset = pMouse.getX() - mvt.modelToViewDeltaX( movable.location.get().getX() );
        clickYOffset = pMouse.getY() - mvt.modelToViewDeltaY( movable.location.get().getY() );
    }

    @Override protected void drag( final PInputEvent event ) {
        super.drag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        double xModel = mvt.viewToModelDeltaX( pMouse.getX() - clickXOffset );
        double yModel = mvt.viewToModelDeltaY( pMouse.getY() - clickYOffset );
        movable.location.set( constrainToBounds( xModel, yModel, movable.getDragBounds() ) );
    }

    @Override public ParameterSet getParametersForAllEvents( PInputEvent event ) {
        return super.getParametersForAllEvents( event ).with( ParameterKeys.locationX, (int) movable.location.get().getX() ).with( ParameterKeys.locationY, (int) movable.location.get().getY() );
    }

    // Constrains xy coordinates to be within the specified bounds.
    private static ImmutableVector2D constrainToBounds( double x, double y, Rectangle2D bounds ) {
        ImmutableVector2D vConstrained;
        if ( bounds != null && !bounds.contains( x, y ) ) {
            double xConstrained = Math.max( Math.min( x, bounds.getMaxX() ), bounds.getX() );
            double yConstrained = Math.max( Math.min( y, bounds.getMaxY() ), bounds.getY() );
            vConstrained = new ImmutableVector2D( xConstrained, yConstrained );
        }
        else {
            vConstrained = new ImmutableVector2D( x, y );
        }
        return vConstrained;
    }
}
