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

import edu.colorado.phet.common.math.AbstractVector2D;


/**
 * HollywoodMagnet is a magnet that does not correspond to any real-world
 * physical model.  The behavior of the magnetic field is faked to 
 * provide results that look like a rough approximation of a magnetic field.
 * (According to the physicists, it's "pretty close".)
 * <p>
 * This class was used for testing while I was waiting to get the
 * actual physical models from PhET physicists.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HollywoodMagnet extends AbstractMagnet {
  
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Relationship between distance and strength.
    private static final double DISTANCE_PER_GAUSS = 1.0;
        
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public HollywoodMagnet() {
        super();
    }

    //----------------------------------------------------------------------------
    // AbstractMagnet implementation
    //----------------------------------------------------------------------------
    
    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#getStrength(java.awt.geom.Point2D)
     */
    public AbstractVector2D getStrength( Point2D p ) {
        assert( p != null );
        
        /* 
         * Magnitude (in Gauss) drops off linearly as distance increases.
         */
        double magnitude = 0.0;
        {
            double strength = super.getStrength();
            double distance = p.distance( super.getLocation() );

            double range = strength * DISTANCE_PER_GAUSS;
            if ( distance > range ) {
                magnitude = 0;
            }
            else {
                magnitude = strength - ( distance / DISTANCE_PER_GAUSS );
            }
        }
        
        /*
         * Angle (in radians) is based on whether the point is inside the magnet,
         * or in one of the four surrounding quadrants.  This should work for 
         * arbitrary orientations of the magnet.
         */
        double angle = Math.toRadians( 0.0 );
        if ( magnitude > 0 )
        {
            double fieldDirection = 0.0;

            // Magnet paramters
            double x = super.getX();
            double y = super.getY();
            double w = super.getWidth();
            double h = super.getHeight();
            double direction = super.getDirection();

            if( p.getX() <= x - w / 2 ) {
                // Point is to left of magnet.
                double opposite = y - p.getY();
                double adjacent = ( x - w / 2 ) - p.getX();
                double theta = Math.toDegrees( Math.atan( opposite / adjacent ) );
                fieldDirection = direction + theta;
            }
            else if( p.getX() >= x + w / 2 ) {
                // Point is to right of magnet.
                double opposite = p.getY() - y;
                double adjacent = p.getX() - ( x + w / 2 );
                double theta = Math.toDegrees( Math.atan( opposite / adjacent ) );
                fieldDirection = direction + theta;
            }
            else if( p.getY() <= y - h / 2 ) {
                // Point is above the magnet.
                double multiplier = ( x + w / 2 - p.getX() ) / w;
                fieldDirection = direction - 90 - ( multiplier * 180 );
            }
            else if( p.getY() >= y + h / 2 ) {
                // Point is below the magnet.
                double multiplier = ( x + w / 2 - p.getX() ) / w;
                fieldDirection = direction + 90 + ( multiplier * 180 );
            }
            else {
                // Point is inside the magnet.
                fieldDirection = direction + 180;
            }

            fieldDirection = fieldDirection % 360;
            angle = Math.toRadians( fieldDirection );
        }
        
        // Vector
        return AbstractVector2D.Double.parseAngleAndMagnitude( magnitude, angle );
    }
}