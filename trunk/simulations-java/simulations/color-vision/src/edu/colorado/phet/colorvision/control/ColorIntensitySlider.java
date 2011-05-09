// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.colorvision.control;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;

/**
 * Color intensity slider.
 * Intensity is a percentage, with a range of 0-100 inclusive.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ColorIntensitySlider extends GraphicLayerSet {

    private final int MIN = 0;
    private final int MAX = 100;
    private final int TRACK_MARGIN = 15;
    private final int TRACK_WIDTH = 3;

    private int _value; // The current value.
    private Point _location; // The upper left corner of the spectrum graphic.
    private Rectangle _dragBounds; //The bounds that constrain dragging of the slider knob.
    private final EventListenerList _listenerList;

    private final PhetShapeGraphic _background;
    private final PhetShapeGraphic _track;
    private final SpectrumSliderKnob _knob;

    /**
     * Sole constructor.
     *
     * @param component parent Component
     */
    public ColorIntensitySlider( Component component, Color color, Dimension size ) {
        super( component );

        _value = MIN;
        _dragBounds = new Rectangle( 0, 0, 0, 0 ); // set correctly by setLocation
        _listenerList = new EventListenerList();

        // background
        Paint backgroundPaint = new GradientPaint( 0f, (float) TRACK_MARGIN, color, 0f, (float) ( size.height - TRACK_MARGIN ), Color.BLACK );
        Shape backgroundShape = new Rectangle2D.Double( 0, 0, size.width, size.height );
        _background = new PhetShapeGraphic( component, backgroundShape, backgroundPaint, new BasicStroke( 1f ), Color.WHITE );
        addGraphic( _background );

        // track
        Shape trackShape = new Rectangle2D.Double( ( size.width - TRACK_WIDTH ) / 2, TRACK_MARGIN, TRACK_WIDTH, size.height - ( 2 * TRACK_MARGIN ) );
        _track = new PhetShapeGraphic( component, trackShape, Color.LIGHT_GRAY, new BasicStroke( 0.5f ), Color.DARK_GRAY );
        addGraphic( _track );

        // knob
        _knob = new SpectrumSliderKnob( component, new Dimension( 15, 20 ), Math.toRadians( -90 ) );
        _knob.setPaint( Color.GRAY );
        _knob.setBorderColor( Color.WHITE );
        addGraphic( _knob );

        // interactivity
        _knob.setCursorHand();
        _knob.addMouseInputListener( new KnobDragListener() );

        // This call recalculates the location of all graphic elements.
        setLocation( 0, 0 );
    }

    /**
     * Sets the slider value.
     *
     * @param value the value, silently clamped to the slider's range (ala JSlider)
     */
    public void setValue( int value ) {
        System.out.println( "ColorSlider.setValue " + value );//XXX

        // Silently clamp the value to the allowed range.
        _value = (int) MathUtil.clamp( MIN, value, MAX );

        // Fire a ChangeEvent to notify listeners that the value has changed.
        fireChangeEvent( new ChangeEvent( this ) );

        // Update the knob.
        updateKnob();
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
     * Gets the slider value based on a hypothetical location of the knob.
     *
     * @return the value
     */
    private int getValue( int x, int y ) {
        double percent = 1 - ( ( y - _dragBounds.y ) / (double) ( _dragBounds.height ) );
        int value = (int) ( percent * ( MAX - MIN ) ) + MIN;
        return value;
    }

    /**
     * Sets the location.
     * The location corresponds to the upper-left corner of the background graphic.
     *
     * @param location the location
     */
    public void setLocation( Point location ) {
        _location = location;
        updateUI();
    }

    /**
     * Updates all graphical components.
     * This method is shared by setter methods.
     */
    private void updateUI() {

        int x = _location.x;
        int y = _location.y;

        // Translate the background and track.
        _background.setLocation( x, y );
        _track.setLocation( x, y );

        // Set drag bounds.
        _dragBounds = new Rectangle( x + ( ( _background.getBounds().width - _knob.getWidth() ) / 2 ), y + TRACK_MARGIN, 0, _background.getBounds().height - ( 2 * TRACK_MARGIN ) );

        // Update the knob.
        updateKnob();
    }

    /*
     * Updates the knob based on the current location and value.
     * This method is shared by setter methods.
     */
    private void updateKnob() {

        // Set the knob's location.
        double percent = 1 - ( _value - MIN ) / (double) ( MAX - MIN );
        int x = _dragBounds.x;
        int y = _dragBounds.y + (int) ( percent * _dragBounds.height );
        _knob.setLocation( x, y );

        repaint();
    }

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
        for ( int i = 0; i < listeners.length; i += 2 ) {
            if ( listeners[i] == ChangeListener.class ) {
                ( (ChangeListener) listeners[i + 1] ).stateChanged( event );
            }
        }
    }

    // Handles dragging of the slider knob.
    private class KnobDragListener extends MouseInputAdapter {

        private int _mouseOffset; // Location of the mouse relative to the knob when a drag is started.

        /**
         * Handles mouse press events.
         * Remembers how far the mouse was from the knob's origin at
         * the time the drag began.
         *
         * @param event the mouse event
         */
        public void mousePressed( MouseEvent event ) {
            _mouseOffset = event.getY() - _knob.getLocation().y;
        }

        /**
         * Handles mouse drag events, related to moving the slider knob.
         * The slider knob's motion is constrained so that it behaves like
         * a JSlider.
         *
         * @param event the mouse event
         */
        public void mouseDragged( MouseEvent event ) {

            // Get the proposed knob coordinates.
            int knobX = event.getX();
            int knobY = event.getY() - _mouseOffset;

            // Constrain the drag boundaries of the knob.
            int x = (int) Math.max( _dragBounds.x, Math.min( _dragBounds.x + _dragBounds.width, knobX ) );
            int y = (int) Math.max( _dragBounds.y, Math.min( _dragBounds.y + _dragBounds.height, knobY ) );

            // Determine the value that corresponds to the constrained location.
            int value = getValue( x, y );

            // If the value will be changed, set the new value.
            if ( value != getValue() ) {
                setValue( value );
            }
        }
    }

}