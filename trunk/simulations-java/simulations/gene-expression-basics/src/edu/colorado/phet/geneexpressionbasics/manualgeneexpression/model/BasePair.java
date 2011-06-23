// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Model class for the base pair in the DNA molecule.
 *
 * @author John Blanco
 */
public class BasePair {
    private static final double BASE_PAIR_WIDTH = 20; // In picometers.  Not sure if this is close to real life, chosen to look decent in view.

    private final Shape shape;

    public BasePair( Point2D centerLocation, double height ) {
        shape = new RoundRectangle2D.Double( centerLocation.getX() - BASE_PAIR_WIDTH / 2, centerLocation.getY() - height / 2, BASE_PAIR_WIDTH,
                                             height, BASE_PAIR_WIDTH / 5, BASE_PAIR_WIDTH / 5 );
    }

    public Shape getShape() {
        return shape;
    }
}
