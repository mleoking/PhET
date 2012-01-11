// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;

/**
 * Base class for drag listeners that perform sim-sharing data collection.
 * <p/>
 * If a client is not interested in sim-sharing, use the zero-arg constructor;
 * otherwise use one of the other constructors or setters.
 * <p/>
 * Client overrides should take care to called super first, so that events are sent first.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingDragListener extends MouseAdapter {

    protected final IUserComponent userComponent;
    private final SimSharingDragPoints dragPoints; // mouse coordinates, accumulated during a drag sequence

    public SimSharingDragListener( IUserComponent userComponent ) {
        this.userComponent = userComponent;
        this.dragPoints = new SimSharingDragPoints();
    }

    // Start the drag and send a sim-sharing message.
    @Override public void mousePressed( MouseEvent event ) {
        clearDragPoints();
        addDragPoint( event );
        SimSharingManager.sendUserMessage( userComponent, UserActions.startDrag, getStartDragParameters( event ).add( getXParameter( event ) ).add( getYParameter( event ) ) );
        super.mousePressed( event );
    }

    // Finish the drag and send a sim-sharing message.
    @Override public void mouseReleased( MouseEvent event ) {
        addDragPoint( event );
        SimSharingManager.sendUserMessage( userComponent, UserActions.endDrag, getEndDragParameters( event ).
                add( getXParameter( event ) ).
                add( getYParameter( event ) ).
                addAll( dragPoints.getParameters() ) );
        clearDragPoints();
        super.mouseReleased( event );
    }

    // Override this is you want to send messages during drags. Be sure to call super.mouseDragged
    @Override public void mouseDragged( MouseEvent event ) {
        System.out.println( "SimSharingDragListener.mouseDragged" );
        addDragPoint( event );
        super.mouseDragged( event );
    }

    private void addDragPoint( MouseEvent event ) {
        dragPoints.add( getPosition( event ) );
    }

    private void clearDragPoints() {
        dragPoints.clear();
    }

    // Override to supply any additional parameters to send on start drag message
    public ParameterSet getStartDragParameters( MouseEvent event ) {
        return getParametersForAllEvents( event );
    }

    // Override to supply any additional parameters to send on end drag message
    public ParameterSet getEndDragParameters( MouseEvent event ) {
        return getParametersForAllEvents( event );
    }

    // Return parameters that are used by default for startDrag and endDrag
    public ParameterSet getParametersForAllEvents( MouseEvent event ) {
        return new ParameterSet();
    }

    private static Parameter getXParameter( MouseEvent event ) {
        return new Parameter( ParameterKeys.x, getPosition( event ).getX() );
    }

    private static Parameter getYParameter( MouseEvent event ) {
        return new Parameter( ParameterKeys.y, getPosition( event ).getY() );
    }

    // Gets the interpretation of the position used throughout this class.
    private static Point2D getPosition( MouseEvent event ) {
        return event.getPoint();
    }
}
