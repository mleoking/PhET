// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.simsharing;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingDragPoints;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Base class for drag sequence handlers that perform sim-sharing data collection.
 * Sends messages on startDrag, endDrag, and (optionally) on drag.
 * <p/>
 * Can be customized in 3 ways:
 * 1. Override getParametersForAllEvents to augment or replace the standard parameters for all events.
 * 2. Override the get*Parameters methods to augment or replace the standard parameters for specific.
 * 3. Call set*Function methods to replace the functions invoked for specific events.
 *
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingDragHandler extends PDragSequenceEventHandler {

    // Function to extend standard ParameterSet
    public interface ParameterSetFunction {
        public ParameterSet apply( ParameterSet standardParameters, PInputEvent event );
    };

    protected final IUserComponent userComponent;
    protected final IUserComponentType componentType;
    private boolean sendDragMessages;
    private final SimSharingDragPoints dragPoints; // canvas coordinates, accumulated during a drag sequence
    private ParameterSetFunction startDragParameterSetFunction, dragParameterSetFunction, endDragParameterSetFunction; // functions to get parameters for various messages

    // Sends a message on startDrag and endDrag, but not drag
    public SimSharingDragHandler( IUserComponent userComponent, IUserComponentType componentType ) {
        this( userComponent, componentType, false );
    }

    // Sends a message on drag if reportDrag=true
    public SimSharingDragHandler( IUserComponent userComponent, IUserComponentType componentType, final boolean sendDragMessages ) {

        this.userComponent = userComponent;
        this.componentType = componentType;
        this.sendDragMessages = sendDragMessages;
        this.dragPoints = new SimSharingDragPoints();

        // default functions
        ParameterSetFunction defaultFunction = new ParameterSetFunction() {
            public ParameterSet apply( ParameterSet parameterSet, PInputEvent event ) {
                return parameterSet;
            }
        };
        this.startDragParameterSetFunction = defaultFunction;
        this.dragParameterSetFunction = defaultFunction;
        this.endDragParameterSetFunction = defaultFunction;
    }

    public boolean setSendDragMessages( boolean sendDragMessages ) {
        this.sendDragMessages = sendDragMessages;
    }

    @Override protected void startDrag( final PInputEvent event ) {
        clearDragPoints();
        addDragPoint( event );
        SimSharingManager.sendUserMessage( userComponent, componentType, UserActions.startDrag, startDragParameterSetFunction.apply( getStartDragParameters( event ), event ) );
        super.startDrag( event );
    }

    @Override protected void drag( PInputEvent event ) {
        addDragPoint( event );
        if ( sendDragMessages ) {
            SimSharingManager.sendUserMessage( userComponent, componentType, UserActions.drag, dragParameterSetFunction.apply( getDragParameters( event ), event ) );
        }
        super.drag( event );
    }

    @Override protected void endDrag( PInputEvent event ) {
        addDragPoint( event );
        SimSharingManager.sendUserMessage( userComponent, componentType, UserActions.endDrag, endDragParameterSetFunction.apply( getEndDragParameters( event ), event ) );
        clearDragPoints();
        super.endDrag( event );
    }

    // Call this to set the function that provides parameters for the startDrag message.
    public void setStartDragParameterSetFunction( ParameterSetFunction f ) {
        startDragParameterSetFunction = f;
    }

    // Call this to set the function that provides parameters for the drag message.
    public void setDragParameterSetFunction( ParameterSetFunction f ) {
        dragParameterSetFunction = f;
    }

    // Call this to set the function that provides parameters for the endDrag message.
    public void setEndDragParameterSetFunction( ParameterSetFunction f ) {
        endDragParameterSetFunction = f;
    }

    // Override to specify parameters that are provided to startDragFunction, chain with super to add parameters.
    protected ParameterSet getStartDragParameters( PInputEvent event ) {
        return getParametersForAllEvents( event );
    }

    // Override to specify parameters that are provided to dragFunction, chain with super to add parameters.
    protected ParameterSet getDragParameters( PInputEvent event ) {
        return getParametersForAllEvents( event );
    }

    // Override to specify parameters that are provided to endDragFunction, chain with super to add parameters.
    protected ParameterSet getEndDragParameters( PInputEvent event ) {
        return getParametersForAllEvents( event ).add( dragPoints.getParameters() ); // includes summary of drag points
    }

    // Override to specify parameters that are included in all messages.
    protected ParameterSet getParametersForAllEvents( PInputEvent event ) {
        return new ParameterSet().add( getXParameter( event ) ).add( getYParameter( event ) );
    }

    private void addDragPoint( PInputEvent event ) {
        dragPoints.add( getPosition( event ) );
    }

    private void clearDragPoints() {
        dragPoints.clear();
    }

    private static Parameter getXParameter( PInputEvent event ) {
        return new Parameter( ParameterKeys.canvasPositionX, getPosition( event ).getX() );
    }

    private static Parameter getYParameter( PInputEvent event ) {
        return new Parameter( ParameterKeys.canvasPositionY, getPosition( event ).getY() );
    }

    // Gets the interpretation of the position used throughout this class.
    private static Point2D getPosition( PInputEvent event ) {
        return event.getCanvasPosition();
    }
}