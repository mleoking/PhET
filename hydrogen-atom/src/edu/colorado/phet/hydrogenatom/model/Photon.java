/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.hydrogenatom.util.ColorUtils;
import edu.colorado.phet.hydrogenatom.util.DebugUtils;

/**
 * Photon is the model of a photon.
 * A photon has a position and direction of motion.
 * It also has an immutable wavelength.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Photon extends DynamicObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double DISTANCE_PER_DT = 5;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _wavelength;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param position
     * @param orientation rotation angle, in radians
     * @param wavelength wavelength, in nanometers
     */
    public Photon( Point2D position, double orientation, double wavelength ) {
        super( position, orientation );
        if ( wavelength < 0 ) {
            throw new IllegalArgumentException( "invalid wavelength: " + wavelength );
        }
        _wavelength = wavelength;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the photon's wavelength.
     * @param wavelength in nanometers
     */
    public double getWavelength() {
        return _wavelength;
    }
    
    /**
     * Gets the Color associated with the photon's wavelength.
     * @return Color
     */
    public Color getColor() {
        return ColorUtils.wavelengthToColor( _wavelength );
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        move( DISTANCE_PER_DT * dt );
    }
    
    /*
     * Moves the photon a specified distance, in the direction of its orientation.
     */
    private void move( double distance ) {
        double direction = getOrientation();
        double dx = Math.cos( direction ) * distance;
        double dy = Math.sin( direction ) * distance;
        double x = getX() + dx;
        double y = getY() + dy;
        setPosition( x, y );
    }
    
    //----------------------------------------------------------------------------
    // Object overrides
    //----------------------------------------------------------------------------
    
    public String toString() {
        String s = "Photon ";
        s += ( "id=" + getId() + " " );
        s += ( "wavelength=" + DebugUtils.format( _wavelength ) + " " );
        s += ( "position=" + DebugUtils.format( getPositionRef() ) + " " );
        s += ( "orientation=" + DebugUtils.format( Math.toDegrees( getOrientation() ) ) + " " );
        return s;
    }
}
