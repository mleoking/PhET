// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * @author John Blanco
 */
public class ProteinA extends Protein {

    private static final Color BASE_COLOR = Color.YELLOW;

    protected ProteinA( GeneExpressionModel model ) {
        super( model, createInitialShape(), BASE_COLOR );
    }

    @Override protected void updateShape() {
        // TODO: Stubbed.
    }

    @Override protected Shape getShape( double growthFactor ) {
        // TODO: Stubbed.
        return createInitialShape();
    }

    private static Shape createInitialShape() {
        return new Ellipse2D.Double( -1, -1, 2, 2 );
    }
}
