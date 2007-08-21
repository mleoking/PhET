/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * Enzyme is the model of an enzyme that feeds on ATP in the fluid and pulls on the DNA.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Enzyme extends FixedObject implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_INNER_ORIENTATION = "innerOrientation";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final double _outerDiameter, _innerDiameter;
    private double _innerOrientation;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Enzyme( Point2D position, double outerDiameter, double innerDiameter ) {
        super( position, 0 /* orientation */ );
        _outerDiameter = outerDiameter;
        _innerDiameter = innerDiameter;
        _innerOrientation = 0;
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public double getOuterDiameter() {
        return _outerDiameter;
    }
    
    public double getInnerDiameter() {
        return _innerDiameter;
    }
    
    public double getInnerOrientation() {
        return _innerOrientation;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    public void stepInTime( double dt ) {
        _innerOrientation += Math.toRadians( 10 );//XXX rotate
        notifyObservers( PROPERTY_INNER_ORIENTATION );
    }
}
