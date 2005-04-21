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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.faraday.util.Vector2D;


/**
 * CoilMagnet is the model of a coil magnet.
 * The shape of the model is a circle, and the calculation of the magnetic field
 * at some point of interest varies depending on whether the point is inside or
 * outside the circle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class CoilMagnet extends AbstractMagnet {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
 
    // Arbitrary positive "fudge factor".
    private static final double FUDGE_FACTOR = 1.0;
    
    // Magnetic field strength drops off by this power.
    private static final double DEFAULT_DISTANCE_EXPONENT = 3.0;
    
    // Number of pixels per 1 unit of distance.
    private static final double PIXELS_PER_DISTANCE = 1.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AffineTransform _transform;
    private Point2D _normalizedPoint;
    private Ellipse2D _modelShape;
    
    // Debugging 
    private double _maxStrengthOutside;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public CoilMagnet() {
        super();
        _transform = new AffineTransform();
        _normalizedPoint = new Point2D.Double();
        _modelShape = new Ellipse2D.Double();
        _maxStrengthOutside = 0.0;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the shape that defines this model, in this case a circle.
     * 
     * @return the shape
     */
    public Shape getShape() {
        return _modelShape;
    }
    
    //----------------------------------------------------------------------------
    // FaradayObservable overrides
    //----------------------------------------------------------------------------
    
    /*
     * Respond to changes that result from calling superclass methods,
     * in this case changes to the magnet's size via super.setSize.
     */
    protected void notifySelf() {
        double width = PIXELS_PER_DISTANCE * getWidth();
        double height = PIXELS_PER_DISTANCE * getHeight() ;
        _modelShape.setFrame( -width/2, -height/2, width, height );
    }
    
    //----------------------------------------------------------------------------
    // AbstractMagnet implementation
    //----------------------------------------------------------------------------

    /*
     * Gets the B-field vector at a specified point.
     */
    public Vector2D getStrength( Point2D p, Vector2D outputVector /* output */ ) {
        return getStrength( p, outputVector, DEFAULT_DISTANCE_EXPONENT );
    }  

    /*
     * Gets the B-field vector at a specified point.
     * The caller may specify an exponent that determines how the field strength 
     * decreases with distance from the magnet.
     * <p>
     * Algorithm courtesy of Michael Dubson (dubson@spot.colorado.edu).
     * <p>
     * Terminology:
     * <ul>
     * <li>axes oriented with +X right, +Y up
     * <li>origin is the center of the coil, at (0,0)
     * <li>(x,y) is the point of interest where we are measuring the magnetic field
     * <li>C = a fudge factor, set so that the lightbulb will light
     * <li>m = magnetic moment = C * #loops * current in the coil
     * <li>R = radius of the coil
     * <li>r = distance from the origin to (x,y)
     * <li>theta = angle between the X axis and (x,y)
     * <li>Bx = X component of the B field
     * <li>By = Y component of the B field
     * <li>e is the exponent that specifies how the field decreases with distance (3 in reality)
     * </ul>
     * <p>
     * Inside the coil (r <= R) :
     * <ul>
     * <li>Bx = ( 2 * m ) / R^e = magnet strength
     * <li>By = 0
     * </ul>
     * <p>
     * Outside the coil (r > R) :
     * <ul>
     * <li>Bx = ( m / r^e ) * ( ( 3 * cos(theta) * cos(theta) ) - 1 )
     * <li>By = ( m / r^e ) * ( 3 * cos(theta) * sin(theta) )
     * </ul>
     * <br>where:
     * <ul>
     * <li>r = sqrt( x^2 + y^2 )
     * <li>cos(theta) = x / r
     * <li>sin(theta) = y / r
     * </ul>
     * 
     * @param p
     * @param outputVector
     * @param distanceExponent
     */
    public Vector2D getStrength( Point2D p, Vector2D outputVector, double distanceExponent ) {
        
        assert( p != null );
        assert( getWidth() == getHeight() );
 
        // Result will be copied into the output parameter, if it was provided.
        Vector2D fieldVector = outputVector;
        if ( fieldVector == null ) {
            fieldVector = new Vector2D();
        }
        fieldVector.zero();
        
        // All of our calculations are based a magnet located at the origin,
        // with the north pole pointing down the X-axis.
        // The point we received is based on the magnet's actual location and origin.
        // So transform the point accordingly, adjusting for location and rotation of the magnet.
        _transform.setToIdentity();       
        _transform.translate( -getX(), -getY() );
        _transform.rotate( -getDirection(), getX(), getY() );
        _transform.transform( p, _normalizedPoint /* output */ );
        
        // Determine whether we're inside or outside the circle that defines the coil.
        double radius = getWidth() / 2;
        double distance = _normalizedPoint.distance( 0, 0 ) / PIXELS_PER_DISTANCE;
        if ( Math.abs( distance ) <= radius ) {
            getStrengthInside( _normalizedPoint, fieldVector );
        }
        else {
            getStrengthOutside( _normalizedPoint, fieldVector, distanceExponent );
        }
        
        // Adjust the field vector to match the magnet's direction.
        fieldVector.rotate( getDirection() );
        
        // Clamp magnitude to magnet strength.
        double magnetStrength = super.getStrength();
        double magnitude = fieldVector.getMagnitude();
        if ( magnitude > magnetStrength ) {
            fieldVector.setMagnitude( magnetStrength );
            //System.out.println( "CoilMagnet.getStrength - magnitude exceeds magnet strength by " + (magnitude - magnetStrength ) ); // DEBUG
        }
        
        return fieldVector;
    }

    /*
     * Gets the B-field vector for points inside the coil.
     * 
     * @param p
     * @param outputVector
     */
    private void getStrengthInside( Point2D p, Vector2D outputVector /* output */ ) {
        assert( p != null );
        assert( outputVector != null );
        outputVector.setMagnitudeAngle( getStrength(), 0 );
    }
    
    /*
     * Gets the B-field vector for points outside the coil.
     * 
     * @param p
     * @param outputVector
     * @param distanceExponent
     */
    private void getStrengthOutside( Point2D p, Vector2D outputVector /* output */, double distanceExponent ) {
        assert ( p != null );
        assert ( outputVector != null );
        assert ( getWidth() == getHeight() );

        // Elemental terms
        double x = p.getX();
        double y = p.getY();
        double r = Math.sqrt( ( x * x ) + ( y * y ) );
        double R = getWidth() / 2;

        /*
         * Inside the magnet, Bx = magnet strength = (2 * m) / (R^3).
         * Rewriting this gives us m = (magnet strength) * (R^3) / 2.
         */
        double m = getStrength() * Math.pow( R, distanceExponent ) / 2;

        // Recurring terms
        double C1 = m / Math.pow( r, distanceExponent );
        double cosTheta = x / r;
        double sinTheta = y / r;

        // B-field component vectors
        double Bx = C1 * ( ( 3 * cosTheta * cosTheta ) - 1 );
        double By = C1 * ( 3 * cosTheta * sinTheta );

        // B-field vector
        outputVector.setXY( Bx, By );

        // Use this to calibrate.
        if ( outputVector.getMagnitude() > _maxStrengthOutside ) {
            _maxStrengthOutside = outputVector.getMagnitude();
            // System.out.println( "CoilMagnet: maxStrengthOutside=" + _maxStrengthOutside ); // DEBUG
        }
    }
}
