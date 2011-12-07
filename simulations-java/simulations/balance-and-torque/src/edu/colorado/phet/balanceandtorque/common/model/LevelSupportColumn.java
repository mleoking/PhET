// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model;

import java.awt.geom.Rectangle2D;

/**
 * This is a column that can be used to support one of the ends of the plank
 * in a level position.  At the time of this writing, this type of column is
 * always used in conjunction with another that is holding up the other side of
 * the plank.
 *
 * @author John Blanco
 */
public class LevelSupportColumn extends ShapeModelElement {

    // Length of the base of the column
    private static final double WIDTH = 0.35;

    /**
     * Constructor.
     *
     * @param height
     * @param centerX
     */
    public LevelSupportColumn( double height, double centerX ) {
        super( new Rectangle2D.Double( centerX - WIDTH / 2, 0, WIDTH, height ) );
    }
}
