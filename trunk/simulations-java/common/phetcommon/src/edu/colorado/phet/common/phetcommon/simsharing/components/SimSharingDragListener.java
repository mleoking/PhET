// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingActions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Base class for drag listeners that perform sim-sharing data collection.
 * Overrides should take care to called super first, so that events are sent first.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingDragListener extends MouseAdapter {

    private final String object;
    private final Function0<Parameter[]> parameters;

    public SimSharingDragListener( String object, Function0<Parameter[]> parameters ) {
        this.object = object;
        this.parameters = parameters;
    }

    @Override public void mousePressed( MouseEvent event ) {
        SimSharingEvents.sendEvent( object, SimSharingActions.START_DRAG, addCanvasPosition( parameters.apply(), event ) );
        super.mousePressed( event );
    }

    @Override public void mouseReleased( MouseEvent event ) {
        SimSharingEvents.sendEvent( object, SimSharingActions.END_DRAG, addCanvasPosition( parameters.apply(), event ) );
        super.mouseReleased( event );
    }

    //Adds the mouse position (relative to the source component) to an array of message parameters
    private Parameter[] addCanvasPosition( Parameter[] parameters, final MouseEvent event ) {
        return new ArrayList<Parameter>( Arrays.asList( parameters ) ) {{
            add( new Parameter( "x", event.getX() ) );
            add( new Parameter( "y", event.getY() ) );
        }}.toArray( new Parameter[0] );
    }
}
