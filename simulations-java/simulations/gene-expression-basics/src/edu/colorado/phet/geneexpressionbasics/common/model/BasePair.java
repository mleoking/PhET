// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Model class for the base pair in the DNA molecule.  In the real world, a
 * "base pair" is a pair of nitrogenous bases that connects to the DNA backbone
 * on one side and in the center of the DNA strand on the other.  For the
 * purposes of this simulation, a base pair only needs to represent a
 * structural element of the DNA, and the individual bases are not actually
 * encapsulated here (nor anywhere in this simulation).
 * <p/>
 * In this class the width of an individual base par is a constant, but the
 * height can vary. This is used to create the illusion of a twisted strand of
 * DNA - the shorter base pairs are the ones that are more angled, and the
 * longer ones are the ones that are seen directly from the side.
 *
 * @author John Blanco
 */
public class BasePair {
    private static final double BASE_PAIR_WIDTH = 13; // In picometers.  Not sure if this is close to real life, chosen to look decent in view.

    private final Shape shape;

    public BasePair( Point2D centerLocation, double height ) {
        shape = new RoundRectangle2D.Double( centerLocation.getX() - BASE_PAIR_WIDTH / 2, centerLocation.getY() - height / 2, BASE_PAIR_WIDTH,
                                             height, BASE_PAIR_WIDTH / 5, BASE_PAIR_WIDTH / 5 );
    }

    public Shape getShape() {
        return shape;
    }

    public Point2D getCenterLocation() {
//        return new Point2D.Double( shape.getBounds2D().getCenterX(), shape.getBounds2D().getCenterY() );
        return new Point2D.Double( shape.getBounds2D().getCenterX(), shape.getBounds2D().getCenterY() );
    }
}
