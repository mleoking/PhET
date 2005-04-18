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
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public CoilMagnet() {
        super();
        _transform = new AffineTransform();
        _normalizedPoint = new Point2D.Double();
        _modelShape = new Ellipse2D.Double();
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
    // AbstractMagnet implementation
    //----------------------------------------------------------------------------
    
    /*
     * Respond to changes that result from calling superclass methods,
     * in this case changes to the magnet's size via super.setSize.
     */
    protected void updateSelf() {
        double width = PIXELS_PER_DISTANCE * getWidth();
        double height = PIXELS_PER_DISTANCE * getHeight() ;
        _modelShape.setFrame( -width/2, -height/2, width, height );
    }
    
    
    /*
     * See AbstractMagnet.getStrength.
     */
    public Vector2D getStrength( Point2D p, Vector2D outputVector /* output */ ) {
        return getStrength( p, outputVector, DEFAULT_DISTANCE_EXPONENT );
    }  

    /*
     * @see edu.colorado.phet.faraday.model.AbstractMagnet#getStrength(java.awt.geom.Point2D, edu.colorado.phet.faraday.util.Vector2D, double)
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

    private void getStrengthInside( Point2D p, Vector2D outputVector /* output */ ) {
        assert( p != null );
        assert( outputVector != null );
        outputVector.setMagnitudeAngle( getStrength(), 0 );
    }
    
    private void getStrengthOutside( Point2D p, Vector2D outputVector /* output */, double distanceExponent ) {
        assert( p != null );
        assert( outputVector != null );
        assert( getWidth() == getHeight() );
        
//        // XXX - This is bogus, substitude correct model here.
//        {
//            outputVector.setXY( p.getX(), p.getY() );
//            double distance = outputVector.getMagnitude();
//            double strength = getStrength() / distance;
//            outputVector.setMagnitude( strength );
//        }
        
        double x = p.getX();
        double y = p.getY();
        double R = getWidth() / 2;
        double m = getStrength() * Math.pow( R, 3 ) / 2;
        double C1 = m / Math.pow( ( x * x ) + ( y * y ), 1.5 );
        double C2 = ( x * x ) + ( y * y );
        double Bx = C1 * ( ( ( 3 * x * x ) / C2 ) - 1 );
        double By = C1 * ( ( ( 3 * x * y ) / C2 ) - 1 );
        outputVector.setXY( Bx, By );
    }
}
