/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.model;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;


public class PlumPuddingModel extends AbstractHydrogenAtom {

    // radius of the atom's goo
    private double _radius;
    
    /**
     * Constructor.
     * @param position
     * @param radius
     */
    public PlumPuddingModel( Point2D position, double radius ) {
        super( position, 0 /* orientation */ );
        _radius = radius;
    }
    
    /**
     * Gets the radius of the atom.
     * @return radius
     */
    public double getRadius() {
        return _radius;
    }

}
