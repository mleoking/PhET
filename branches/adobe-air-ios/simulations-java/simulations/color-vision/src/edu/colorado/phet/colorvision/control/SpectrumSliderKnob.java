// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.colorvision.control;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;

/**
 * SpectrumSliderKnob is the knob on a SpectrumSlider.
 * The origin is at the knob's tip.
 * The default orientation is with the tip of the arrow pointing straight up.
 * <p>
 * Note that this class is declared without a visibility modifier.
 * Visibility therefore defaults to "package visible", which makes it
 * visible only to other classes within the same package.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SpectrumSliderKnob extends PhetShapeGraphic {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Location of the knob's tip.
    private Point _location;
    // Size of the knob
    private Dimension _size;
    // Rotation angle
    private double _angle;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * Creates a white knob with a black border, located at (0,0).
     * 
     * @param component the parent Component
     * @param size dimensions in pixels
     * @param angle rotation angle, in radians
     */
    public SpectrumSliderKnob( Component component, Dimension size, double angle ) {

        super( component, null, null );

        _location = new Point( 0, 0 );
        _size = new Dimension( size );
        _angle = angle;

        //  Request antialiasing.
        RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.setRenderingHints( hints );

        super.setPaint( Color.WHITE );
        super.setStroke( new BasicStroke( 1f ) );
        super.setBorderColor( Color.BLACK );

        updateShape();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the knob's location.
     * 
     * @param location the location
     */
    public void setLocation( Point location ) {

        super.translate( -_location.x, -_location.y );
        _location = new Point( location );
        super.translate( location.x, location.y );
    }

    /**
     * Convenience method for setting the knob's location.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void setLocation( int x, int y ) {

        this.setLocation( new Point( x, y ) );
    }

    /**
     * Gets the knob's location.\
     * 
     * @return the location
     */
    public Point getLocation() {

        return new Point( _location );
    }

    /**
     * Sets the knob's size.
     * 
     * @param size the size
     */
    public void setSize( Dimension size ) {

        _size = new Dimension( size );
        updateShape();
    }

    /**
     * Gets the knob's size.
     * 
     * @return the size
     */
    public Dimension getSize() {

        return new Dimension( _size );
    }

    /**
     * Sets the angle of rotation.
     * Rotation is performed about the tip of the knob.
     * At zero degrees, the tip is pointing straight up.
     * 
     * @param angle the angle, in radians
     */
    public void setAngle( double angle ) {

        _angle = angle;
        updateShape();
    }

    /**
     * Gets the angle of rotation.
     * 
     * @return the angle, in degrees
     */
    public double getAngle() {

        return _angle;
    }

    //----------------------------------------------------------------------------
    // Shape initialization
    //----------------------------------------------------------------------------

    /*
     * Updates the knob's shape, based on its size and angle.
     */
    private void updateShape() {

        GeneralPath path = new GeneralPath();

        // counterclockwise, starting at the tip
        path.moveTo( 0, 0 );
        path.lineTo( -0.5f * _size.width, 0.3f * _size.height );
        path.lineTo( -0.5f * _size.width, 1f * _size.height );
        path.lineTo( 0.5f * _size.width, 1f * _size.height );
        path.lineTo( 0.5f * _size.width, 0.3f * _size.height );
        path.closePath();
        Shape shape = path;

        // Rotate and translate.
        AffineTransform transform = new AffineTransform();
        transform.translate( _location.x, _location.y );
        transform.rotate( _angle );
        shape = transform.createTransformedShape( shape );

        super.setShape( shape );
    }

}