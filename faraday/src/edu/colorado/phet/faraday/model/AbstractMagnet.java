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

import edu.colorado.phet.common.math.AbstractVector2D;


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

    private double _strength;
    private Dimension _size;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor
     */
    public AbstractMagnet() {
        super();
        _strength = 1.0;
        _size = new Dimension( 250, 50 );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /** 
     * Sets the magnitude of the magnet's strength, in Gauss.
     * 
     * @param strength the strength
     */
    public void setStrength( double strength ) {
        if ( strength <= 0 ) {
            throw new IllegalArgumentException( "strength must be > 0 : " + strength );
        }
        _strength = strength;
        notifyObservers();
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
     * @return the strength vector
     */
    public abstract AbstractVector2D getStrength( final Point2D p );
    
    /**
     * Sets the physical size of the magnet.
     * 
     * @param size the size
     */
    public void setSize( Dimension size ) {
        _size = new Dimension( size );
        notifyObservers();
    }
    
    /**
     * Sets the physical size of the magnet.
     * 
     * @param width the width
     * @param height the height
     */
    public void setSize( double width, double height ) {
        _size = new Dimension();
        _size.setSize( width, height );
        notifyObservers();
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
        return _size.width;
    }
    
    /**
     * Gets the physical height of the magnet.
     * 
     * @return the height
     */
    public double getHeight() {
        return _size.height;
    }
}
