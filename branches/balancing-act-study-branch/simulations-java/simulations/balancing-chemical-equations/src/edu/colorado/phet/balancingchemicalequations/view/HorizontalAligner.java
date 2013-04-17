// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.Dimension;

import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;

/**
 * Encapsulates the strategy used to horizontally aligning terms in an equation
 * with columns of molecules in the "boxes" view.  Based on knowledge of the
 * size and separation of the boxes, we determine the x-axis offset for each
 * term in the equation.  This offset is relative to a local coordinate system
 * where the origin is at (0,0).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HorizontalAligner {

    private final Dimension boxSize;
    private final double boxSeparation;

    /**
     * Constructor.
     * @param boxSize size of one of the 2 boxes (both boxes are assumed to be the same size)
     * @param boxSeparation horizontal separation between the left and right boxes
     */
    public HorizontalAligner( Dimension boxSize, double boxSeparation ) {
        this.boxSize = new Dimension( boxSize );
        this.boxSeparation = boxSeparation;
    }

    public Dimension getBoxSizeReference() {
        return boxSize;
    }

    public double getBoxSeparation() {
        return boxSeparation;
    }

    /**
     * Gets the x offset of the center between left- and right-hand sides.
     * @return
     */
    public double getCenterXOffset() {
        return boxSize.getWidth() + ( boxSeparation / 2 );
    }

    /**
     * Gets the offsets for an equation's reactant terms.
     * Reactants are on the left-hand side of the equation.
     * @param equation
     * @return
     */
    public double[] getReactantXOffsets( Equation equation ) {
        return getXOffsets( equation.getReactants(), false /* right justified */, 0 /* left edge of left box */ );
    }

    /**
     * Gets the offsets for an equation's product terms.
     * Products are on the right-hand side of the equation.
     * @param equation
     * @return
     */
    public double[] getProductXOffsets( Equation equation ) {
        return getXOffsets( equation.getProducts(), true /* left justified */, boxSize.getWidth() + boxSeparation /* left edge of right box */ );
    }

    /*
     * Gets the x offsets for a set of terms.
     * The box is divided up into columns and terms are centered in the columns.
     * @param terms
     * @param leftJustified
     * @param xAdjustment
     */
    private double[] getXOffsets( EquationTerm[] terms, boolean leftJustified, double xAdjustment ) {
        final int numberOfTerms = terms.length;
        double[] xOffsets = new double[ numberOfTerms ];
        if ( numberOfTerms == 1 ) {
            /*
             * In the special case of 1 term, the box is divided into 2 columns,
             * and the single term is centered in the column that corresponds to alignment.
             */
            if ( leftJustified ) {
                xOffsets[0] = xAdjustment + ( 0.25 * boxSize.getWidth() );
            }
            else {
                xOffsets[0] = xAdjustment + ( 0.75 * boxSize.getWidth() );
            }
        }
        else {
            /*
             * In the general case of N terms, the box is divided into N columns,
             * and one term is centered in each column.
             */
            final double columnWidth = boxSize.getWidth() / numberOfTerms;
            double x = xAdjustment + columnWidth / 2;
            for ( int i = 0; i < numberOfTerms; i++ ) {
                xOffsets[i] = x;
                x += columnWidth;
            }
        }
        return xOffsets;
    }
}
