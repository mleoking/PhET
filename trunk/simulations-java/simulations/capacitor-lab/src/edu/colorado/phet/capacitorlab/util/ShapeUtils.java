/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.util;

import java.awt.Shape;
import java.awt.geom.Area;

/**
 * Utilities related to Shapes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ShapeUtils {
    
    /* not intended for instantiation */
    private ShapeUtils() {}
    
    /**
     * Do two Shapes intersect?
     * @param shape1
     * @param shape2
     * @return
     */
    public static final boolean intersects( Shape shape1, Shape shape2 ) {
        Area area1 = new Area( shape1 );
        Area area2 = new Area( shape2 );
        area1.intersect( area2 );
        return !area1.isEmpty();
    }
}
