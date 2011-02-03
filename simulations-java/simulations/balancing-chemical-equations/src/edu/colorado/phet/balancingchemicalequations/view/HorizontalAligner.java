// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import edu.colorado.phet.balancingchemicalequations.model.Equation;

/**
 * This sim's user interface shows multiple representations of a chemical equation,
 * often simultaneously, and those representations are vertically stacked.
 * Corresponding components in each representation must be horizontally aligned
 * to provide visual connections between the representations.
 * <p>
 * For example, for a bar chart below and equation, one bar should be positioned
 * directly below each term in the equation.
 * <p>
 * This class centralizes the logic for determining the location of components.
 * Location is specified as an x-offset, and the size of the "box of molecules"
 * is the key feature that determines the layout.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HorizontalAligner {

    private final double leftRightSideLength;
    private final double leftRightSeparation;

    public HorizontalAligner( double leftRightSideLength, double leftRightSeparation ) {
        this.leftRightSideLength = leftRightSideLength;
        this.leftRightSeparation = leftRightSeparation;
    }

    /**
     * Gets the x offset of the center between left and right hand sides.
     * @return
     */
    public double getCenterXOffset() {
        return leftRightSideLength + ( leftRightSeparation / 2 );
    }

    /**
     * Gets the offsets for an equation's reactant terms.
     * The box is divided up into columns and reactants are centered in the columns.
     * @param equation
     * @return
     */
    public double[] getReactantXOffsets( Equation equation ) {
        final int numberOfTerms = equation.getReactants().length;
        final double columnWidth = leftRightSideLength / Math.max( 2, numberOfTerms );
        double x = columnWidth / 2;
        double[] xOffsets = new double[ numberOfTerms ];
        for ( int i = 0; i < numberOfTerms; i++ ) {
            xOffsets[i] = x;
            x += columnWidth;
        }
        return xOffsets;
    }

    /**
     * Gets the offsets for an equation's product terms.
     * The product terms are equally spaced inside the right box.
     * @param equation
     * @return
     */
    public double[] getProductXOffsets( Equation equation ) {
        final int numberOfTerms = equation.getProducts().length;
        final double columnWidth = leftRightSideLength / Math.max( 2, numberOfTerms );
        double x = leftRightSideLength + leftRightSeparation + columnWidth / 2;
        double[] xOffsets = new double[ numberOfTerms ];
        for ( int i = 0; i < numberOfTerms; i++ ) {
            xOffsets[i] = x;
            x += columnWidth;
        }
        return xOffsets;
    }
}
