/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.ModelElement;


public class Bead extends MovableObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_DIAMETER = "diameter";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _diameter; // nm
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Bead( Point2D position, double orientation, double diameter ) {
        super( position, orientation, 0 /* speed */ );
        if ( diameter <= 0 ) {
            throw new IllegalArgumentException( "diameter must be > 0: " + diameter );
        }
        _diameter = diameter;
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public void setDiameter( double diameter ) {
        if ( diameter <= 0 ) {
            throw new IllegalArgumentException( "diameter must be > 0" );
        }
        if ( diameter != _diameter ) {
            _diameter = diameter;
            notifyObservers( PROPERTY_DIAMETER );
        }
    }
    
    public double getDiameter() {
        return _diameter;
    }

    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        // TODO Auto-generated method stub
    }
}
