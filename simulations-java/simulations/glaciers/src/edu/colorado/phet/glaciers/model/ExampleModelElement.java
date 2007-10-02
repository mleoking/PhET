/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;

/**
 * ExampleModelElement is an example model element.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModelElement extends MovableObject {

    public ExampleModelElement( Point2D position, double orientation ) {
        super( position, orientation, 0 /* speed */ );
    }
}
