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

    private static final double DISTANCE_PER_DT = 5;
    
    private double _wavelength;
    
    public Photon( Point2D position, double orientation, double wavelength ) {
        super( position, orientation );
        if ( wavelength < 0 ) {
            throw new IllegalArgumentException( "invalid wavelength: " + wavelength );
        }
        _wavelength = wavelength;
    }
    
    public double getWavelength() {
        return _wavelength;
    }
    
    public Color getColor() {
        return ColorUtils.wavelengthToColor( _wavelength );
    }
    
    public void stepInTime( double dt ) {
        move( DISTANCE_PER_DT * dt );
    }
    
    private void move( double distance ) {
        double direction = getOrientation();
        double dx = Math.cos( direction ) * distance;
        double dy = Math.sin( direction ) * distance;
        double x = getX() + dx;
        double y = getY() + dy;
        setPosition( x, y );
    }
    
    public String toString() {
        String s = "Photon ";
        s += ( "id=" + getId() + " " );
        s += ( "wavelength=" + DebugUtils.format( _wavelength ) + " " );
        s += ( "position=" + DebugUtils.format( getPositionRef() ) + " " );
        s += ( "orientation=" + DebugUtils.format( Math.toDegrees( getOrientation() ) ) + " " );
        return s;
    }
}
