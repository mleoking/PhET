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

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObservable;


/**
 * BarMagnet
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnet extends SimpleObservable implements IMagnet {

    private static final double MIN_STRENGTH = 1;
    private static final double MAX_STRENGTH = 100;
    
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
        _strength = MathUtil.clamp( MIN_STRENGTH, strength, MAX_STRENGTH );
        notifyObservers();
    }
    
    public double getStrength() {
        return _strength;
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
}
