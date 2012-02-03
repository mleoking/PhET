// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;

/**
 * Base class for Swing drag listeners that perform sim-sharing data collection.
 * Sends messages on startDrag, endDrag, and (optionally) on drag.
 * <p/>
 * Can be customized in 2 ways:
 * 1. Override getParametersForAllEvents to augment or replace the standard parameters for all messages.
 * 2. Override the get*Parameters methods to augment or replace the standard parameters for specific messages.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingDragListener extends MouseAdapter {

    protected final IUserComponent userComponent;
    private final IUserComponentType componentType;
    private final boolean sendDragMessages;
    private final SimSharingDragPoints dragPoints; // mouse coordinates, accumulated during a drag sequence

    // Sends a message on startDrag and endDrag, but not drag
    public SimSharingDragListener( IUserComponent userComponent, IUserComponentType componentType ) {
        this( userComponent, componentType, false );
    }

    // Sends a message on drag if reportDrag=true
    public SimSharingDragListener( IUserComponent userComponent, IUserComponentType componentType, final boolean sendDragMessages ) {
        this.userComponent = userComponent;
        this.componentType = componentType;
        this.sendDragMessages = sendDragMessages;
        this.dragPoints = new SimSharingDragPoints();
    }

    @Override public void mousePressed( MouseEvent event ) {
        clearDragPoints();
        addDragPoint( event );
        SimSharingManager.sendUserMessage( userComponent, componentType, UserActions.startDrag, getStartDragParameters( event ) );
        super.mousePressed( event );
    }

    @Override public void mouseDragged( MouseEvent event ) {
        addDragPoint( event );
        if ( sendDragMessages ) {
            SimSharingManager.sendUserMessage( userComponent, componentType, UserActions.startDrag, getDragParameters( event ) );
        }
        super.mouseDragged( event );
    }

    @Override public void mouseReleased( MouseEvent event ) {
        addDragPoint( event );
        SimSharingManager.sendUserMessage( userComponent, componentType, UserActions.startDrag, getEndDragParameters( event ) );
        clearDragPoints();
        super.mouseReleased( event );
    }

    // Override to specify startDrag parameters, chain with super to add parameters.
    protected ParameterSet getStartDragParameters( MouseEvent event ) {
        return getParametersForAllEvents( event );
    }

    // Override to specify drag parameters, chain with super to add parameters.
    protected ParameterSet getDragParameters( MouseEvent event ) {
        return getParametersForAllEvents( event );
    }

    // Override to specify endDrag parameters, chain with super to add parameters.
    protected ParameterSet getEndDragParameters( MouseEvent event ) {
        return getParametersForAllEvents( event ).add( dragPoints.getParameters() ); // includes summary of drag points
    }

    // Return parameters that are used by default for all events, chain with super to add parameters.
    protected ParameterSet getParametersForAllEvents( MouseEvent event ) {
        return new ParameterSet().add( getXParameter( event ) ).add( getYParameter( event ) );
    }

    private void addDragPoint( MouseEvent event ) {
        dragPoints.add( getPosition( event ) );
    }

    private void clearDragPoints() {
        dragPoints.clear();
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
