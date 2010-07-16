/* Copyright 2005-2010, University of Colorado */

package edu.colorado.phet.faraday.model;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.faraday.util.Vector2D;


/**
 * CoilMagnet is the model of a coil magnet.
 * The shape of the model is a circle, and the calculation of the magnetic field
 * at some point of interest varies depending on whether the point is inside or
 * outside the circle.
 * <p>
 * See getStrength for details on the algorithm used.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class CoilMagnet extends AbstractMagnet {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Rectangle2D _modelShape;
    private double _maxStrengthOutside; // for debugging
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public CoilMagnet() {
        super();
        _modelShape = new Rectangle2D.Double();
        _maxStrengthOutside = 0.0;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the shape that defines this physical boundaries of this magnet.
     * 
     * @return the shape
     */
    public Shape getShape() {
        return _modelShape;
    }
    
    /**
     * Is the specified point inside the magnet?
     * 
     * @param p
     * @return true or false
     */
    public boolean isInside( Point2D p ) {
        return _modelShape.contains( p );
    }
    
    //----------------------------------------------------------------------------
    // FaradayObservable overrides
    //----------------------------------------------------------------------------
    
    /*
     * Respond to changes that result from calling superclass methods,
     * in this case changes to the magnet's size via super.setSize.
     */
    protected void notifySelf() {
        double width = getWidth();
        double height = getHeight() ;
        _modelShape.setFrame( -width/2, -height/2, width, height );
    }
    
    //----------------------------------------------------------------------------
    // AbstractMagnet implementation
    //----------------------------------------------------------------------------

    /**
     * Gets the B-field vector at a point in the magnet's local 2D coordinate frame.
     * 
     * @param p
     * @param outputVector
     * @return outputVector
     */
    protected Vector2D getBFieldRelative( Point2D p, Vector2D outputVector ) {
        
        assert( p != null );
        assert( outputVector != null );
        assert( getWidth() == getHeight() );
 
        outputVector.zero();
        
        // Algorithm differs depending on whether we're inside or outside the shape that defines the coil.
        if ( isInside( p ) ) {
            getBFieldInside( p, outputVector );
        }
        else {
            getBFieldOutside( p, outputVector );
        }
        
        return outputVector;
    }

    /*
     * Gets the B-field vector for points inside the coil.
     * <p>
     * Inside the coil (r <= R) :
     * <ul>
     * <li>Bx = ( 2 * m ) / R^e = magnet strength
     * <li>By = 0
     * </ul>
     * 
     * @param p
     * @param outputVector
     */
    private void getBFieldInside( Point2D p, Vector2D outputVector /* output */ ) {
        assert( p != null );
        assert( outputVector != null );
        outputVector.setMagnitudeAngle( getStrength(), 0 );
    }
    
    /*
     * Gets the B-field vector for points outside the coil.
     * <p>
     * Algorithm courtesy of Mike Dubson (dubson@spot.colorado.edu).
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
     */
    private void getBFieldOutside( Point2D p, Vector2D outputVector /* output */ ) {
        assert ( p != null );
        assert ( outputVector != null );
        assert ( getWidth() == getHeight() );

        // Elemental terms
        double x = p.getX();
        double y = p.getY();
        double r = Math.sqrt( ( x * x ) + ( y * y ) );
        double R = getWidth() / 2;
        double distanceExponent = 3;

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
