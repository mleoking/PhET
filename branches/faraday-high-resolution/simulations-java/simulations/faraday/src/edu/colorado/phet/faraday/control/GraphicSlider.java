/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.control;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;

/**
 * GraphicSlider is a PhetGraphic UI component that is similar to a JSlider.
 * <p>
 * The default orientation is horizontal.
 * Unlike JSlider, there is no setOrientation method.
 * Use the rotate method to set an arbitrary orientation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GraphicSlider extends GraphicLayerSet {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Graphics layers
    private static final double BACKGROUND_LAYER = 0;
    private static final double TICK_LAYER = 1;
    private static final double TRACK_LAYER = 2;
    private static final double KNOB_LAYER = 3;
    private static final double KNOB_HIGHLIGHT_LAYER = 4;
    
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
    // Size of tick marks.
    private Dimension _tickSize;
    // Event listeners.
    private EventListenerList _listenerList;
    // Listener for knob mouse events.
    private KnobListener _knobListener;

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
        super( component );
        
        // Initialize instance data.
        _knob = _knobHighlight = _track = _background = null;
        _knobHighlight = null;
        _dragBounds = new Rectangle();
        _minimum = 0;
        _maximum = 100;
        _value = ( _maximum - _minimum ) / 2;
        _tickSize = new Dimension( 1, 12 );
        _listenerList = new EventListenerList();
        _knobListener = new KnobListener();
        
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
            _knob.addMouseInputListener( _knobListener );
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
            _knobHighlight.addMouseInputListener( _knobListener );
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
    
    /**
     * Sets the size used for subsequent tick marks added using setTick.
     * Previous tick marks are not modified.
     * 
     * @param tickSize the tick size
     */
    public void setTickSize( Dimension tickSize ) {
        if ( tickSize != null ) {
            _tickSize.setSize( tickSize );
        }
    }
    
    /**
     * Gets the tick size.
     * 
     * @return the tick size.
     */
    public Dimension getTickSize() {
        return new Dimension( _tickSize );
    }
    
    /**
     * Adds a tick mark at the specified value.
     * This call is ignored if the slider has no track, or if the value is outside 
     * the min/max range.
     * 
     * @param tickValue the tick value
     */
    public void addTick( int tickValue ) {
        if ( _track != null && tickValue >= _minimum && tickValue <= _maximum ) {
            
            Shape shape = new Line2D.Double( 0, 0, 0, _tickSize.height );
            PhetShapeGraphic tick = new PhetShapeGraphic( getComponent() );
            tick.setShape( shape );
            tick.setBorderColor( Color.BLACK );
            tick.setStroke( new BasicStroke( (float) _tickSize.width ) );
            
            double percent = ( tickValue - _minimum ) / (double) ( _maximum - _minimum );
            int x = _dragBounds.x + (int) ( percent * _dragBounds.width );
            int y = _dragBounds.y;
            tick.setLocation( x, y );
            
            addGraphic( tick, TICK_LAYER );
        }
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
        int x = _dragBounds.x + (int) ( percent * _dragBounds.width );
        int y = _dragBounds.y;
        
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

        private Point2D _somePoint; // reusable point
        
        /** Sole constructor. */
        public KnobListener() {
            super();
            _somePoint = new Point2D.Double();
        }
        
        /**
         * Handles mouse drag events, related to moving the knob.
         * The knob's motion is constrained so that it behaves like a JSlider.
         * All calculations are performed relative to a slider in its default
         * (horizontal) orientation.
         * 
         * @param event the MouseEvent
         */
        public void mouseDragged( MouseEvent event ) {
            
            // Inverse transform the mouse coordinates to match a slider in its default (horizontal) orientation.
            int mouseX = 0;
            try {
                AffineTransform transform = getNetTransform();
                transform.inverseTransform( event.getPoint(), _somePoint /* output */ );
                mouseX = (int) _somePoint.getX();
            }
            catch ( NoninvertibleTransformException e ) {
                e.printStackTrace();
            }
            
            // Constrain the knob position to the drag boundaries.
            int x = (int) Math.max( _dragBounds.x, Math.min( _dragBounds.x + _dragBounds.width, mouseX ) );

            // Determine the value that corresponds to the constrained location.
            double percent = ( x - _dragBounds.x ) / (double) ( _dragBounds.width );
            int value = (int) ( percent * ( _maximum - _minimum ) ) + _minimum;

            // Set the new value.
            setValue( value );
        }
        
        /**
         * Turns on the knob highlight when the mouse enters the knob.
         * 
         * @param event the MouseEvent
         */
        public void mouseEntered( MouseEvent event ) {
            if ( _knob != null && _knobHighlight != null && _knob.getBounds().contains( event.getPoint() ) ) {
                _knobHighlight.setVisible( true );
            }
        }
        
        /**
         * Turns off the knob highlight when the mouse exits the knob.
         * 
         * @param event the MouseEvent
         */
        public void mouseExited( MouseEvent event ) {
            if ( _knob != null && _knobHighlight != null && !_knobHighlight.getBounds().contains( event.getPoint() ) ) {
                _knobHighlight.setVisible( false );
            }
        }
    }
}