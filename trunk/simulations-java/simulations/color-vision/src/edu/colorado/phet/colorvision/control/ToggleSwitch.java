// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.colorvision.control;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.colorvision.view.BoundsOutliner;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;

/**
 * ToggleSwitch is an on/off switch.
 * The on/off states are represented by two images.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ToggleSwitch extends PhetImageGraphic {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Image resource name for the "on" state.
    private String _onImageName;
    // Image resource name for the "off" state.
    private String _offImageName;
    // The state.
    private boolean _on;
    // List of event listeners.
    private EventListenerList _listenerList;

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param onImageName the resource name of the "on" image
     * @param offImageName the resource name of the "off" image
     */
    public ToggleSwitch( Component component, String onImageName, String offImageName ) {

        super( component, offImageName );
        
        _onImageName = onImageName;
        _offImageName = offImageName;
        
        _listenerList = new EventListenerList();

        // Set up interactivity.
        super.setCursorHand();
        super.addMouseInputListener( new ToggleSwitchMouseInputListener() );
        
        _on = true; // force an update
        setOn( false );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the state.
     * 
     * @param onFlag true to turn the switch on, false to turn off
     */
    public void setOn( boolean onFlag ) {
        if ( onFlag != _on ) {
            _on = onFlag;
            setImageResourceName( onFlag ? _onImageName : _offImageName );
            fireChangeEvent( new ChangeEvent( this ) );
            repaint();
        }
    }

    /**
     * Gets the state.
     * 
     * @return true if the switch is on, false it it's off
     */
    public boolean isOn() {
        return _on;
    }

    public void paint( Graphics2D g2 ) {
        if( isVisible() ) {
            super.paint( g2 );
            BoundsOutliner.paint( g2, this ); // DEBUG
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /*
     * ToggleSwitchMouseInputListener is an inner class that handles MouseEvents.
     * <p>
     * Typically, I would prefer to have ToggleSwitch act as the listener.
     * But since DefaultInteractiveGraphics already uses the method names 
     * of the MouseInputListener interface for other purposes, resorting to
     * an inner class was the only way to accomplish this.
     */
    private class ToggleSwitchMouseInputListener extends MouseInputAdapter {

        /* Constructor */
        public ToggleSwitchMouseInputListener() {}

        /*
         * Handle mouse clicks by toggling the state.
         * 
         * @param event the mouse event
         */
        public void mouseClicked( MouseEvent event ) {
            setOn( !_on );
        }
    }

    /**
     * Adds a ChangeListener.
     * 
     * @param listener the listener
     */
    public void addChangeListener( ChangeListener listener ) {
        _listenerList.add( ChangeListener.class, listener );
    }

    /**
     * Removes a ChangeListener.
     * 
     * @param listener the listener
     */
    public void removeChangeListener( ChangeListener listener ) {
        _listenerList.remove( ChangeListener.class, listener );
    }

    /**
     * Fires a ChangeEvent.
     * 
     * @param event the event
     */
    private void fireChangeEvent( ChangeEvent event ) {

        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ChangeListener.class ) {
                ( (ChangeListener) listeners[i + 1] ).stateChanged( event );
            }
        }
    }
}