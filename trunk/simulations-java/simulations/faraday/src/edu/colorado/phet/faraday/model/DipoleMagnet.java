/* Copyright 2005-2010, University of Colorado */

package edu.colorado.phet.faraday.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.faraday.util.Vector2D;


/**
 * DipoleMagnet is an abstract magnet model based on a pair of dipoles.
 * The magnetic field is modeled as a pair of dipoles.
 * One dipole is located at each pole of the magnet.
 * This model is applicable to magnets that are cylindrical in shape.
 * <p>
 * See getStrength for details on the algorithm used.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class DipoleMagnet extends AbstractMagnet {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
 
    /*
     * Arbitrary "fudge factor" that controls the B-field transitions between
     * inside and outside the magnet.  Adjust this using the provided developer control.
     */
    private static final double DEFAULT_INSIDE_OUTSIDE_TRANSITION_FACTOR = 321;
    
    // Magnetic field strength drops off by this power.
    private static final double DEFAULT_DISTANCE_EXPONENT = 3.0;
    
    // Number of pixels per 1 unit of distance.
    private static final double PIXELS_PER_DISTANCE = 1.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AffineTransform _transform;
    private Point2D _northPoint, _southPoint;
    private Point2D _normalizedPoint;
    private Vector2D _northVector, _southVector;
    private Rectangle2D _modelShape;
    private double _insideOutsideTransitionFactor;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DipoleMagnet() {
        super();
        _transform = new AffineTransform();
        _northPoint = new Point2D.Double();
        _southPoint = new Point2D.Double();
        _normalizedPoint = new Point2D.Double();
        _northVector = new Vector2D();
        _southVector = new Vector2D();
        _modelShape = new Rectangle2D.Double();
        _insideOutsideTransitionFactor = DEFAULT_INSIDE_OUTSIDE_TRANSITION_FACTOR;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the factor that controls the B-field transitions between inside and outside the magnet.
     * Intended for use by developer control, for calibration only.
     * 
     * @param insideOutsideTransitionFactor
     */
    public void setInsideOutsideTransitionFactor( double insideOutsideTransitionFactor ) {
        if ( insideOutsideTransitionFactor != _insideOutsideTransitionFactor ) {
            _insideOutsideTransitionFactor = insideOutsideTransitionFactor;
        }
    }
    
    /**
     * Gets the factor that controls the B-field transitions between inside and outside the magnet.
     * 
     * @param insideOutsideTransitionFactor
     */
    public double getInsideOutsideTransitionFactor() {
        return _insideOutsideTransitionFactor;
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
    
    /**
     * Gets the B-field vector at a specified point in the 2D plane of the magnet.
     */
    public Vector2D getBField( Point2D p, Vector2D outputVector /* output */ ) {
        return getStrength( p, outputVector, DEFAULT_DISTANCE_EXPONENT );
    }   
    
    /**
     * Gets the B-field vector at a specified point in the 2D plane of the magnet.
     */
    public Vector2D getStrength( Point2D p, Vector2D outputVector /* output */, double distanceExponent ) {
        return getStrength( p, outputVector, distanceExponent, true /* inPlaneOfMagnet */ );
    }
    
    /**
     * Gets the B-field vector at a specified point slightly outside the 2D plane of the magnet.
     * This is used solely by the B-field grid (as implemented by CompassGridGraphic), so that
     * we don't see the compass needles suddenly flip at the top and bottom edges of the magnet
     * when dragging the magnet vertically.  See Unfuddle ticket #424.
     */
    public Vector2D getStrengthOutsidePlane( final Point2D p, Vector2D outputVector, double distanceExponent ) {
        return getStrength( p, outputVector, distanceExponent, false /* inPlaneOfMagnet */ );
    }
    
    /*
     * Gets the B-field vector at a specified point.
     * The point may be either in the 2D plane of the magnet, or slightly outside the 2D plane.
     * The caller may specify an exponent that determines how the field strength 
     * decreases with distance from the magnet.
     */
    private Vector2D getStrength( Point2D p, Vector2D outputVector /* output */, double distanceExponent, boolean inPlaneOfMagnet ) {

        assert( p != null );
        assert( getWidth() > getHeight() );
 
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
        
        // Choose the appropriate algorithm based on whether the point is 
        // inside or outside the magnet, and whether we are in the 2D plane of the magnet.
        if ( inPlaneOfMagnet && isInside( _normalizedPoint ) )  {
            getStrengthInside( _normalizedPoint, fieldVector /* output */ );
        }
        else {
            getStrengthOutside( _normalizedPoint, fieldVector /* output */, distanceExponent );
        }
        
        // Adjust the field vector to match the magnet's direction.
        fieldVector.rotate( getDirection() );

        //TODO Why is this necessary? If I remove it, the max strength is exceeded often, sometime by a large amount.
        // Clamp magnitude to magnet strength.
        double magnetStrength = getStrength();
        double magnitude = fieldVector.getMagnitude();
        if ( magnitude > magnetStrength ) {
            fieldVector.setMagnitude( magnetStrength );
//            System.out.println( "DipoleMagnet.getStrength - magnitude exceeds magnet strength by " + (magnitude - magnetStrength ) ); // DEBUG
        }
        
        return fieldVector;
    }
    
    /*
     * Is the specified point inside the magnet?
     */
    private boolean isInside( Point2D p ) {
        return _modelShape.contains( p );
    }
    
    /*
     * Gets the magnetic field strength at a point inside the magnet.
     * Algorithm courtesy of Mike Dubson (dubson@spot.colorado.edu).
     * <p>
     * B-field varies as a gradual curve inside the magnet.
     * Full magnet strength is at the magnet center.
     * B-field drops off more rapidly the further you get from the center,
     * and is half magnet strength at the magnet ends.
     * 
     * @param p the point, relative to the magnet's origin
     * @param outputVector write the result into this vector
     */
    private void getStrengthInside( Point2D p, Vector2D outputVector /* output */ ) {
        double strength = getStrength();
        double w = getWidth();
        double h = getHeight();
        double x = p.getX();
        double C = ( strength / 2 ) * ( Math.sqrt( ( w * w ) + ( h * h ) ) / w );
        double denominator1 = Math.sqrt( ( ( x - w / 2 ) * ( x - w / 2 ) ) + ( h / 2 * h / 2 ) );
        double denomimator2 = Math.sqrt( ( ( x + w / 2 ) * ( x + w / 2 ) ) + ( h / 2 * h / 2 ) );
        double Bx = C * ( ( ( w / 2 - x ) / denominator1 ) + ( ( w / 2 + x ) / denomimator2 ) );
        double By = 0;
        outputVector.setMagnitudeAngle( Bx, By );
    }
    
    /*
     * Gets the magnetic field strength at a point outside the magnet.
     * Algorithm courtesy of Mike Dubson (dubson@spot.colorado.edu).
     * <p>
     * The magnitude is guaranteed to be >=0 and <= the magnet strength.
     * This algorithm makes the following assumptions:
     * <ul>
     * <li>the magnet's location is (0,0)
     * <li>the magnet's direction is 0.0 (north pole pointing down the positive X axis)
     * <li>the magnet's physical center is at (0,0)
     * <li>the magnet's width > height
     * <li>the point is guaranteed to be outside the magnet
     * <li>the point has been transformed so that it is relative to above magnet assumptions
     * </ul>
     * <p>
     * Terminology:
     * <ul>
     * <li>axes oriented with +X right, +Y up
     * <li>origin is the center of the coil, at (0,0)
     * <li>(x,y) is the point of interest where we are measuring the magnetic field
     * <li>h is the height of the magnet
     * <li>w is the width of the magnet
     * <li>L is the distance between the dipoles
     * <li>C is a fudge factor
     * <li>rN is the distance from the north dipole to (x,y)
     * <li>rS is the distance from the south dipole to (x,y)
     * <li>B is the field vector at (x,y) due to the entire magnet
     * <li>BN is the field vector at (x,y) due to the north dipole
     * <li>BS is the field vector at (x,y) due to the south dipole
     * <li>e is the exponent that specifies how the field decreases with distance (3 in reality)
     * </ul>
     * <p>
     * The dipole locations are:
     * <ul>
     * <li>north: w/2 - h/2
     * <li>south: -w/2 + h/2
     * </ul>
     * The algorithm for outside the magnet:
     * <ul>
     * <li>BN = ( +C / rN^e ) [ ( x - L/2 ), y ]
     * <li>BS = ( -C / rS^e ) [ ( x + L/2 ), y ]
     * <li>B = BN + BS
     * 
     * @param p the point, relative to the magnet's origin
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
        if ( rN == 0 ) {
            rN = 0.001; // must be non-zero or later calculations will have divide-by-zero problem
        }
        double rS = _southPoint.distance( p ) / PIXELS_PER_DISTANCE; // south dipole to point
        if ( rS == 0 ) {
            rS = 0.001; // must be non-zero or later calculations will have divide-by-zero problem
        }
        double L = _southPoint.distance( _northPoint ); // dipole to dipole
        
        // Fudge factor
        double C = _insideOutsideTransitionFactor * magnetStrength;
        
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
