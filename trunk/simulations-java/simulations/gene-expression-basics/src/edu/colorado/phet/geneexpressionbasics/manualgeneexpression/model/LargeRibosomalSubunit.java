// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;

/**
 * Class that represents the small ribosomal subunit in the model.
 *
 * @author John Blanco
 */
public class LargeRibosomalSubunit extends MobileBiomolecule {

    private static final double WIDTH = 250;   // In nanometers.
    private static final double HEIGHT = 190;  // In nanometers.

    public LargeRibosomalSubunit() {
        this( new Point2D.Double( 0, 0 ) );

    }

    public LargeRibosomalSubunit( Point2D position ) {
        super( createShape(), new Color( 205, 155, 29 ) );
        setPosition( position );
    }

    private static Shape createShape() {
        return new Ellipse2D.Double( -WIDTH / 2, -HEIGHT / 2, WIDTH, HEIGHT );
    }
}
