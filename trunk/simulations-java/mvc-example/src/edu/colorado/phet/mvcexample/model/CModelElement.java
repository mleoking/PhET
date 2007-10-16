/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.model;

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
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double WIDTH = 200;
    private static final double HEIGHT= 100;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Point2D _position;
    private double _orientation;
    
    private ArrayList _listeners;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public CModelElement( Point2D position, double orientation ) {
        super();
        _position = new Point2D.Double( position.getX(), position.getY() );
        _orientation = orientation;
        _listeners = new ArrayList();
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
            notifyPositionChanged();
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
