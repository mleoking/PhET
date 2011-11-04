// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;

import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;

/**
 * Base class for proteins.
 *
 * @author John Blanco
 */
public abstract class Protein extends MobileBiomolecule {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Max value for the growth factor, indicates that it is fully grown.
    public static final double MAX_GROWTH_FACTOR = 1;

    // Default values used during construction.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // A value between 0 and 1 that defines how fully developed, or "grown"
    // this protein is.
    private double growthFactor = 0;

    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------

    protected Protein( GeneExpressionModel model, Shape initialShape, Color baseColor ) {
        super( model, initialShape, baseColor );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public void setGrowthFactor( double growthFactor ) {
        this.growthFactor = growthFactor;
        updateShape();
    }

    public double getGrowthFactor() {
        return growthFactor;
    }

    public void grow( double growthAmount ) {
        assert growthAmount >= 0;
        if ( growthAmount < 0 ) {
            // Ignore this.
            return;
        }
        setGrowthFactor( Math.min( growthFactor + growthAmount, 1 ) );
    }

    /**
     * Update the shape based on the current growth factor setting.
     */
    protected abstract void updateShape();

    protected abstract Shape getShape( double growthFactor );

    protected Shape getFullyGrownShape() {
        return getShape( MAX_GROWTH_FACTOR );
    }
}
