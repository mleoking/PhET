/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.event;

import java.util.ArrayList;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * ButtonEventHandler is a Piccolo event handler that maps mouse events to a more 
 * abstract representation that is typical of JButton behavior.
 * A JButton is either armed or unarmed, and (when the mouse is released 
 * over the button) it fires.
 * <p>
 * Listeners can easily determine when the button is "armed" and when it is "fired".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ButtonEventHandler extends PBasicInputEventHandler {

    private boolean mousePressed; // true if the mouse is pressed
    private boolean mouseInside; // true if the mouse is inside the node
    private ArrayList listeners; // list of ButtonEventListener

    public ButtonEventHandler() {
        super();
        mousePressed = false;
        mouseInside = false;
        listeners = new ArrayList();
    }

    public void mouseEntered( PInputEvent event ) {
        mouseInside = true;
        if ( mousePressed ) {
            notifyArmed( true );
        }
    }

    public void mouseExited( PInputEvent event ) {
        mouseInside = false;
        if ( mousePressed ) {
            notifyArmed( false );
        }
    }

    public void mousePressed( PInputEvent event ) {
        mousePressed = true;
        notifyArmed( true );
    }

    public void mouseReleased( PInputEvent event ) {
        mousePressed = false;
        notifyArmed( false );
        if ( mouseInside ) {
            notifyFire();
        }
    }
    
    public interface ButtonEventListener {
        /**
         * Indicates whether the node is armed.
         * The node is armed when the mouse is pressed 
         * and the cursor is inside the node's bounds.
         * @param armed
         */
        public void setArmed( boolean armed );
        /**
         * Indicates that the node has been fired.
         * The node is fired when the mouse is release
         * and the cursor is inside the node's bounds.
         */
        public void fire();
    }
    
    public static class ButtonEventAdapter {
        public void setArmed( boolean armed ) {}
        public void fire() {}
    }
    
    public void addButtonListener( ButtonEventListener listener ) {
        listeners.add( listener );
    }
    
    public void removeButtonListener( ButtonEventListener listener ) {
        listeners.remove( listener );
    }
    
    private void notifyArmed( boolean armed ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (ButtonEventListener) listeners.get( i ) ).setArmed( armed );
        }
    }
    
    private void notifyFire() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (ButtonEventListener) listeners.get( i ) ).fire();
        }
    }
}
