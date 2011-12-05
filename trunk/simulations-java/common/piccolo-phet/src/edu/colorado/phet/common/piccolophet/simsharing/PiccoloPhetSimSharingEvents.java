// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.simsharing;

import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingActions;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents.sendEvent;

/**
 * Utility class for attaching listeners to PNodes.
 *
 * @author Sam Reid
 */
public class PiccoloPhetSimSharingEvents {

    //TODO SimSharingEvents.addDragSequenceListener should send the same type of message, unify these
    //Report when the mouse starts or stops a drag, and its canvas positions for start and stop
    public static void addDragSequenceListener( PNode node, final String object, final Function0<Parameter[]> parameters ) {
        node.addInputEventListener( new PBasicInputEventHandler() {

            @Override public void mousePressed( final PInputEvent event ) {
                sendEvent( object, SimSharingActions.START_DRAG, addCanvasPosition( parameters.apply(), event ) );
            }

            @Override public void mouseReleased( PInputEvent event ) {
                sendEvent( object, SimSharingActions.END_DRAG, addCanvasPosition( parameters.apply(), event ) );
            }

            //Adds the canvas position to an array of message parameters
            private Parameter[] addCanvasPosition( Parameter[] parameters, final PInputEvent event ) {
                return new ArrayList<Parameter>( Arrays.asList( parameters ) ) {{
                    add( new Parameter( "canvasPositionX", event.getCanvasPosition().getX() ) );
                    add( new Parameter( "canvasPositionY", event.getCanvasPosition().getY() ) );
                }}.toArray( new Parameter[0] );
            }
        } );
    }
}
