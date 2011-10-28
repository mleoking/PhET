// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.simsharing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents.actionPerformed;

/**
 * @author Sam Reid
 */
public class PiccoloPhetSimSharingEvents {
    //Fire an action when the mouse is released on the specified PNode
    public static void registerMouseReleasedListener( PNode node, final String action ) {
        node.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( PInputEvent event ) {
                actionPerformed( action );
            }
        } );
    }

    //Write the specified message when the button is pressed
    public static void addActionListener( ButtonNode textButtonNode, final String message ) {
        textButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SimSharingEvents.actionPerformed( message );
            }
        } );
    }

    public static void addDragSequenceListener( PNode node, final Function0<String> message ) {
        node.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseDragged( PInputEvent event ) {
                actionPerformed( "Mouse dragged: " + message.apply() );
            }

            @Override public void mouseReleased( PInputEvent event ) {
                actionPerformed( "Mouse released: " + message.apply() );
            }
        } );
    }
}
