// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;

/**
 * Class that represents the small ribosomal subunit in the model.
 *
 * @author John Blanco
 */
public class SmallRibosomalSubunit extends MobileBiomolecule {

    private static final double WIDTH = 340;   // In nanometers.
    private static final double HEIGHT = 80;  // In nanometers.

    public SmallRibosomalSubunit() {
        this( new Point2D.Double( 0, 0 ) );

    }

    public SmallRibosomalSubunit( Point2D position ) {
        super( createShape(), new Color( 205, 155, 29 ) );
        setPosition( position );
    }

    private static Shape createShape() {
        GeneralPath path = new GeneralPath();
        // Start from top center.
        path.moveTo( 0, HEIGHT * 0.5 );
        path.quadTo( WIDTH * 0.5, HEIGHT * 0.5, WIDTH * 0.5, -HEIGHT * 0.2 );
        path.quadTo( WIDTH * 0.5, -HEIGHT * 0.5, 0, -HEIGHT * 0.4 );
        path.quadTo( -WIDTH * 0.5, -HEIGHT * 0.5, -WIDTH * 0.5, -HEIGHT * 0.2 );
        path.quadTo( -WIDTH * 0.5, HEIGHT * 0.5, 0, HEIGHT * 0.5 );
        path.closePath();
        return path;
    }
}
