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

import edu.colorado.phet.common.util.SimpleObservable;


/**
 * AbstractMagnet is the abstract base class for all magnets.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractMagnet extends SimpleObservable implements IMagnet {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private double _strength;
    private Point2D _location;
    private double _direction;
    private Dimension _size;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor
     */
    public AbstractMagnet() {
        _strength = 1.0;
        _location = new Point2D.Double( 0, 0 );
        _direction = 0.0;
        _size = new Dimension( 250, 50 );
    }
    
    //----------------------------------------------------------------------------
    // IMagnet implementation
    //----------------------------------------------------------------------------

    /** 
     * @see edu.colorado.phet.faraday.model.IMagnet#setStrength(double)
     */
    public void setStrength( double strength ) {
        if ( strength <= 0 ) {
            throw new IllegalArgumentException( "strength must be > 0 : " + strength );
        }
        _strength = strength;
        notifyObservers();
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#getStrength()
     */
    public double getStrength() {
        return _strength;
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#setLocation(java.awt.geom.Point2D)
     */
    public void setLocation( final Point2D location ) {
        setLocation( location.getX(), location.getY() );
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#setLocation(double, double)
     */
    public void setLocation( double x, double y ) {
       _location.setLocation( x, y );
       notifyObservers();
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#getLocation()
     */
    public Point2D getLocation() {
        return new Point2D.Double( _location.getX(), _location.getY() );
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#getX()
     */
    public double getX() {
        return _location.getX();
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#getY()
     */
    public double getY() {
        return _location.getY();
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#setDirection(double)
     */
    public void setDirection( double direction ) {
        _direction = direction;
        notifyObservers();
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#getDirection()
     */
    public double getDirection() {
        return _direction;
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#setSize(java.awt.Dimension)
     */
    public void setSize( Dimension size ) {
        _size = new Dimension( size );
        notifyObservers();
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#setSize(double, double)
     */
    public void setSize( double width, double height ) {
        _size = new Dimension();
        _size.setSize( width, height );
        notifyObservers();
    }
    
    /** 
     * @see edu.colorado.phet.faraday.model.IMagnet#getSize()
     */
    public Dimension getSize() {
        return new Dimension( _size );
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#getWidth()
     */
    public double getWidth() {
        return _size.width;
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#getHeight()
     */
    public double getHeight() {
        return _size.height;
    }
}
