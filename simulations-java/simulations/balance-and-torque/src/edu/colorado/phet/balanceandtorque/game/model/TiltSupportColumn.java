// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.balanceandtorque.common.model.ShapeModelElement;

//REVIEW class unused, delete?

/**
 * This is a column that can be used to support one of the ends of the plank.
 *
 * @author John Blanco
 */
public class TiltSupportColumn extends ShapeModelElement {

    // Length of the base of the column
    private static final double WIDTH = 0.35;

    /**
     * Constructor.
     *
     * @param height
     * @param initialCenterX
     */
    public TiltSupportColumn( double height, double initialCenterX ) {
        super( new Rectangle2D.Double( initialCenterX - WIDTH / 2, 0, WIDTH, height ) );
    }
}
