/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

/**
 * GraphicSlider is a PhetGraphic UI component that is similar to a JSlider.
 * <p>
 * The default orientation is horizontal.
 * Unlike JSlider, there is no setOrientation method.
 * Use the rotate method to set an arbitrary orientation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GraphicSlider extends GraphicLayerSet {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double BACKGROUND_LAYER = 0;
    private static final double TRACK_LAYER = 1;
    private static final double KNOB_LAYER = 2;
    private static final double KNOB_HIGHLIGHT_LAYER = 3;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // The graphic used for the slider's components.
    private PhetGraphic _knob, _knobHighlight, _track, _background;
    // Bounary for dragging the knob, in relative coordinates.
    private Rectangle _dragBounds;
    // Minimum and maximum range, inclusive.
    private int _minimum, _maximum;
    // The current value.
    private int _value;
    // Location of the mouse relative to the knob when a drag is started.
    private int _mouseOffset;
    // Event listeners.
    private EventListenerList _listenerList;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Creates a slider with no user interface.
     * Use setKnob, setTrack and setBackground to build its user interface.
     * 
     * @param component the parent Component
     */
    public GraphicSlider( Component component ) {
        super( null );
        
        // Initialize instance data.
        _knob = _knobHighlight = _track = _background = null;
        _knobHighlight = null;
        _dragBounds = new Rectangle();
        _minimum = 0;
        _maximum = 100;
        _value = ( _maximum - _minimum ) / 2;
        _mouseOffset = 0;
        _listenerList = new EventListenerList();
        
        // Enable anti-aliasing.
        RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        setRenderingHints( hints );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the graphic used for the knob.
     * 
     * @param knob the knob graphic
     */
    public void setKnob( PhetGraphic knob ) {
        if ( _knob != null ) {
            removeGraphic( _knob );
            _knob.removeCursor();
            _knob.removeAllMouseInputListeners();
        }
        _knob = knob;
        if ( _knob != null ) {
            addGraphic( knob, KNOB_LAYER );
            _knob.setCursorHand();
            _knob.addMouseInputListener( new KnobListener() );
        }
        update();
    }
    
    /**
     * Gets the graphic used for the knob.
     * 
     * @return the knob graphic
     */
    public PhetGraphic getKnob() {
        return _knob;
    }
    
    /**
     * Sets the graphic used to highlight the knob.
     * 
     * @param knobHighlight the knob highlight graphic
     */
    public void setKnobHighlight( PhetGraphic knobHighlight ) {
        if ( _knobHighlight != null ) {
            removeGraphic( _knobHighlight );
        }
        _knobHighlight = knobHighlight;
        if ( _knobHighlight != null ) {
            addGraphic( knobHighlight, KNOB_HIGHLIGHT_LAYER );
            _knobHighlight.setVisible( false );
            _knobHighlight.setCursorHand();
            _knobHighlight.addMouseInputListener( new KnobListener() );
        }
        update();
    }
    
    /**
     * Gets the graphic used to highlight the knob.
     * 
     * @return the knob highlight graphic
     */
    public PhetGraphic getKnobHighlight() {
        return _knobHighlight;
    }
    
    /**
     * Sets the graphic used for the track.
     * 
     * @param track the track graphic
     */
    public void setTrack( PhetGraphic track ) {
        if ( _track != null ) {
            removeGraphic( track );
        }
        _track = track;
        _dragBounds.setBounds( 0, 0, 0, 0 );
        if ( _track != null ) {
            addGraphic( track, TRACK_LAYER );
            track.setRegistrationPoint( 0, 0 ); // upper left
            if ( _background == null ) {
                track.setLocation( 0, 0 );
            }
            else {
                int x = ( _background.getWidth() - track.getWidth() ) / 2;
                int y = ( _background.getHeight() - track.getHeight() ) / 2;
                track.setLocation( x, y );
            }
            _dragBounds.setBounds( track.getX(), track.getY() + ( track.getHeight() / 2 ), track.getWidth(), 1 );
        }
        setKnob( _knob );
    }
    
    /**
     * Gets the graphic used for the track.
     * 
     * @return the track graphic
     */
    public PhetGraphic getTrack() {
        return _track;
    }
    
    /**
     * Sets the graphic used for the background.
     * 
     * @param background the background graphic
     */
    public void setBackground( PhetGraphic background ) {
        if ( _background != null ) {
            removeGraphic( _background );
        }
        _background = background;
        if ( background != null ) {
            addGraphic( background, BACKGROUND_LAYER );
            background.setRegistrationPoint( 0, 0 ); // upper left
            background.setLocation( 0, 0 );
        }
        setTrack( _track );
    }
    
    /**
     * Gets the graphic used for the background.
     * 
     * @return the background graphic
     */
    public PhetGraphic getBackground() {
        return _background;
    }
    
    /**
     * Sets the slider value.
     *
     * @param value the value, silently clamped to the slider's range (ala JSlider)
     */
    public void setValue( int value ) {
        if ( value != _value ) {
            
            // Silently clamp the value to the allowed range.
            _value = (int) MathUtil.clamp( _minimum, value, _maximum );

            // Fire a ChangeEvent to notify listeners that the value has changed.
            fireChangeEvent( new ChangeEvent( this ) );

            // Update the knob.
            update();
        }
    }

    /**
     * Gets the slider value.
     * 
     * @return the value
     */
    public int getValue() {
        return _value;
    }

    /**
     * Sets the minimum value of the slider's range.
     * 
     * @param minimum the minimum
     */
    public void setMinimum( int minimum ) {
        _minimum = minimum;
        update();
    }

    /**
     * Gets the minimum value of the slider's range.
     * 
     * @return the minimum
     */
    public int getMinimum() {
        return _minimum;
    }

    /**
     * Sets the maximum value of the slider's range.
     * 
     * @param maximum the maximum
     */
    public void setMaximum( int maximum ) {
        _maximum = maximum;
        update();
    }

    /** 
     * Gets the maximum value of the slider's range.
     * 
     * @return the maximum
     */
    public int getMaximum() {
        return _maximum;
    }

    //----------------------------------------------------------------------------
    // UI update
    //----------------------------------------------------------------------------
    
    /*
     * Updates the user interface.
     */
    private void update() {
        // Set the knob's location based on the value.
        double percent = ( _value - _minimum ) / (double) ( _maximum - _minimum );
        int x = (int) ( _dragBounds.x + (int) ( percent * _dragBounds.width ) );
        int y = (int) _dragBounds.y;
        
        if ( _knob != null ) {
            _knob.setLocation( x, y );
        }
        
        if ( _knobHighlight != null ) {
            _knobHighlight.setLocation( x, y );
        }
        
        repaint();
    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /**
     * Adds a ChangeListener, ala JSlider.
     * 
     * @param listener the listener to add
     */
    public void addChangeListener( ChangeListener listener ) {
        _listenerList.add( ChangeListener.class, listener );
    }

    /**
     * Removes a ChangeListener, ala JSlider.
     * 
     * @param listener the listener to remove
     */
    public void removeChangeListener( ChangeListener listener ) {
        _listenerList.remove( ChangeListener.class, listener );
    }

    /**
     * Fires a ChangeEvent, ala JSlider.
     * This occurs each time the slider is moved.
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

    /**
     * KnobListener is an inner class the handles knob interactivity.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private class KnobListener extends MouseInputAdapter {

        /** Sole constructor. */
        public KnobListener() {
            super();
        }
        
        /**
         * Handles mouse drag events, related to moving the knob.
         * The knob's motion is constrained so that it behaves like a JSlider.
         * All calculations are performed relative to a slider in its default
         * (horizontal) orientation.
         * 
         * @param event the mouse event
         */
        public void mouseDragged( MouseEvent event ) {
            
            // Inverse transform the mouse coordinates to match a slider in its default (horizontal) orientation.
            int mouseX = 0;
            try {
                AffineTransform transform = getNetTransform();
                Point2D p = transform.inverseTransform( event.getPoint(), null );
                mouseX = (int) p.getX();
            }
            catch ( NoninvertibleTransformException e ) {
                e.printStackTrace();
            }
            
            // Calculate the proposed knob position.
            int knobX = mouseX - _mouseOffset;
            
            // Constrain the knob position to the drag boundaries.
            int x = (int) Math.max( _dragBounds.x, Math.min( _dragBounds.x + _dragBounds.width, knobX ) );

            // Determine the value that corresponds to the constrained location.
            double percent = ( x - _dragBounds.x ) / (double) ( _dragBounds.width );
            int value = (int) ( percent * ( _maximum - _minimum ) ) + _minimum;

            // Set the new value.
            setValue( value );
        }

        /**
         * Handles mouse press events.
         * Remembers how far the mouse was from the knob's 
         * registration point at when the drag began.
         * 
         * @param event the mouse event
         */
        public void mousePressed( MouseEvent event )  {
            if ( _knob != null ) {
                _mouseOffset = event.getX() - _knob.getBounds().x - _knob.getRegistrationPoint().x;
            }
            else {
                _mouseOffset = 0;
            }
        }
        
        /**
         * Turns on the knob highlight when the mouse enters the knob.
         */
        public void mouseEntered( MouseEvent event ) {
            if ( _knob != null && _knobHighlight != null ) {
                _knob.setVisible( false );
                _knobHighlight.setVisible( true );
            }
        }
        
        /**
         * Turns off the knob highlight when the mouse exits the knob.
         */
        public void mouseExited( MouseEvent event ) {
            if ( _knob != null && _knobHighlight != null ) {
                _knob.setVisible( true );
                _knobHighlight.setVisible( false );
            }
        }
    }

}