/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * ExampleModelElement is an example model element.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModelElement extends GlaciersObservable implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_SIZE = "size";
    public static final String PROPERTY_POSITION = "position";
    public static final String PROPERTY_ORIENTATION = "orientation";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Dimension _size;
    private Point2D _position;
    private double _orientation;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleModelElement( Dimension size, Point2D position, double orientation ) {
        super();
        _size = new Dimension( size );
        _position = new Point2D.Double( position.getX(), position.getY() );
        _orientation = orientation;
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setSize( Dimension size ) {
        setSize( size.getWidth(), size.getHeight() );
    }
    
    public void setSize( double width, double height ) {
        if ( width != _size.getWidth() || height != _size.getHeight() ) {
            _size.setSize( width, height );
            notifyObservers( PROPERTY_SIZE );
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
