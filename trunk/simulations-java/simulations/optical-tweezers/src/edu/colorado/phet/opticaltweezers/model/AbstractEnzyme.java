/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * AbstractEnzyme is base class for all enzymes.
 * An enzyme that feeds on ATP in the fluid and pulls on the DNA.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractEnzyme extends FixedObject implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_ENABLED = "enabled";
    public static final String PROPERTY_INNER_ORIENTATION = "innerOrientation";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final double _outerDiameter, _innerDiameter;
    private double _innerOrientation;
    private boolean _enabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractEnzyme( Point2D position, double outerDiameter, double innerDiameter ) {
        super( position, 0 /* orientation */ );
        _outerDiameter = outerDiameter;
        _innerDiameter = innerDiameter;
        _innerOrientation = 0;
        _enabled = false;
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
    
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            notifyObservers( PROPERTY_ENABLED );
        }
    }
    
    public boolean isEnabled() {
        return _enabled;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    public void stepInTime( double dt ) {
        if ( _enabled ) {
            _innerOrientation += Math.toRadians( 10 );//XXX rotate
            notifyObservers( PROPERTY_INNER_ORIENTATION );
        }
    }
}
