/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import java.awt.geom.Point2D;


/**
 * HollywoodMagnet is a magnet that does not correspond to any real-world
 * physical model.  The behavior of the magnetic field is faked to 
 * provide results that look like a rough approximation of a magnetic field.
 * <p>
 * This class was used for testing while I was waiting to get the
 * actual physical models from PhET physicists.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HollywoodMagnet extends AbstractMagnet {
    
    /**
     * Sole constructor.
     */
    public HollywoodMagnet() {
        super();
    }

    /**
     * Gets the strength of the magnetic field at a point in 2D space.
     * In this case, we are totally faking the result.
     * There is no magnetic field that behaves this way,
     * but the results look like a rough approximation of a 
     * magnetic field.
     * 
     * @see edu.colorado.phet.faraday.model.IMagnet#getDirection(java.awt.geom.Point2D)
     */
    public double getStrength( final Point2D p ) {
        
        double fieldStrength = 0.0;
        
        double strength = super.getStrength();
        double dx = p.getX() - super.getX();
        double dy = p.getY() - super.getY();
        double distance = Math.sqrt( Math.pow(dx,2) + Math.pow(dy,2) );
        
        if ( distance > strength ) {
            fieldStrength = 0;
        }
        else {
            // XXX Assume that "strength" is the radius (in pixels) of the magnet field.
            fieldStrength = strength - (strength * (distance / strength ));
        }
        
        return fieldStrength;
    }
    
    /**
     * Gets the direction of the magnet field at a point in 2D space.
     * In this case, we are totally faking the result.
     * There is no magnetic field that behaves this way,
     * but the results look like a rough approximation of a 
     * magnetic field.
     * 
     * @see edu.colorado.phet.faraday.model.IMagnet#getDirection(java.awt.geom.Point2D)
     */
    public double getDirection( final Point2D p ) {
      
        double fieldDirection = 0.0;
        
        // Magnet paramters
        double x = super.getX();
        double y = super.getY();
        double w = super.getWidth();
        double h = super.getHeight();
        double direction = super.getDirection();
        
        if ( p.getX() <= x - w/2 ) {
            // Point is to left of magnet
            double opposite = y - p.getY();
            double adjacent = ( x - w/2 ) - p.getX();
            double theta = Math.toDegrees( Math.atan( opposite / adjacent ) );
            fieldDirection = direction + theta;
        }
        else if ( p.getX() >= x + w/2 ) {
            // Point is to right of magnet...
            double opposite = p.getY() - y;
            double adjacent = p.getX() - ( x + w/2 );
            double theta = Math.toDegrees( Math.atan( opposite / adjacent ) );
            fieldDirection = direction + theta;
        }
        else if ( p.getY() <= y - h/2 ) {
            // Point is above the magnet...
            double multiplier = ( x + w/2 - p.getX() ) / w;
            fieldDirection = direction - 90 - ( multiplier * 180 );
        }
        else if ( p.getY() >= y + h/2 ) {
            // Point is below the magnet...
            double multiplier = ( x + w/2 - p.getX() ) / w;
            fieldDirection = direction + 90 + ( multiplier * 180 );
        }
        else {
            // Point is inside the magnet...
            fieldDirection = direction + 180;
        }

        return ( fieldDirection % 360 );
    }
}
