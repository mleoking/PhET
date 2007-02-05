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
    
    /* photon's wavelength, immutable */
    private double _wavelength;
    /* was the photon emitted by the atom? immutable, used by collision detection */
    private boolean _emitted;
    /* did the photon already collide with the atom */
    private boolean _collided;
    
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
        _wavelength = wavelength;
        _emitted = emitted;
        _collided = false;
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
    
    /**
     * Was this photon emitted by the atom?
     * @return true or false
     */
    public boolean isEmitted() {
        return _emitted;
    }
    
    /**
     * When a photon collides with an atom, we set this to true.
     * This excludes the photon from future collision detection,
     * and prevents it from bouncing around inside the atom.
     * @param collided true or false
     */
    public void setCollided( boolean collided ) {
        _collided = collided;
    }
    
    /**
     * Did this photon collide with the atom?
     * @return true or false
     */
    public boolean isCollided() {
        return _collided;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /** Do nothing */
    public void stepInTime( double dt ) {}
    
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
