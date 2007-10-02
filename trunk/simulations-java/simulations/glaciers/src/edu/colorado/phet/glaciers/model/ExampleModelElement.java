/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * ExampleModelElement is an example model element.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModelElement extends MovableObject implements ModelElement {
    
    private final double _radius;

    public ExampleModelElement( Point2D position, double orientation, double radius ) {
        super( position, orientation, 0 /* speed */ );
        _radius = radius;
    }

    public double getRadius() {
        return _radius;
    }
    
    public void stepInTime( double dt ) {
        //XXX
    }
}
