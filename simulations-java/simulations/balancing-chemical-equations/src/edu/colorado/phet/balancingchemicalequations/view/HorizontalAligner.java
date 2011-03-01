// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.Dimension;

import javax.swing.SwingConstants;

import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;

/**
 * Encapsulates the strategy used to horizontally aligning terms in an equation
 * with columns of molecules in the "boxes" view.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HorizontalAligner {

    private final Dimension boxSize;
    private final double boxSeparation;

    /**
     * Constructor.
     * @param boxWidth width of one of the 2 boxes
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
        return getXOffsets( equation.getReactants(), SwingConstants.RIGHT, 0 /* left edge of left box */ );
    }

    /**
     * Gets the offsets for an equation's product terms.
     * Products are on the right-hand side of the equation.
     * @param equation
     * @return
     */
    public double[] getProductXOffsets( Equation equation ) {
        return getXOffsets( equation.getProducts(), SwingConstants.LEFT, boxSize.getWidth() + boxSeparation /* left edge of right box */ );
    }

    /*
     * Gets the x offsets for a set of terms.
     * The box is divided up into columns and terms are centered in the columns.
     * In the general case, N terms will divide the box into N columns.
     * In the special case of 1 term, the box will be divided into 2 columns,
     * and the single term will be placed in the column that corresponds to alignment.
     *
     * @param terms
     * @param alignment
     * @param xAdjustment
     */
    private double[] getXOffsets( EquationTerm[] terms, int alignment, double xAdjustment ) {
        assert( alignment == SwingConstants.LEFT || alignment == SwingConstants.RIGHT );
        final int numberOfTerms = terms.length;
        final double columnWidth = boxSize.getWidth() / Math.max( 2, numberOfTerms );
        double x = xAdjustment + columnWidth / 2;
        double[] xOffsets = new double[ numberOfTerms ];
        if ( numberOfTerms == 1 && alignment == SwingConstants.RIGHT ) {
            xOffsets[0] = x + columnWidth;
        }
        else {
            for ( int i = 0; i < numberOfTerms; i++ ) {
                xOffsets[i] = x;
                x += columnWidth;
            }
        }
        return xOffsets;
    }
}
