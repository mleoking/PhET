/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.model;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * CModelElement is a model element that defines its own listener interface
 * and manages its own listener lists directly.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CModelElement implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Point2D _position;
    private double _orientation;
    private Dimension _size;
    private ArrayList _listeners;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public CModelElement( Point2D position, double orientation, Dimension size ) {
        super();
        _position = new Point2D.Double( position.getX(), position.getY() );
        _orientation = orientation;
        _size = new Dimension( size );
        _listeners = new ArrayList();
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public Dimension getSize() {
        return new Dimension( _size );
    }
    
    public void setPosition( Point2D position ) {
        if ( !position.equals( _position ) ) {
            _position.setLocation( position.getX(), position.getY() );
            notifyPositionChanged();
        }
    }
    
    public Point2D getPosition() {
        return new Point2D.Double( _position.getX(), _position.getY() );
    }
    
    public void setOrientation( double orientation ) {
        if ( orientation != _orientation) {
            _orientation = orientation;
            notifyOrientationChanged();
        }
    }
    
    public double getOrientation() {
        return _orientation;
    }
    
    //----------------------------------------------------------------------------
    // Listener
    //----------------------------------------------------------------------------
    
    public interface CModelElementListener {
    	public void positionChanged();
    	public void orientationChanged();
    }
    
    public static class CModelElementAdapter implements CModelElementListener {
		public void orientationChanged() {}
		public void positionChanged() {}
    }
    
    public void addListener( CModelElementListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeListener( CModelElementListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyPositionChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (CModelElementListener) _listeners.get( i ) ).positionChanged();
        }
    }
    
    private void notifyOrientationChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (CModelElementListener) _listeners.get( i ) ).orientationChanged();
        }
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
