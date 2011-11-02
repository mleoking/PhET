// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.simsharing;

import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents.actionPerformed;

/**
 * @author Sam Reid
 */
public class PiccoloPhetSimSharingEvents {

    public static void addDragSequenceListener( PNode node, final Function0<Parameter[]> message ) {
        node.addInputEventListener( new PBasicInputEventHandler() {

            @Override public void mousePressed( final PInputEvent event ) {
                actionPerformed( "mouse", "startDrag", addCanvasPosition( message, event ) );
            }

            @Override public void mouseReleased( PInputEvent event ) {
                actionPerformed( "mouse", "endDrag", addCanvasPosition( message, event ) );
            }

            //Adds the canvas position to an array of message parameters
            private Parameter[] addCanvasPosition( Function0<Parameter[]> message, final PInputEvent event ) {
                return new ArrayList<Parameter>( Arrays.asList( message.apply() ) ) {{
                    add( new Parameter( "canvasPositionX", event.getCanvasPosition().getX() ) );
                    add( new Parameter( "canvasPositionY", event.getCanvasPosition().getY() ) );
                }}.toArray( new Parameter[0] );
            }
        } );
    }
}
