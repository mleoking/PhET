/* Copyright 2004-2010, University of Colorado */

package edu.colorado.phet.faraday.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.faraday.util.Vector2D;

/**
 * AbstractMagnet is the abstract base class for all magnets.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractMagnet extends FaradayObservable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private double _width, _height;
    private double _strength;
    private double _maxStrength;
    private double _minStrength;
    private AffineTransform _transform; // reusable transform
    private Point2D _relativePoint; // reusable point, in magnet's local coordinate frame
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor
     */
    public AbstractMagnet() {
        super();
        _width = 250;
        _height = 50;
        _strength = 1.0;
        _minStrength = 0.0;  // couldn't be any weaker
        _maxStrength = Double.POSITIVE_INFINITY;  // couldn't be any stronger
        _transform = new AffineTransform();
        _relativePoint = new Point2D.Double();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /** 
     * Flips the magnet's polarity by rotating it 180 degrees.
     */
    public void flipPolarity() {
        setDirection( ( getDirection() + Math.PI ) % ( 2 * Math.PI ) );
    }
    
    /**
     * Sets the maximum magnet strength.
     * This value is used in rescaling of field strength.
     * 
     * @param maxStrength the maximum strength, in Gauss
     */
    public void setMaxStrength( double maxStrength ) {
        if ( maxStrength != _maxStrength ) {
            _maxStrength = maxStrength;
            if ( _strength > _maxStrength ) {
                _strength = _maxStrength;
            }
            if ( _maxStrength < _minStrength ) {
                _minStrength = _maxStrength;
            }
            notifySelf();
            notifyObservers();
        }
    }
    
    /**
     * Gets the maximum magnet strength.
     * This value is used in rescaling of field strength.
     * 
     * @return the maximumum strength, in Gauss
     */
    public double getMaxStrength() {
        return _maxStrength;
    }
    
    /**
     * Sets the minimum magnet strength.
     * This value is used in rescaling of field strength.
     * 
     * @param minStrength the minimum strength, in Gauss
     */
    public void setMinStrength( double minStrength ) {
        if ( minStrength != _minStrength ) {
            _minStrength = minStrength;
            if ( _strength < _minStrength ) {
                _strength = _minStrength;
            }
            if ( _minStrength > _maxStrength ) {
                _maxStrength = _minStrength;
            }
            notifySelf();
            notifyObservers();
        }
    }
    
    /**
     * Gets the minimum magnet strength.
     * This value is used in rescaling of field strength.
     * 
     * @return the minimum strength, in Gauss
     */
    public double getMinStrength() {
        return _minStrength;
    }
    
    /** 
     * Sets the magnitude of the magnet's strength, in Gauss.
     * 
     * @param strength the strength
     * @throws IllegalArgumentException if strength is outside of the min/max range
     */
    public void setStrength( double strength ) {
        if ( strength < _minStrength || strength > _maxStrength ) {
            throw new IllegalArgumentException( "strength out of range: " + strength );
        }
        if ( strength != _strength ) {
            _strength = strength;
            notifySelf();
            notifyObservers();
        }
    }
    
    /**
     * Gets the magnitude of the magnet's strength, in Gauss.
     * 
     * @return the strength
     */
    public double getStrength() {
        return _strength;
    }
    
    /**
     * Gets the B-field vector at a point in the global 2D space.
     * This call allocates a Vector object. If memory or performance is
     * a concern, use the more efficient getStrength(Point2D, Vector2D).
     * 
     * @param p the point
     * @return the B-field vector
     */
    public Vector2D getBField( final Point2D p ) {
        return getBField( p, new Vector2D() );
    }
    
    /**
     * Gets the B-field vector at a point in the global 2D space.
     * 
     * @param p the point
     * @param outputVector B-field is written here if provided, may be null
     * @return the B-field vector, outputVector if it was provided
     */
    public Vector2D getBField( final Point2D p, Vector2D outputVector ) {
        assert( p != null );
        assert( outputVector != null );
        
        /* 
         * Our models are based a magnet located at the origin, with the north pole pointing down the positive x-axis.
         * The point we receive is in global 2D space.
         * So transform the point to the magnet's local coordinate system, adjusting for position and orientation.
         */
        _transform.setToIdentity();
        _transform.translate( -getX(), -getY() );
        _transform.rotate( -getDirection(), getX(), getY() );
        _transform.transform( p, _relativePoint /* output */ );
        
        // get strength in magnet's local coordinate frame
        getBFieldRelative( _relativePoint, outputVector );
        
        // Adjust the field vector to match the magnet's direction.
        outputVector.rotate( getDirection() );
        
        // Clamp magnitude to magnet strength.
        //TODO: why do we need to do this?
        double magnetStrength = getStrength();
        double magnitude = outputVector.getMagnitude();
        if ( magnitude > magnetStrength ) {
            outputVector.setMagnitude( magnetStrength );
            //System.out.println( "AbstractMagnet.getStrength - magnitude exceeds magnet strength by " + (magnitude - magnetStrength ) ); // DEBUG
        }
        
        return outputVector;
    }
    
    /**
     * Gets the B-field vector at a point in the magnet's local 2D coordinate frame.
     * That is, the point is relative to the magnet's origin.
     * In the magnet's local 2D coordinate frame, it is located at (0,0),
     * and its north pole is pointing down the positive x-axis.
     * 
     * @param p the point
     * @param outputVector B-field is written here if provided, may NOT be null
     * @return outputVector
     */
    protected abstract Vector2D getBFieldRelative( final Point2D p, Vector2D outputVector );
    
    /**
     * Sets the physical size of the magnet.
     * 
     * @param width the width
     * @param height the height
     * @throws IllegalArgumentException if width or height is <= 0
     */
    public void setSize( double width, double height ) {
        if ( width <= 0 || height <= 0) {
            throw new IllegalArgumentException( "dimensions must be > 0" );
        }
        if ( width != _width || height != _height ) {
            _width = width;
            _height = height;
            notifySelf();
            notifyObservers();
        }
    }
    
    /**
     * Gets the physical width of the magnet.
     * 
     * @return the width
     */
    public double getWidth() {
        return _width;
    }
    
    /**
     * Gets the physical height of the magnet.
     * 
     * @return the height
     */
    public double getHeight() {
        return _height;
    }
}
