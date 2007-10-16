/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.model;

import java.awt.geom.Point2D;
import java.util.Collection;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.DynamicListenerController;
import edu.colorado.phet.common.phetcommon.util.DynamicListenerControllerFactory;

/**
 * BModelElement is a model element that uses a DynamicListenerController to notify listeners.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BModelElement implements ModelElement {
    
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
    
    private BModelElementListener _controller;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BModelElement( Point2D position, double orientation ) {
        super();
        _position = new Point2D.Double( position.getX(), position.getY() );
        _orientation = orientation;
        _controller = (BModelElementListener) DynamicListenerControllerFactory.newController( BModelElementListener.class );
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
        	Point2D oldPosition = getPosition();
            _position.setLocation( x, y );
            _controller.positionChanged(oldPosition, getPosition() ); 
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
        	double oldOrientation = getOrientation();
            _orientation = orientation;
            _controller.orientationChanged(oldOrientation, getOrientation() );
        }
    }
    
    public double getOrientation() {
        return _orientation;
    }
    
    //----------------------------------------------------------------------------
    // Listener
    //----------------------------------------------------------------------------
    
    public interface BModelElementListener {
    	public void positionChanged( Point2D oldPosition, Point2D newPosition );
    	public void orientationChanged( double oldOrientation, double newOrientation );
    }
    
    public static class BModelElementAdapter implements BModelElementListener {

		public void orientationChanged(double oldOrientation, double newOrientation) {}

		public void positionChanged(Point2D oldPosition, Point2D newPosition) {}
    }
    
    public void addListener( BModelElementListener listener ) {
    	((DynamicListenerController)_controller).addListener( listener );
    	Point2D position = getPosition();
    	_controller.positionChanged( position, position );
    	double orientation = getOrientation();
    	_controller.orientationChanged( orientation, orientation );
    }
    
    public void removeListener( BModelElementListener listener ) {
    	((DynamicListenerController)_controller).removeListener( listener );
    }
    
    public Collection getAllListeners() {
    	return ((DynamicListenerController)_controller).getAllListeners();
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
