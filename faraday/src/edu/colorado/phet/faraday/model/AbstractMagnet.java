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

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.faraday.util.Vector2D;


/**
 * AbstractMagnet is the abstract base class for all magnets.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractMagnet extends SpacialObservable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Dimension _size;
    private double _strength;
    private double _maxStrength;
    private double _minStrength;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor
     */
    public AbstractMagnet() {
        super();
        _size = new Dimension( 250, 50 );
        _strength = 1.0;
        _minStrength = 0.0;  // couldn't be any weaker
        _maxStrength = Double.POSITIVE_INFINITY;  // couldn't be any stronger
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /** 
     * Flips the magnet's polarity by rotating it 180 degrees.
     */
    public void flipPolarity() {
        setDirection( getDirection() + Math.PI );
    }
    
    /**
     * Sets the maximum magnet strength.
     * This value is used in rescaling of field strength.
     * 
     * @see edu.colorado.phet.faraday.model.AbstractMagnet#rescale(double)
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
            updateSelf();
            notifyObservers();
        }
    }
    
    /**
     * Gets the maximum magnet strength.
     * This value is used in rescaling of field strength.
     * 
     * @see edu.colorado.phet.faraday.model.AbstractMagnet#rescale(double)
     * @return the maximumum strength, in Gauss
     */
    public double getMaxStrength() {
        return _maxStrength;
    }
    
    /**
     * Sets the minimum magnet strength.
     * This value is used in rescaling of field strength.
     * 
     * @see edu.colorado.phet.faraday.model.AbstractMagnet#rescale(double)
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
            updateSelf();
            notifyObservers();
        }
    }
    
    /**
     * Gets the minimum magnet strength.
     * This value is used in rescaling of field strength.
     * 
     * @see edu.colorado.phet.faraday.model.AbstractMagnet#rescale(double)
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
            updateSelf();
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
     * Gets the strength vector of the magnetic field at a point in 2D space.
     * 
     * @param p the point
     * @param outputVector strength is written here if provided, may be null
     * @return the strength vector, strengthDst if it was provided
     */
    public abstract Vector2D getStrength( final Point2D p, Vector2D outputVector );
    
    /**
     * Gets the strength vector of the magnetic field at a point in 2D space,
     * using a specified exponent for computing how the field strength decreases
     * with the distance.
     * 
     * @param p the point
     * @param outputVector strength is written here if provided, may be null
     * @param distanceExponent the distance exponent
     * @return the strength vector, strengthDst if it was provided
     */
    public abstract Vector2D getStrength( final Point2D p, Vector2D outputVector, double distanceExponent );
    
    /**
     * Gets the strength vector of the magnetic field at a point in 2D space.
     * 
     * @param p the point
     * @return the strength vector
     */
    public Vector2D getStrength( final Point2D p ) {
        return getStrength( p, null );
    }
    
    /**
     * Gets the strength vector of the magnetic field at a point in 2D space,
     * using a specified exponent for computing how the field strength decreases
     * with the distance.
     * 
     * @param p the point
     * @param distanceExponent the distance exponent
     * @return the strength vector
     */
    public Vector2D getStrength( final Point2D p, double distanceExponent ) {
        return getStrength( p, null, distanceExponent );
    }
    
    /**
     * Sets the physical size of the magnet.
     * 
     * @param size the size
     * @throws IllegalArgumentException if both dimensions are not > 0
     */
    public void setSize( Dimension size ) {
        assert( size != null );
        setSize( size.getWidth(), size.getHeight() );
    }
    
    /**
     * Sets the physical size of the magnet.
     * 
     * @param width the width
     * @param height the height
     * @throws IllegalArgumentException if width or height is not > 0, or if height >= width
     */
    public void setSize( double width, double height ) {
        if ( ! (width > 0 && height > 0) ) {
            throw new IllegalArgumentException( "dimensions must be > 0" );
        }
        if ( width <= height ) {
            throw new IllegalArgumentException( "width must be > height" );
        }
        if ( width != _size.getWidth() || height != _size.getHeight() ) {
            _size.setSize( width, height );
            updateSelf();
            notifyObservers();
        }
    }
    
    /** 
     * Gets the physical size of the magnet.
     * 
     * @return the size
     */
    public Dimension getSize() {
        return new Dimension( _size );
    }
    
    /**
     * Gets the physical width of the magnet.
     * 
     * @return the width
     */
    public double getWidth() {
        return _size.getWidth();
    }
    
    /**
     * Gets the physical height of the magnet.
     * 
     * @return the height
     */
    public double getHeight() {
        return _size.getHeight();
    }
}
