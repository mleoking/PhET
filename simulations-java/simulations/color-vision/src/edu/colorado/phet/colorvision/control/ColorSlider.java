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
 * SpectrumSlider is a UI component, similar to a JSlider, for selecting a
 * wavelength from the visible spectrum.
 * <p/>
 * The slider track shows the spectrum of colors that correspond to visible
 * wavelengths.  As the slider knob is moved, the knob color changes to match
 * the selected wavelength.  If a transmission width has been set, then a
 * bell curve is overlayed on the spectrum, aligned with the knob.
 * <p/>
 * The slider value is determined by the position of the slider knob,
 * and corresponds to a wavelength in the range VisibleColor.MIN_WAVELENGTH
 * to VisibleColor.MAX_WAVELENGTH, inclusive.
 * <p/>
 * The default orientation of the slider is horizontal. See setOrientation
 * for a description of the UI layout for each orientation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ColorSlider extends GraphicLayerSet {

    private final int TRACK_MARGIN = 10;
    private final int TRACK_WIDTH = 3;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Component _component; // The parent Component.
    private int _value; // The current value.
    private int _minimum, _maximum; // Minimum and maximum range, inclusive.
    private Point _location; // The upper left corner of the spectrum graphic.
    private Rectangle _dragBounds; //The bounds that constrain dragging of the slider knob.
    private EventListenerList _listenerList;

    private final PhetShapeGraphic _background;
    private final PhetShapeGraphic _track;
    private final SpectrumSliderKnob _knob;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param component parent Component
     */
    public ColorSlider( Component component, Color color, Dimension size ) {

        super( null );

        // Initialize instance data.
        _component = component;
        _minimum = 0;
        _maximum = 100;
        _value = _minimum;
        _dragBounds = new Rectangle( 0, 0, 0, 0 ); // set correctly by setLocation
        _listenerList = new EventListenerList();

        Paint backgroundPaint = new GradientPaint( 0f, (float) TRACK_MARGIN, color, 0f, (float) ( size.height - TRACK_MARGIN ), Color.BLACK );

        Shape backgroundShape = new Rectangle2D.Double( 0, 0, size.width, size.height );
        _background = new PhetShapeGraphic( component, backgroundShape, backgroundPaint, new BasicStroke( 1f ), Color.WHITE );
        addGraphic( _background );

        Shape trackShape = new Rectangle2D.Double( ( size.width - TRACK_WIDTH ) / 2, TRACK_MARGIN, TRACK_WIDTH, size.height - ( 2 * TRACK_MARGIN ) );
        _track = new PhetShapeGraphic( component, trackShape, Color.LIGHT_GRAY, new BasicStroke( 0.5f ), Color.DARK_GRAY );
        addGraphic( _track );

        _knob = new SpectrumSliderKnob( component, new Dimension( 15, 20 ), Math.toRadians( -90 ) );
        _knob.setPaint( Color.GRAY );
        _knob.setBorderColor( Color.WHITE );
        addGraphic( _knob );

        // Initialize interactivity
        _knob.setCursorHand();
        _knob.addMouseInputListener( new SpectrumSliderMouseInputListener() );

        // This call recalculates the location of all graphic elements.
        setLocation( 0, 0 );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the slider value.
     *
     * @param value the value, silently clamped to the slider's range (ala JSlider)
     */
    public void setValue( int value ) {
        System.out.println( "ColorSlider.setValue " + value );

        // Silently clamp the value to the allowed range.
        _value = (int) MathUtil.clamp( _minimum, value, _maximum );

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
        int value = (int) ( percent * ( _maximum - _minimum ) ) + _minimum;
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
     * Gets the bounds.
     *
     * @return the bounds
     */
    public Rectangle getBounds() {

        // Start with the spectrum graphic's bounds.
        // Make a copy, so we don't accidentally change the graphic's bounds.
        Rectangle bounds = new Rectangle( _background.getBounds() );

        // Add the knob's bounds.
        bounds.add( _knob.getBounds() );

        return bounds;
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
        Rectangle spectrumBounds = _background.getBounds();
        _dragBounds = new Rectangle( x + spectrumBounds.width, y, 0, spectrumBounds.height );

        // Update the knob.
        updateKnob();
    }

    /*
     * Updates the knob based on the current location and value.
     * This method is shared by setter methods.
     */
    private void updateKnob() {

        // Set the knob's location.
        double percent = 1 - ( _value - _minimum ) / (double) ( _maximum - _minimum );
        int x = _dragBounds.x;
        int y = _dragBounds.y + (int) ( percent * _dragBounds.height );
        _knob.setLocation( x, y );

        repaint();
    }

    //----------------------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------------------

    /**
     * Repaints the slider.
     */
    public void repaint() {

        Rectangle r = getBounds();
        _component.repaint( r.x, r.y, r.width, r.height );
    }

    /**
     * Draws the slider.
     *
     * @param g2 the graphics context
     */
    public void paint( Graphics2D g2 ) {

        if ( super.isVisible() ) {

            // Draw the specturm & slider knob.
            super.paint( g2 );

            //BoundsOutliner.paint( g2, getBounds(), Color.GREEN, new BasicStroke(1f) ); // DEBUG
        }
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
        for ( int i = 0; i < listeners.length; i += 2 ) {
            if ( listeners[i] == ChangeListener.class ) {
                ( (ChangeListener) listeners[i + 1] ).stateChanged( event );
            }
        }
    }

    /**
     * SpectrumSliderMouseInputListener is an inner class the handles
     * dragging of the slider knob.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Id$
     */
    private class SpectrumSliderMouseInputListener extends MouseInputAdapter {

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