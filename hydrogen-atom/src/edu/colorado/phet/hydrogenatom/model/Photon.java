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
 * Photons move with constant speed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Photon extends MovingObject implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _wavelength;
    private boolean _emitted;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param wavelength wavelength, in nanometers
     * @param position
     * @param orientation rotation angle, in radians
     * @param speed distance per dt
     */
    public Photon( double wavelength, Point2D position, double orientation, double speed ) {
        this( wavelength, position, orientation, speed, false /* emitted */ );
    }
    
    /**
     * Constructor.
     * @param wavelength wavelength, in nanometers
     * @param position
     * @param orientation rotation angle, in radians
     * @param speed distance per dt
     * @param emitted was this photon emitted by an atom?
     */
    public Photon( double wavelength, Point2D position, double orientation, double speed, boolean emitted ) {
        super( position, orientation, speed );
        if ( wavelength < 0 ) {
            throw new IllegalArgumentException( "invalid wavelength: " + wavelength );
        }
        _emitted = emitted;
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
    
    public boolean wasEmitted() {
        return _emitted;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        double distance = getSpeed() * dt;
        move( distance );
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
