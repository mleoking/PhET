// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * Pointer is the base class for all pointer model elements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Pointer implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private Point2D _position;
    private double _orientation;
    private Dimension _size;
    private Color _color;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Pointer( Point2D position, double oriention, Dimension size, Color color ) {
        super();
        _position = new Point2D.Double( position.getX(), position.getY() );
        _orientation = oriention;
        _size = new Dimension( size );
        _color = color;
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    public Dimension getSize() {
        return new Dimension( _size );
    }
    
    public Color getColor() {
        return _color;
    }

    public void setPosition( Point2D position ) {
        _position.setLocation( position.getX(), position.getY() );
    }

    public Point2D getPosition() {
        return new Point2D.Double( _position.getX(), _position.getY() );
    }

    public void setOrientation( double orientation ) {
        _orientation = orientation;
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
