// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.simsharing;

import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingActions;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents.sendEvent;

/**
 * Base class for drag sequence handlers that perform sim-sharing data collection.
 * Overrides should take care to called super first, so that events are sent first.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingDragSequenceEventHandler extends PDragSequenceEventHandler {

    private final String object;
    private final Function0<Parameter[]> parameters;

    public SimSharingDragSequenceEventHandler( String object ) {
        this( object, new Function0<Parameter[]>() {
            public Parameter[] apply() {
                return new Parameter[] { }; // empty array
            }
        } );
    }

    public SimSharingDragSequenceEventHandler( String object, Function0<Parameter[]> parameters ) {
        this.object = object;
        this.parameters = parameters;
    }

    @Override protected void startDrag( final PInputEvent event ) {
        sendEvent( object, SimSharingActions.START_DRAG, addCanvasPosition( parameters.apply(), event ) );
        super.startDrag( event );
    }

    @Override protected void endDrag( PInputEvent event ) {
        sendEvent( object, SimSharingActions.END_DRAG, addCanvasPosition( parameters.apply(), event ) );
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
