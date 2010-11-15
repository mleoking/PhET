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
    
    /**
     * Adds a variable number of shapes to a primary shape.
     * @param primaryShape
     * @param shapes
     * @return
     */
    public static final Shape add( Shape primaryShape, Shape... shapes ) {
        Area area = new Area( primaryShape );
        for ( Shape shape : shapes ) {
            area.add( new Area( shape ) );
        }
        return area;
    }
    
    /**
     * Subtracts a variable number of shapes from a primary shape.
     * @param primaryShape
     * @param shapes
     * @return
     */
    public static final Shape subtract( Shape primaryShape, Shape... shapes ) {
        Area area = new Area( primaryShape );
        for ( Shape shape : shapes ) {
            area.subtract( new Area( shape ) );
        }
        return area;
    }
}
