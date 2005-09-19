/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * SpectrumSlider is a UI component, similar to a JSlider, for selecting a
 * wavelength from the visible spectrum.
 * <p/>
 * The slider track shows the spectru of colors that correspond to visible
 * wavelengths.  As the slider knob is moved, the knob color changes to match
 * the seleted wavelength.  If a transmission width has been set, then a
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
//public class SpectrumSlider extends GraphicLayerSet {
public class SpectrumSlider extends PNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    /**
     * Horizontal orientation
     */
    public static final int HORIZONTAL = JSlider.HORIZONTAL;
    /**
     * Vertical orientation
     */
    public static final int VERTICAL = JSlider.VERTICAL;

    // Default knob dimensions
    private static final Dimension DEFAULT_KNOB_SIZE = new Dimension( 20, 30 );
    // Rotation angle for horizontal orientation, in radians.
    private static final double HORIZONTAL_ROTATION_ANGLE = Math.toRadians( 0 );
    // Rotation angle for vertical orientation, in radians.
    private static final double VERTICAL_ROTATION_ANGLE = Math.toRadians( -90 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // The parent Component.
    private Component _component;
    // The current value.
    private int _value;
    // Minimum and maximum range, inclusive.
    private int _minimum, _maximum;
    // Transmission width, in wavelengths.
    private double _transmissionWidth;
    // The upper left corner of the spectrum graphic.
    private Point _location;
    // Orientation
    private int _orientation;
    //The bounds that constrain dragging of the slider knob.
    private Rectangle _dragBounds;
    // Location of the mouse relative to the knob when a drag is started.
    private int _mouseOffset;
    // Event listeners.
    private EventListenerList _listenerList;

    // The spectrum graphic.
    private PhetImageGraphic _spectrum;
    // The slider knob.
    private SpectrumSliderKnob _knob;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param component parent Component
     */
    public SpectrumSlider( Component component ) {

        super();

        // Initialize instance data.
        _component = component;
        _value = 0;
        _minimum = (int)VisibleColor.MIN_WAVELENGTH;
        _maximum = (int)VisibleColor.MAX_WAVELENGTH;
        _orientation = HORIZONTAL;
        _transmissionWidth = 0;
        _dragBounds = new Rectangle( 0, 0, 0, 0 ); // set correctly by setLocation
        _mouseOffset = 0;
        _listenerList = new EventListenerList();

        // Initialize graphical components.
        _spectrum = new PhetImageGraphic( component, ColorVisionConfig.SPECTRUM_IMAGE );
        _knob = new SpectrumSliderKnob( component, DEFAULT_KNOB_SIZE, getRotationAngle() );

        // Initialize interactivity
        super.addChild( _knob );
        super.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        //todo piccolo
//        super.addMouseInputListener( new SpectrumSliderMouseInputListener() );

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

        // Silently clamp the value to the allowed range.
        _value = (int)MathUtil.clamp( _minimum, value, _maximum );

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

        double percent;
        if( _orientation == HORIZONTAL ) {
            percent = ( x - _dragBounds.x ) / (double)( _dragBounds.width );
        }
        else {
            percent = 1 - ( ( y - _dragBounds.y ) / (double)( _dragBounds.height ) );
        }
        int value = (int)( percent * ( _maximum - _minimum ) ) + _minimum;
        return value;
    }

    /**
     * Sets the minimum value of the slider's range.
     *
     * @param minimum the minimum
     */
    protected void setMinimum( int minimum ) {

        _minimum = minimum;
        updateKnob();
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
    protected void setMaximum( int maximum ) {

        _maximum = maximum;
        updateKnob();
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
     * Sets the orientation.
     * <p/>
     * For a HORIZONTAL orientation, the slider knob is below the spectrum,
     * the minimum value is at the left, and the maximum value is at the right.
     * <p/>
     * For a VERTICAL orientation, the slider knob is to the right of the spectrum,
     * the minimum value is at the bottom, and the maximum value is at the top.
     *
     * @param orientation HORIZONTAL or VERTICAL
     * @throws IllegalArgumentException if orientation is invalid
     */
    public void setOrientation( int orientation ) {

        if( orientation != HORIZONTAL && orientation != VERTICAL ) {
            throw new IllegalArgumentException( "invalid orientation: " + orientation );
        }
        else if( orientation != _orientation ) {
            _orientation = orientation;
            _knob.setAngle( getRotationAngle() );
            updateUI();
        }
    }

    /**
     * Gets the orientation.
     *
     * @return HORIZONTAL or VERTICAL
     */
    public int getOrientation() {

        return _orientation;
    }

    /**
     * Sets the transmission width.
     * Setting the width to zero effectively disables drawing of the curve.
     *
     * @param width the width, in wavelengths
     */
    public void setTransmissionWidth( double width ) {

        _transmissionWidth = width;
        repaint();
    }

    /**
     * Gets the transmission width.
     *
     * @return width in wavelengths
     */
    public double getTransmissionWidth() {

        return _transmissionWidth;
    }

    /**
     * Sets the location.
     * The location corresponds to the upper-left corner of the spectrum graphic.
     *
     * @param location the location
     */
    public void setLocation( Point location ) {

        _location = location;
        updateUI();
    }

    /**
     * Convenience method for setting location.
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void setLocation( int x, int y ) {

        this.setLocation( new Point( x, y ) );
    }

    /**
     * Gets the location.
     * The location corresponds to the upper-left corner of the spectrum graphic.
     *
     * @return the location
     */
    public Point getLocation() {

        return _location;
    }

    /**
     * Sets the slider's spectrum graphic size.
     * Note that these are the dimensions of a spectrum with HORIZONTAL orientation.
     *
     * @param size the desired size
     */
    public void setSpectrumSize( Dimension size ) {

        // Read a fresh copy of the image.
        String resourceName = ColorVisionConfig.SPECTRUM_IMAGE;
        BufferedImage image;
        try {
            image = ImageLoader.loadBufferedImage( resourceName );
        }
        catch( IOException e ) {
            throw new RuntimeException( "Image resource not found: " + resourceName );
        }

        // Calculate the scaling.
        double scaleX = (double)size.width / (double)image.getWidth();
        double scaleY = (double)size.height / (double)image.getHeight();

        // Scale the image.
        AffineTransform tx = new AffineTransform();
        tx.scale( scaleX, scaleY );
        AffineTransformOp op = new AffineTransformOp( tx, AffineTransformOp.TYPE_BILINEAR );
        BufferedImage newImage = op.filter( image, null );

        // HACK: create a new PhetImageGraphic - setImage on the old one doesn't work right.
        _spectrum = new PhetImageGraphic( _component, newImage );

        updateUI();
    }

    /**
     * Sets the size of the slider knob.
     * Note that these are the dimension of a knob with HORIZONTAL orientation.
     *
     * @param knobSize the dimensions of the knob
     */
    public void setKnobSize( Dimension knobSize ) {

        _knob.setSize( knobSize );
        repaint();
    }

    //todo piccolo
//    /**
//     * Gets the bounds.
//     *
//     * @return the bounds
//     */
//    public Rectangle getBounds() {
//
//        // Start with the spectrum graphic's bounds.
//        // Make a copy, so we don't accidentally change the graphic's bounds.
//        Rectangle bounds = new Rectangle( _spectrum.getBounds() );
//
//        // Add the knob's bounds.
//        bounds.add( _knob.getBounds() );
//
//        return bounds;
//    }

    /*
     * Overrides superclass implementation.
     * The superclass doesn't properly repaint.
     *
     * @param visible true for visible, false for invisible
     */
    public void setVisible( boolean visible ) {

        if( visible != super.getVisible() ) {
            super.setVisible( visible );
            repaint();
        }
    }

    /**
     * Sets the border color used to outline the knob.
     * Setting this to null effectively disables the border.
     *
     * @param color the Color to use for the border
     */
    public void setKnobBorderColor( Color color ) {

        _knob.setStrokePaint( color );
        repaint();
    }

    /**
     * Gets the rotation angle that corresponds to the orientation.
     *
     * @return the angle, in radians
     */
    private double getRotationAngle() {

        double angle;
        if( _orientation == HORIZONTAL ) {
            angle = HORIZONTAL_ROTATION_ANGLE;
        }
        else {
            angle = VERTICAL_ROTATION_ANGLE;
        }
        return angle;
    }

    /*
     * Updates all graphical components.
     * This method is shared by setter methods.
     */
    private void updateUI() {

        Rectangle spectrumBounds = _spectrum.getBounds();
        PBounds knobBounds = _knob.getBounds();
        int x = _location.x;
        int y = _location.y;

        if( _orientation == HORIZONTAL ) {
            // Translate the spectrum graphic.
            _spectrum.setLocation( x, y );

            // Set drag bounds.
            _dragBounds = new Rectangle( 0, spectrumBounds.height, spectrumBounds.width, 0 );
//            _dragBounds = new Rectangle( x, y + spectrumBounds.height, spectrumBounds.width, 0 );
        }
        else {
            // Rotate and translate the spectrum graphic.
            double angle = getRotationAngle();
            AffineTransform transform = AffineTransform.getRotateInstance( angle );
            _spectrum.setTransform( transform );
            _spectrum.setLocation( x, y + spectrumBounds.height );

            // Set drag bounds.
            _dragBounds = new Rectangle( spectrumBounds.width, 0, 0, spectrumBounds.height );
//            _dragBounds = new Rectangle( x + spectrumBounds.width, y, 0, spectrumBounds.height );
        }

        // Update the knob.
        updateKnob();
    }

    /*
     * Updates the knob based on the current location and value.
     * This method is shared by setter methods.
     */
    private void updateKnob() {

        // Set the knob's location.
        int x, y;
        if( _orientation == HORIZONTAL ) {
            double percent = ( _value - _minimum ) / (double)( _maximum - _minimum );
//            x = (int) ( percent * _dragBounds.width );
//            y = (int)_knob.getLocation().getY();
            x = _dragBounds.x + (int)( percent * _dragBounds.width );
            y = _dragBounds.y;
        }
        else {
            double percent = 1 - ( _value - _minimum ) / (double)( _maximum - _minimum );
            x = (int)_knob.getOffset().getX();
            y = (int)( percent * _dragBounds.height );
            x = _dragBounds.x;
            y = _dragBounds.y + (int)( percent * _dragBounds.height );
        }
//        _knob.setLocation( x , y );
        _knob.setOffset( x + (int)this.getLocation().getX(), y + (int)this.getLocation().getY() );

        // Set the knob's color.
        VisibleColor color = new VisibleColor( _value );
        _knob.setPaint( color.toColor() );

        repaint();
    }

    //----------------------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------------------

    //todo piccolo
//    /**
//     * Repaints the slider.
//     */
//    public void repaint() {
//
//        PBounds r = getBounds();
//        _component.repaint( r.x, r.y, r.width, r.height );
//    }

    /**
     * Draws the slider.
     *
     * @param g2 the graphics context
     */
//    public void paint( Graphics2D g2 ) {
//
//        if( super.isVisible() ) {
//            // Draw the spectrum graphic.
//            _spectrum.paint( g2 );
//
//            // Draw the slider knob.
//            super.paint( g2 );
//
//            // Draw the optional transmission width curve.
//            if( _transmissionWidth > 0 ) {
//                // Calculate location.
//                int x, y, w, h;
//                if( _orientation == HORIZONTAL ) {
//                    x = _knob.getLocation().x;
//                    y = _spectrum.getBounds().y;
//                    w = getCurveWidth();
//                    h = _spectrum.getBounds().height;
//                }
//                else {
//                    x = _spectrum.getBounds().x;
//                    y = _knob.getLocation().y;
//                    w = getCurveWidth();
//                    h = _spectrum.getBounds().width;
//                }
//
//                // Create the curve.
//                BellCurve curve = new BellCurve( _component, x,
////                BellCurve curve = new BellCurve( _component, x + (int)this.getLocation().getX(),
//                                                 (int)this.getLocation().getY(),
////                                                 y + (int)this.getLocation().getY(),
//                                                 w, h, getRotationAngle() );
//
//                // Save graphics state.
//                Shape oldClip = g2.getClip();
//
//                // Draw the curve, clipped to the spectrum graphic.
//                g2.setClip( _spectrum.getBounds() );
//                curve.paint( g2 );
//
//                // Restore graphics state.
//                g2.setClip( oldClip );
//            }
//
//            //BoundsOutliner.paint( g2, getBounds(), Color.GREEN, new BasicStroke(1f) ); // DEBUG
//        }
//    }

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
                ( (ChangeListener)listeners[i + 1] ).stateChanged( event );
            }
        }
    }

    /**
     * SpectrumSliderMouseInputListener is an inner class the handles
     * dragging of the slider knob.
     * <p/>
     * Note that SpectrumSlider cannot implement MouseListener because
     * its superclass already does.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Id$
     */
    private class SpectrumSliderMouseInputListener extends MouseInputAdapter {

        /**
         * Handles mouse drag events, related to moving the slider knob.
         * The slider knob's motion is constrained so that it behaves like
         * a JSlider.
         *
         * @param event the mouse event
         */
        public void mouseDragged( MouseEvent event ) {

            // Get the proposed knob coordinates.
            int knobX, knobY;
            if( _orientation == HORIZONTAL ) {
                knobX = event.getX() - _mouseOffset - (int)getLocation().getX();
                knobY = event.getY();
            }
            else {
                knobX = event.getX();
                knobY = event.getY() - _mouseOffset - (int)getLocation().getY();
            }

            // Constrain the drag boundaries of the knob.
            int x = (int)Math.max( _dragBounds.x, Math.min( _dragBounds.x + _dragBounds.width, knobX ) );
            int y = (int)Math.max( _dragBounds.y, Math.min( _dragBounds.y + _dragBounds.height, knobY ) );

            // Determine the value that corresponds to the constrained location.
            int value = getValue( x, y );

            // If the value will be changed, set the new value.
            if( value != getValue() ) {
                setValue( value );
            }
        }

        /**
         * Handles mouse press events.
         * Remembers how far the mouse was from the knob's origin at
         * the time the drag began.
         *
         * @param event the mouse event
         */
        public void mousePressed( MouseEvent event ) {

            if( _orientation == HORIZONTAL ) {
                _mouseOffset = (int)( event.getX() - _knob.getOffset().getX() );
            }
            else {
                _mouseOffset = (int)( event.getY() - _knob.getOffset().getY() );
            }
        }
    }

}