/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.model;

import java.awt.geom.Point2D;
import java.util.Observable;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * AModelElement is a model element that uses Observer to notify listeners.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AModelElement extends Observable implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_POSITION = "position";
    public static final String PROPERTY_ORIENTATION = "orientation";
    
    private static final double WIDTH = 200;
    private static final double HEIGHT= 100;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Point2D _position;
    private double _orientation;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AModelElement( Point2D position, double orientation ) {
        super();
        _position = new Point2D.Double( position.getX(), position.getY() );
        _orientation = orientation;
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public double getWidth() {
        return WIDTH;
    }
    
    public double getHeight() {
        return HEIGHT;
    }
    
    public void setPosition( Point2D position ) {
        setPosition( position.getX(), position.getY() );
    }
    
    public void setPosition( double x, double y ) {
        if ( x != _position.getX() || y != _position.getY() ) {
            _position.setLocation( x, y );
            notifyObservers( PROPERTY_POSITION );   
        }
    }
    
    public Point2D getPosition() {
        return new Point2D.Double( _position.getX(), _position.getY() );
    }
    
    public Point2D getPositionReference() {
        return _position;
    }
    
    public double getX() {
        return _position.getX();
    }
    
    public double getY() {
        return _position.getY();
    }
    
    public void setOrientation( double orientation ) {
        if ( orientation != _orientation) {
            _orientation = orientation;
            notifyObservers( PROPERTY_ORIENTATION );
        }
    }
    
    public double getOrientation() {
        return _orientation;
    }
    
    //----------------------------------------------------------------------------
    // Observer overrides
    //----------------------------------------------------------------------------
    
    public void notifyObservers( Object arg ) {
        setChanged();
        super.notifyObservers( arg );
        clearChanged();
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the model each time the clock ticks.
     * 
     * @param dt
     */
    public void stepInTime( double dt ) {
        // do nothing
    }
}
