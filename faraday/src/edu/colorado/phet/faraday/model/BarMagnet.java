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

import edu.colorado.phet.common.util.SimpleObservable;


/**
 * BarMagnet
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnet extends SimpleObservable implements IMagnet {
    
    private double _strength;
    private Point2D _location;
    private double _direction;
    
    public BarMagnet() {
        _strength = 1;
        _location = new Point2D.Double( 0, 0 );
        _direction = 0;
    }
    
    public BarMagnet( double strength ) {
        this();
        setStrength( strength );
    }
    
    public BarMagnet( double strength, Point2D location, double direction ) {
        this();
        setStrength( strength );
        setLocation( location );
        setDirection( direction );
    }
    
    public void setStrength( double strength ) {
        if ( strength <= 0 ) {
            throw new IllegalArgumentException( "strength must be > 0 : " + strength );
        }
        _strength = strength;
        notifyObservers();
    }
    
    public double getStrength() {
        return _strength;
    }
    
    // XXX fake
    public double getStrength( Point2D p ) {
        double dx = p.getX() - _location.getX();
        double dy = p.getY() - _location.getY();
        double distance = Math.sqrt( Math.pow(dx,2) + Math.pow(dy,2) );
        double strength = 0;
        if ( distance != 0 ) {
            strength = _strength * ( 1/distance);
        }
        return strength;
    }
    
    public void setLocation( Point2D location ) {
        setLocation( location.getX(), location.getY() );
    }
    
    public void setLocation( double x, double y ) {
       _location.setLocation( x, y );
       notifyObservers();
    }
    
    public Point2D getLocation() {
        return new Point2D.Double( _location.getX(), _location.getY() );
    }
    
    public double getX() {
        return _location.getX();
    }
    
    public double getY() {
        return _location.getY();
    }
    
    public void setDirection( double direction ) {
        _direction = direction;
        notifyObservers();
    }
    
    public double getDirection() {
        return _direction;
    }
    
    // XXX fake
    public double getDirection( Point2D p ) {
        
        //  Here are the names for the regions that surround the magnet:
        //
        //    R1     |      R2        |   R3
        //   --------|----------------|----------
        //    R4     |     magnet     |   R5
        //   --------|----------------|----------
        //    R6     |      R7        |   R8
        
        double direction = 0;
        double w = 250.0; // XXX
        double h = 50.0; // XXX
        
        if ( p.getY() >= _location.getY() - h/2 && p.getY() <= _location.getY() + h/2  ) {
            // Point is in R4 or R5, use magnet's direction.
            direction = _direction;
        }
        else if ( p.getX() >= _location.getX() - w/2 && p.getX() <= _location.getX() + w/2 ) {
            // Point is in R2 or R7, use the magnet's opposite direction.
            direction = ( _direction + 180 );
        }
        else if ( p.getX() < _location.getX() - w/2 )  {
            // Point is in R1 or R6, determine angle.
            double opposite = _location.getY() - p.getY();
            double adjacent = ( _location.getX() - w/2 ) - p.getX();
            double theta = Math.toDegrees( Math.atan( opposite / adjacent ) );
            direction = _direction + theta;   
        }
        else {
            // Point is in R3 or R8, determine angle.
            double opposite = p.getY() - _location.getY();
            double adjacent = p.getX() - ( _location.getX() + w/2 );
            double theta = Math.toDegrees( Math.atan( opposite / adjacent ) );
            direction = _direction + theta;
        }
        return ( direction % 360 );
    }
}
