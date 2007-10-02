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
public class ExampleModelElement extends MovableObject implements ModelElement {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Dimension _size;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ExampleModelElement( Point2D position, double orientation, Dimension size ) {
        super( position, orientation, 0 /* speed */ );
        _size = size;
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public Dimension getSize() {
        return _size;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        // do nothing
    }
}
