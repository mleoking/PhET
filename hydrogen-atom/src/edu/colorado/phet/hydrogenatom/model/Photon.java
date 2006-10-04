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

import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.hydrogenatom.HAConstants;
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
public class Photon extends MovingObject {

    private static final double DISTANCE_PER_DT = 1;
    
    private static int ID = 0;
    
    private double _id;
    private double _wavelength;
    
    public Photon( Point2D position, double direction, double wavelength ) {
        super( position, direction );
        if ( wavelength < 0 ) {
            throw new IllegalArgumentException( "invalid wavelength: " + wavelength );
        }
        _wavelength = wavelength;
        _id = ID++;
    }
    
    public double getWavelength() {
        return _wavelength;
    }
    
    public Color getColor() {
        return ColorUtils.wavelengthToColor( _wavelength );
    }
    
    public void simulationTimeChanged( ClockEvent event ) {
        double dt = event.getSimulationTimeChange();
        double distance = DISTANCE_PER_DT * dt;
        move( distance );
    }
    
    private void move( double distance ) {
        double direction = getDirection();
        double dx = Math.cos( direction ) * distance;
        double dy = Math.sin( direction ) * distance;
        double x = getX() + dx;
        double y = getY() + dy;
        setPosition( x, y );
        System.out.println( "Photon.move distance=" + distance + " " + this );
    }
    
    public String toString() {
        String s = "Photon ";
        s += ( "id=" + _id + " " );
        s += ( "wavelength=" + DebugUtils.format( _wavelength ) + " " );
        s += ( "position=" + DebugUtils.format( getPositionRef() ) + " " );
        s += ( "direction=" + DebugUtils.format( Math.toDegrees( getDirection() ) ) + " " );
        return s;
    }
}
