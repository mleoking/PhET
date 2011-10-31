// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.simsharing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
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

    //Write the specified message when the button is pressed
    public static void addActionListener( ButtonNode textButtonNode, final String object, final String action, final Parameter... parameters ) {
        textButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                SimSharingEvents.actionPerformed( object, action, parameters );
            }
        } );
    }

    public static void addDragSequenceListener( PNode node, final Function0<Parameter[]> message ) {
        node.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseDragged( PInputEvent event ) {
                actionPerformed( "mouse", "dragged", message.apply() );
            }

            @Override public void mouseReleased( PInputEvent event ) {
                actionPerformed( "mouse", "released", message.apply() );
            }
        } );
    }
}
