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

import edu.colorado.phet.faraday.util.Vector2D;


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
    // Instance data
    //----------------------------------------------------------------------------
    
    private Point2D _point; // reusable point
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public HollywoodMagnet() {
        super();
        _point = new Point2D.Double();
    }

    //----------------------------------------------------------------------------
    // AbstractMagnet implementation
    //----------------------------------------------------------------------------
    
    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#getStrength(java.awt.geom.Point2D)
     */
    public Vector2D getStrength( Point2D p, Vector2D strengthDst ) {
        assert( p != null );
        
        /* 
         * Magnitude (in Gauss) drops off linearly as distance increases.
         */
        double fieldMagnitude = 0.0;
        {
            double strength = super.getStrength();
            getLocation( _point /* destination */ );
            double distance = p.distance( _point );

            double range = strength * DISTANCE_PER_GAUSS;
            if ( distance > range ) {
                fieldMagnitude = 0;
            }
            else {
                fieldMagnitude = strength - ( distance / DISTANCE_PER_GAUSS );
            }
        }
        
        /*
         * Angle (in radians) is based on whether the point is inside the magnet,
         * or in one of the four surrounding quadrants.  This should work for 
         * arbitrary orientations of the magnet.
         */
        double fieldDirection = Math.toRadians( 0.0 );
        if ( fieldMagnitude > 0 )
        {
            double fieldAngle = 0.0;

            // Magnet paramters
            double x = super.getX();
            double y = super.getY();
            double w = super.getWidth();
            double h = super.getHeight();
            double angle = Math.toDegrees( super.getDirection() );

            if( p.getX() <= x - w / 2 ) {
                // Point is to left of magnet.
                double opposite = y - p.getY();
                double adjacent = ( x - w / 2 ) - p.getX();
                double theta = Math.atan( opposite / adjacent );
                fieldAngle = angle + Math.toDegrees( theta );
            }
            else if( p.getX() >= x + w / 2 ) {
                // Point is to right of magnet.
                double opposite = p.getY() - y;
                double adjacent = p.getX() - ( x + w / 2 );
                double theta = Math.atan( opposite / adjacent );
                fieldAngle = angle + Math.toDegrees( theta );
            }
            else if( p.getY() <= y - h / 2 ) {
                // Point is above the magnet.
                double multiplier = ( x + w / 2 - p.getX() ) / w;
                fieldAngle = angle - 90 - ( multiplier * 180 );
            }
            else if( p.getY() >= y + h / 2 ) {
                // Point is below the magnet.
                double multiplier = ( x + w / 2 - p.getX() ) / w;
                fieldAngle = angle + 90 + ( multiplier * 180 );
            }
            else {
                // Point is inside the magnet.
                fieldAngle = angle + 180;
            }

            fieldAngle = fieldAngle % 360;
            fieldDirection = Math.toRadians( fieldAngle );
        }
        
        // Vector
        Vector2D v = new Vector2D();
        v.setMagnitudeAngle( fieldMagnitude, fieldDirection );
        return v;
    }
}