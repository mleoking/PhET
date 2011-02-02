// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.Dimension;

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

    private final Dimension boxSize;
    private final double boxSeparation;

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
     * Get the arrow offset.
     * The arrow is centered between the 2 boxes.
     * @return
     */
    public double getArrowXOffset() {
        return boxSize.getWidth() + ( boxSeparation / 2 );
    }

    /**
     * Gets the offsets for an equation's reactant terms.
     * The reactant terms are equally spaced inside the left box.
     * @param equation
     * @return
     */
    public double[] getReactantXOffsets( Equation equation ) {
        final int numberOfReactants = equation.getReactants().length;
        double[] xOffsets = new double[ numberOfReactants ];
        double deltaX = boxSize.getWidth() / ( numberOfReactants + 1 );
        for ( int i = 0; i < numberOfReactants; i++ ) {
            xOffsets[i] = ( i + 1 ) * deltaX;
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
        final int numberOfProducts = equation.getProducts().length;
        double[] xOffsets = new double[ numberOfProducts ];
        double deltaX = boxSize.getWidth() / ( numberOfProducts + 1 );
        for ( int i = 0; i < numberOfProducts; i++ ) {
            xOffsets[i] = boxSize.getWidth() + boxSeparation + ( ( i + 1 ) * deltaX );
        }
        return xOffsets;
    }
}
