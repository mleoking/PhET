/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.model;

import java.awt.Dimension;
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
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Point2D _position;
    private double _orientation;
    private Dimension _size;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AModelElement( Point2D position, double orientation, Dimension size ) {
        super();
        _position = new Point2D.Double( position.getX(), position.getY() );
        _orientation = orientation;
        _size = new Dimension( size );
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public Dimension getSize() {
        return new Dimension( _size );
    }
    
    public void setPosition( Point2D position ) {
        if ( ! position.equals( _position ) ) {
            _position.setLocation( position.getX(), position.getY() );
            notifyObservers( PROPERTY_POSITION );   
        }
    }
    
    public Point2D getPosition() {
        return new Point2D.Double( _position.getX(), _position.getY() );
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
