/* Copyright 2007, University of Colorado */

package edu.colorado.phet.simtemplate.model;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

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
    
    private ArrayList _listeners;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleModelElement( Dimension size, Point2D position, double orientation ) {
        super();
        _size = new Dimension( size );
        _position = new Point2D.Double( position.getX(), position.getY() );
        _orientation = orientation;
        _listeners = new ArrayList();
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setSize( Dimension size ) {
        if ( !size.equals( _size ) ) {
            _size.setSize( size );
            notifySizeChanged();
        }
    }
    
    public Dimension getSize() {
        return new Dimension( _size );
    }
    
    public Dimension getSizeReference() {
        return _size;
    }
    
    public void setPosition( Point2D position ) {
        if ( !position.equals( _position ) ) {
            _position.setLocation( position );
            notifyPositionChanged();
        }
    }
    
    public Point2D getPosition() {
        return new Point2D.Double( _position.getX(), _position.getY() );
    }
    
    public Point2D getPositionReference() {
        return _position;
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
    
    public interface ExampleModelElementListener {
        public void sizeChanged();
        public void positionChanged();
        public void orientationChanged();
    }

    public static class ExampleModelElementAdapter implements ExampleModelElementListener {
        public void orientationChanged() {}
        public void positionChanged() {}
        public void sizeChanged() {}

    }

    public void addListener( ExampleModelElementListener listener ) {
        _listeners.add( listener );
    }

    public void removeListener( ExampleModelElementListener listener ) {
        _listeners.remove( listener );
    }

    private void notifySizeChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (ExampleModelElementListener) _listeners.get( i ) ).sizeChanged();
        }
    }
    
    private void notifyPositionChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (ExampleModelElementListener) _listeners.get( i ) ).positionChanged();
        }
    }
    
    private void notifyOrientationChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (ExampleModelElementListener) _listeners.get( i ) ).orientationChanged();
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
