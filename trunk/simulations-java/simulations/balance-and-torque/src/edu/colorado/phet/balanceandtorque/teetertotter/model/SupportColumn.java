// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model;

import java.awt.geom.Rectangle2D;

/**
 * This is a column that can be used to support one of the ends of the plank.
 *
 * @author John Blanco
 */
public class SupportColumn extends ShapeModelElement {

    // Length of the base of the column
    private static final double WIDTH = 0.35;

    /**
     * Constructor.
     *
     * @param height
     * @param initialCenterX
     */
    public SupportColumn( double height, double initialCenterX ) {
        super( new Rectangle2D.Double( initialCenterX - WIDTH / 2, 0, WIDTH, height ) );
    }
}
