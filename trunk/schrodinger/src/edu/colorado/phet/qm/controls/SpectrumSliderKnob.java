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

import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * SpectrumSliderKnob is the knob on a SpectrumSlider.
 * The origin is at the knob's tip.
 * The default orientation is with the tip of the arrow pointing straight up.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SpectrumSliderKnob extends PPath {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

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
     * @param size      dimensions in pixels
     * @param angle     rotation angle, in radians
     */
    public SpectrumSliderKnob( Component component, Dimension size, double angle ) {
        _size = new Dimension( size );
        _angle = angle;

        super.setPaint( Color.WHITE );
        super.setStroke( new BasicStroke( 1f ) );
        super.setStrokePaint( Color.BLACK );

        updateShape();
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

        path.moveTo( 0, 0 );
        path.lineTo( -0.5f * _size.width, 0.3f * _size.height );
        path.lineTo( -0.5f * _size.width, 1f * _size.height );
        path.lineTo( 0.5f * _size.width, 1f * _size.height );
        path.lineTo( 0.5f * _size.width, 0.3f * _size.height );
        path.closePath();

        super.setPathTo( path );
    }

}