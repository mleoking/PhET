/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Collection;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.DynamicListenerController;
import edu.colorado.phet.common.phetcommon.util.DynamicListenerControllerFactory;

/**
 * ExampleModelElement is an example model element.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModelElement implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Dimension _size;
    private Point2D _position;
    private double _orientation;
    
    private ExampleModelElementListener _controller;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleModelElement( Dimension size, Point2D position, double orientation ) {
        super();
        _size = new Dimension( size );
        _position = new Point2D.Double( position.getX(), position.getY() );
        _orientation = orientation;
        
        _controller = (ExampleModelElementListener) DynamicListenerControllerFactory.newController( ExampleModelElementListener.class );
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setSize( Dimension size ) {
        setSize( size.getWidth(), size.getHeight() );
    }
    
    public void setSize( double width, double height ) {
        if ( width != _size.getWidth() || height != _size.getHeight() ) {
        	Dimension oldSize = getSize();
            _size.setSize( width, height );
            _controller.sizeChanged( oldSize, getSize() );
        }  
    }
    
    public Dimension getSize() {
        return new Dimension( _size );
    }
    
    public Dimension getSizeReference() {
        return _size;
    }
    
    public double getWidth() {
        return _size.getWidth();
    }
    
    public double getHeight() {
        return _size.getHeight();
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
    
    public interface ExampleModelElementListener {
    	public void sizeChanged( Dimension oldSize, Dimension newSize );
    	public void positionChanged( Point2D oldPosition, Point2D newPosition );
    	public void orientationChanged( double oldOrientation, double newOrientation );
    }
    
    public static class ExampleModelElementAdapter implements ExampleModelElementListener {

		public void orientationChanged(double oldOrientation, double newOrientation) {}

		public void positionChanged(Point2D oldPosition, Point2D newPosition) {}

		public void sizeChanged(Dimension oldSize, Dimension newSize) {}
    	
    }
    
    public void addListener( ExampleModelElementListener listener ) {
    	((DynamicListenerController)_controller).addListener( listener );
    	Dimension size = getSize();
    	_controller.sizeChanged( size, size );
    	Point2D position = getPosition();
    	_controller.positionChanged( position, position );
    	double orientation = getOrientation();
    	_controller.orientationChanged( orientation, orientation );
    }
    
    public void removeListener( ExampleModelElementListener listener ) {
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
