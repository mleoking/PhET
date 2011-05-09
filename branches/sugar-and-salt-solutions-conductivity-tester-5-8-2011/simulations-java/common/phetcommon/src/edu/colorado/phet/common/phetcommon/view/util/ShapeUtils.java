// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.*;
import java.awt.geom.Area;

/**
 * Utilities related to Shapes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ShapeUtils {

    /* not intended for instantiation */
    private ShapeUtils() {
    }

    /**
     * Do two Shapes intersect?
     *
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
     * Returns the intersection of 2 Shapes.
     *
     * @param s1
     * @param s2
     * @return
     */
    public static final Shape intersect( Shape s1, Shape s2 ) {
        Area area = new Area( s1 );
        area.intersect( new Area( s2 ) );
        return area;
    }

    /**
     * Adds a variable number of Shapes.
     *
     * @param shape
     * @param shapes
     * @return
     */
    public static final Shape add( Shape shape, Shape... shapes ) {
        Area area = new Area( shape );
        for ( Shape s : shapes ) {
            area.add( new Area( s ) );
        }
        return area;
    }

    /**
     * Subtracts a variable number of Shapes, in the order of the args.
     *
     * @param shape
     * @param shapes
     * @return
     */
    public static final Shape subtract( Shape shape, Shape... shapes ) {
        Area area = new Area( shape );
        for ( Shape s : shapes ) {
            area.subtract( new Area( s ) );
        }
        return area;
    }
}
