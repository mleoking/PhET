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
      
        double direction = 0.0;
        double w = 250.0; // XXX
        double h = 50.0; // XXX
        
        if ( p.getX() <= _location.getX() - w/2 ) {
            // Point is to left of magnet
            double opposite = _location.getY() - p.getY();
            double adjacent = ( _location.getX() - w/2 ) - p.getX();
            double theta = Math.toDegrees( Math.atan( opposite / adjacent ) );
            direction = _direction + theta;
        }
        else if ( p.getX() >= _location.getX() + w/2 ) {
            // Point is to right of magnet...
            double opposite = p.getY() - _location.getY();
            double adjacent = p.getX() - ( _location.getX() + w/2 );
            double theta = Math.toDegrees( Math.atan( opposite / adjacent ) );
            direction = _direction + theta;
        }
        else if ( p.getY() <= _location.getY() - h/2 ) {
            // Point is above the magnet...
            double multiplier = (_location.getX() + w/2 - p.getX()) / w;
            direction = _direction - 90 - ( multiplier * 180 );
        }
        else if ( p.getY() >= _location.getY() + h/2 ) {
            // Point is below the magnet...
            double multiplier = (_location.getX() + w/2 - p.getX()) / w;
            direction = _direction + 90 + ( multiplier * 180 );
        }
        else {
            // Point is inside the magnet...
            direction = _direction + 180;
        }

        return ( direction % 360 );
    }
}
