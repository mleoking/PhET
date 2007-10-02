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
    
    private final Dimension _size;

    public ExampleModelElement( Point2D position, double orientation, Dimension size ) {
        super( position, orientation, 0 /* speed */ );
        _size = size;
    }

    public Dimension getSize() {
        return _size;
    }
    
    public void stepInTime( double dt ) {
        // do nothing
    }
}
