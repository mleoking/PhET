/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.faraday.util.Vector2D;


/**
 * DipoleMagnet is an abstract magnet model based on a pair of dipoles.
 * The magnetic field is modeled as a pair of dipoles.
 * One dipole is located at each pole of the magnet.
 * This model is applicable to magnets that are cylindrical in shape.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class DipoleMagnet extends AbstractMagnet {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
 
    /*
     * Arbitrary positive "fudge factor".
     * This should be adjusted so that transitions between inside and outside
     * the magnet don't result in abrupt changes in the magnetic field.
     */
    private static final double FUDGE_FACTOR = 700.0;
    
    // Magnetic field strength drops off by this power.
    private static final double DEFAULT_DISTANCE_EXPONENT = 3.0;
    
    // Number of pixels per 1 unit of distance.
    private static final double PIXELS_PER_DISTANCE = 1.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AffineTransform _transform;
    private Rectangle _bounds;
    private Point2D _northPoint, _southPoint;
    private Point2D _normalizedPoint;
    private Vector2D _northVector, _southVector;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DipoleMagnet() {
        super();
        _transform = new AffineTransform();
        _bounds = new Rectangle();
        _northPoint = new Point2D.Double();
        _southPoint = new Point2D.Double();
        _normalizedPoint = new Point2D.Double();
        _northVector = new Vector2D();
        _southVector = new Vector2D();
    }
    
    //----------------------------------------------------------------------------
    // AbstractMagnet implementation
    //----------------------------------------------------------------------------

    /*
     * See AbstractMagnet.getStrength.
     */
    public Vector2D getStrength( Point2D p, Vector2D outputVector /* output */ ) {
        return getStrength( p, outputVector, DEFAULT_DISTANCE_EXPONENT );
    }   
    
    /*
     * See AbstractMagnet.getStrength.
     * <p>
     * Algorithm courtesy of Michael Dubson (dubson@spot.colorado.edu).
     * <p>
     * Assumptions made by this algorithm:
     * <ul>
     * <li>the magnet's physical center is positioned at the magnet's location
     * <li>the magnet's width > height
     * </ul>
     */
    public Vector2D getStrength( Point2D p, Vector2D outputVector /* output */, double distanceExponent ) {

        assert( p != null );
        assert( getWidth() > getHeight() );
 
        // Result will be copied into the output parameter, if it was provided.
        Vector2D fieldVector = outputVector;
        if ( fieldVector == null ) {
            fieldVector = new Vector2D();
        }
        
        // All of our calculations are based a magnet located at the origin,
        // with the north pole pointing down the X-axis.
        // The point we received is based on the magnet's actual location and origin.
        // So transform the point accordingly, adjusting for location and rotation of the magnet.
        _transform.setToIdentity();       
        _transform.translate( -getX(), -getY() );
        _transform.rotate( -getDirection(), getX(), getY() );
        _transform.transform( p, _normalizedPoint /* output */ );
        
        // Bounds that define the "inside" of the magnet.
        _bounds.setRect( -(getWidth()/2), -(getHeight()/2), getWidth(), getHeight() );
              
        // Choose the appropriate algorithm based on
        // whether the point is inside or outside the magnet.
        if ( _bounds.contains( _normalizedPoint ) )  {
            getStrengthInside( _normalizedPoint, fieldVector /* output */ );
        }
        else
        {
            getStrengthOutside( _normalizedPoint, fieldVector /* output */, distanceExponent );
        }
        
        // Adjust the field vector to match the magnet's direction.
        fieldVector.rotate( getDirection() );

        // Clamp magnitude to magnet strength.
        double magnetStrength = super.getStrength();
        double magnitude = fieldVector.getMagnitude();
        if ( magnitude > magnetStrength ) {
            fieldVector.setMagnitude( magnetStrength );
            //System.out.println( "BarMagnet.getStrengthOutside - magnitude exceeds magnet strength by " + (magnitude - magnetStrength ) ); // DEBUG
        }
        
        return fieldVector;
    }
    
    /**
     * Gets the magnetic field strength at a point inside the magnet.
     * This is constant for all points inside the magnet.
     * <p>
     * This algorithm makes the following assumptions:
     * <ul>
     * <li>the point is guaranteed to be inside the magnet
     * <li>the magnet's direction is 0.0 (north pole pointing down the positive X axis)
     * </ul>
     * 
     * @param p the point
     * @param outputVector write the result into this vector
     */
    private void getStrengthInside( Point2D p, Vector2D outputVector /* output */ ) {
        assert( p != null );
        assert( outputVector != null );
        outputVector.setMagnitudeAngle( getStrength(), 0 );
    }
    
    /**
     * Gets the magnetic field strength at a point outside the magnet.
     * The magnitude is guaranteed to be >=0 and <= the magnet strength.
     * <p>
     * This algorithm makes the following assumptions:
     * <ul>
     * <li>the magnet's location is (0,0)
     * <li>the magnet's direction is 0.0 (north pole pointing down the positive X axis)
     * <li>the magnet's physical center is at (0,0)
     * <li>the magnet's width > height
     * <li>the point is guaranteed to be outside the magnet
     * <li>the point has been transformed so that it is relative to above magnet assumptions
     * </ul>
     * 
     * @param p the point
     * @param outputVector write the result into this vector
     * @param distanceExponent exponent that determines how the field strength decreases
     * with the distance from the magnet
     */
    private void getStrengthOutside( Point2D p, Vector2D outputVector /* output */, double distanceExponent ) {
        assert( p != null );
        assert( outputVector != null );
        assert( getWidth() > getHeight() );
        
        // Magnet strength.
        double magnetStrength = super.getStrength();
        
        // Dipole locations.
        _northPoint.setLocation( +getWidth()/2 - getHeight()/2, 0 ); // north dipole
        _southPoint.setLocation( -getWidth()/2 + getHeight()/2, 0 ); // south dipole
        
        // Distances.
        double rN = _northPoint.distance( p ) / PIXELS_PER_DISTANCE; // north dipole to point
        double rS = _southPoint.distance( p ) / PIXELS_PER_DISTANCE; // south dipole to point
        double L = _southPoint.distance( _northPoint ); // dipole to dipole
        
        // Fudge factor
        double C = FUDGE_FACTOR * magnetStrength;
        
        // North dipole field strength vector.
        double cN = +( C / Math.pow( rN, distanceExponent ) ); // constant multiplier
        double xN = cN * ( p.getX() - ( L / 2 ) ); // X component
        double yN = cN * p.getY(); // Y component
        _northVector.setXY( xN, yN ); // north dipole vector
        
        // South dipole field strength vector.
        double cS = -( C / Math.pow( rS, distanceExponent ) ); // constant multiplier
        double xS = cS * ( p.getX() + ( L / 2 ) ); // X component
        double yS = cS * p.getY(); // Y component
        _southVector.setXY( xS, yS ); // south dipole vector
        
        // Total field strength is the vector sum.
        _northVector.add( _southVector );
        
        // Copy the result into the output parameter.
        outputVector.copy( _northVector );
    }
}
