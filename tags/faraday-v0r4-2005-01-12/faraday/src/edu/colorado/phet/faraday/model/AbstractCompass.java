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

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;


/**
 * AbstractCompass is the base class for all compass models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractCompass extends SimpleObservable implements ModelElement, SimpleObserver, ICompass {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private IMagnet _magnetModel;
    private Point2D _location;
    private double _direction;
    private AbstractVector2D _fieldStrength;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param magnetModel the magnet that the compass is observing
     */
    public AbstractCompass( IMagnet magnetModel ) {
        super();
        
        _magnetModel = magnetModel;
        _location = new Point2D.Double( 0.0, 0.0 );
        _fieldStrength = _magnetModel.getStrength( _location );
        _direction = Math.toDegrees( _fieldStrength.getAngle() );   
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * @see edu.colorado.phet.faraday.model.ICompass#setLocation(Point2D)
     */
    public void setLocation( Point2D p ) {
        setLocation( p.getX(), p.getY() );
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.ICompass#setLocation(double,double)
     */
    public void setLocation( double x, double y ) {
        _location.setLocation( x, y );
        update();
        notifyObservers();
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.ICompass#getLocation()
     */
    public Point2D getLocation() {
        return new Point2D.Double( _location.getX(), _location.getY() );
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.ICompass#getX()
     */
    public double getX() {
        return _location.getX();
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.ICompass#getY()
     */
    public double getY() {
        return _location.getY();
    }
    
    /**
     * Sets the direction of the compass needle, in degrees.
     * 
     * @param direction the direction, in degrees
     * @see edu.colorado.phet.faraday.model.ICompass#getDirection()
     */
    protected void setDirection( double direction ) {
        _direction = direction;
        notifyObservers();
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.ICompass#getDirection()
     */
    public double getDirection() {
        return _direction;
    }
    
    /**
     * @see edu.colorado.phet.faraday.model.ICompass#getFieldStrength()
     */
    public AbstractVector2D getFieldStrength() {
        return _fieldStrength; // AbstractVector2D is immutable
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * @see edu.colorado.phet.common.model.ModelElement#stepInTime(double)
     */
    public abstract void stepInTime( double dt );
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        _fieldStrength = _magnetModel.getStrength( _location );
        notifyObservers();
    }
    
    //----------------------------------------------------------------------------
    // Object overrides
    //----------------------------------------------------------------------------
    
    /**
     * Provides a string representation of this object.
     * Do not write code that relies on the format of this string.
     * <p>
     * Since this class is abstract, it is the subclass' responsibility
     * to include this information in its toString method.
     * 
     * @return string representation
     */
    public String toString() {
        return
            "location=" + _location + 
            " direction=" + _direction +
            " B=" + _fieldStrength.getMagnitude() +
            " Bx=" + _fieldStrength.getX() +
            " By=" + _fieldStrength.getY() +
            " B0=" + Math.toDegrees(_fieldStrength.getAngle());
    }
}