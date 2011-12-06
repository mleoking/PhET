// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.simsharing;

import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingActions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEventArgs;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents.sendEvent;

/**
 * Base class for drag sequence handlers that perform sim-sharing data collection.
 * <p/>
 * If a client is not interested in sim-sharing, use the zero-arg constructor;
 * otherwise use one of the other constructors, or setSimSharingEventArgs, to
 * provide information that will be sent with sim-sharing events.
 * <p/>
 * Overrides should take care to called super first, so that events are sent first.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingDragSequenceEventHandler extends PDragSequenceEventHandler {

    private SimSharingEventArgs eventArgs;

    public SimSharingDragSequenceEventHandler() {
    }

    public SimSharingDragSequenceEventHandler( String object ) {
        this( new SimSharingEventArgs( object ) );
    }

    public SimSharingDragSequenceEventHandler( String object, Function0<Parameter[]> parameters ) {
        this( new SimSharingEventArgs( object, parameters ) );
    }

    public SimSharingDragSequenceEventHandler( SimSharingEventArgs eventArgs ) {
        this.eventArgs = eventArgs;
    }

    public void setSimSharingEventArgs( SimSharingEventArgs eventArgs ) {
        this.eventArgs = eventArgs;
    }

    @Override protected void startDrag( final PInputEvent event ) {
        if ( eventArgs != null ) {
            sendEvent( eventArgs.object, SimSharingActions.START_DRAG, addCanvasPosition( eventArgs.parameters.apply(), event ) );
        }
        super.startDrag( event );
    }

    @Override protected void endDrag( PInputEvent event ) {
        if ( eventArgs != null ) {
            sendEvent( eventArgs.object, SimSharingActions.END_DRAG, addCanvasPosition( eventArgs.parameters.apply(), event ) );
        }
        super.endDrag( event );
    }

    //Adds the canvas position to an array of message parameters
    private Parameter[] addCanvasPosition( Parameter[] parameters, final PInputEvent event ) {
        return new ArrayList<Parameter>( Arrays.asList( parameters ) ) {{
            add( new Parameter( "canvasPositionX", event.getCanvasPosition().getX() ) );
            add( new Parameter( "canvasPositionY", event.getCanvasPosition().getY() ) );
        }}.toArray( new Parameter[0] );
    }
}
