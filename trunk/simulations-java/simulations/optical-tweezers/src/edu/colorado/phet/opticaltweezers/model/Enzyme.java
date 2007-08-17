/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * Enzyme is the model of an enzyme that feeds on ATP in the fluid and pulls on the DNA.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Enzyme extends FixedObject implements ModelElement {
    
    private Dimension _size; // nm
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Enzyme( Point2D position, Dimension size ) {
        super( position, 0 /* orientation */ );
        _size = size;
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public Dimension getSize() {
        return _size;
    }
    
    public double getWidth() {
        return _size.getWidth();
    }
    
    public double getHeight() {
        return _size.getHeight();
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    public void stepInTime( double dt ) {
        //XXX
    }

}
