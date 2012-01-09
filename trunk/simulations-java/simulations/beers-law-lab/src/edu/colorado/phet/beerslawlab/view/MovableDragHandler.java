// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.BLLSimSharing.Parameters;
import edu.colorado.phet.beerslawlab.model.Movable;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragSequenceEventHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * A drag handler for something that is movable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MovableDragHandler extends SimSharingDragSequenceEventHandler {

    private final Movable movable;
    private final PNode dragNode;
    private double clickXOffset, clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

    public MovableDragHandler( final UserComponent userComponent, final Movable movable, PNode dragNode ) {
        this( userComponent, movable, dragNode,
              new Function0<Parameter[]>() {
                  public Parameter[] apply() {
                      return new Parameter[0];
                  }
              } );
    }

    public MovableDragHandler( final UserComponent userComponent, final Movable movable, PNode dragNode, final Function0<Parameter[]> simSharingParametersFunction ) {
        this.movable = movable;
        this.dragNode = dragNode;
        // sim-sharing
        setStartEndDragFunction( new DragFunction() {
            public void apply( UserAction action, Parameter xParameter, Parameter yParameter, PInputEvent event ) {
                SimSharingManager.sendUserMessage( userComponent, action,
                                                   new ParameterSet( simSharingParametersFunction.apply() ).param( Parameters.locationX, (int) movable.location.get().getX() ).
                                                           param( Parameters.locationY, (int) movable.location.get().getY() ) );
            }
        } );
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
        //TODO assumes a 1:1 model-view transform
        movable.location.set( constrainToBounds( x, y, movable.getDragBounds() ) );
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
