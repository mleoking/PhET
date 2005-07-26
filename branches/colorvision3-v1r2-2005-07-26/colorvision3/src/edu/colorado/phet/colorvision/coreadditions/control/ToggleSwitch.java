/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.colorvision.coreadditions.control;

import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.colorvision.coreadditions.view.BoundsOutliner;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

/**
 * ToggleSwitch is an on/off switch.
 * The on/off states are represented by two images.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Revision$
 */
public class ToggleSwitch extends DefaultInteractiveGraphic {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Image for the "on" state.
    private PhetImageGraphic _onImage;
    // Image for the "off" state.
    private PhetImageGraphic _offImage;
    // The state.
    private boolean _on;
    // Location, origin at upper left.
    private Point _location;
    // The parent component, needed for repaint.
    private Component _component;
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

        super( null );

        _component = component;
        _onImage = new PhetImageGraphic( component, onImageName );
        _offImage = new PhetImageGraphic( component, offImageName );
        _listenerList = new EventListenerList();

        // Set up interactivity.
        super.setBoundedGraphic( _onImage );
        super.addCursorHandBehavior();
        super.addMouseInputListener( new ToggleSwitchMouseInputListener() );

        setLocation( 0, 0 );
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
        if( onFlag != _on ) {
            _on = onFlag;
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

    /**
     * Sets the location.
     * 
     * @param location the location
     */
    public void setLocation( Point location ) {
        _location = location;
        _onImage.setPosition( location.x, location.y );
        _offImage.setPosition( location.x, location.y );
        repaint();
    }

    /**
     * Convenience function for setting the location.
     * 
     * @param x the X coordinate
     * @param y the Y coordinate
     */
    public void setLocation( int x, int y ) {
        setLocation( new Point( x, y ) );
    }

    /**
     * Gets the location.
     * 
     * @return the location
     */
    public Point getLocation() {
        return _location;
    }

    /**
     * Gets the bounds.
     * If the switch is in the "on" state, the bounds of the "on" image are used.
     * If the switch is in the "off" state, the bounds of the "off" image are used.
     * 
     * @return the bounds
     */
    protected Rectangle getBounds() {

        Rectangle bounds = null;
        if( isOn() ) {
            bounds = new Rectangle( _onImage.getBounds() );
        }
        else {
            bounds = new Rectangle( _offImage.getBounds() );
        }
        return bounds;
    }

    //----------------------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------------------

    /**
     * Repaints the switch.
     */
    public void repaint() {
        Rectangle r = getBounds();
        _component.repaint( r.x, r.y, r.width, r.height );
    }

    /**
     * Draw the switch.
     * 
     * @param g2 the graphics context
     */
    public void paint( Graphics2D g2 ) {

        if( super.isVisible() ) {
            if( isOn() ) {
                _onImage.paint( g2 );
            }
            else {
                _offImage.paint( g2 );
            }
            BoundsOutliner.paint( g2, getBounds(), Color.RED, new BasicStroke( 1f ) ); // DEBUG
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