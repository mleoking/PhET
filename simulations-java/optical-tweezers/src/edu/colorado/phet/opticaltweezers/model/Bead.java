/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.ModelElement;

/**
 * Bead is the model of a glass bead, the dialectric particle in this experiement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Bead extends MovableObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_DIAMETER = "diameter";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _diameter; // nm
    private final double _density; // g/nm^3
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Bead( Point2D position, double orientation, double diameter, double density ) {
        super( position, orientation, 0 /* speed */ );
        if ( diameter <= 0 ) {
            throw new IllegalArgumentException( "diameter must be > 0: " + diameter );
        }
        if ( density <= 0 ) {
            throw new IllegalArgumentException( "density must be > 0: " + density );   
        }
        _diameter = diameter;
        _density = density;
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the diameter.
     * 
     * @param diameter diameter in nm
     */
    public void setDiameter( double diameter ) {
        if ( diameter <= 0 ) {
            throw new IllegalArgumentException( "diameter must be > 0" );
        }
        if ( diameter != _diameter ) {
            _diameter = diameter;
            notifyObservers( PROPERTY_DIAMETER );
        }
    }
    
    /**
     * Gets the diameter.
     * 
     * @return diameter in nm
     */
    public double getDiameter() {
        return _diameter;
    }
    
    /**
     * Gets the mass.
     * 
     * @return mass, in grams (g)
     */
    public double getMass() {
        double radius = ( _diameter / 2 );
        double volume = ( 4. / 3. ) * Math.PI * ( radius * radius * radius );
        return volume * _density;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        // TODO Auto-generated method stub
    }
}
