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
public class TranscriptionFactor extends MobileBiomolecule {

    private static final double WIDTH = 350;   // In nanometers.
    private static final double HEIGHT = 240;  // In nanometers.

    public TranscriptionFactor() {
        this( new Point2D.Double( 0, 0 ) );
    }

    public TranscriptionFactor( Point2D position ) {
        super( createShape(), Color.yellow );
        setPosition( position );
    }

    private static Shape createShape() {
        GeneralPath path = new GeneralPath();
        // Start from left side, vertical center.
        path.moveTo( -WIDTH * 0.5, 0 );
//        path.quadTo( -WIDTH * 0.7, HEIGHT * 0.5, -WIDTH * 0.3, HEIGHT * 0.5 );
        path.lineTo( -WIDTH * 0.2, HEIGHT * 0.5 );
        path.lineTo( WIDTH * 0.2, HEIGHT * 0.2 );  // 3
        path.lineTo( WIDTH * 0.4, HEIGHT * 0.5 );  // 4
        path.lineTo( WIDTH * 0.5, -HEIGHT * 0.3 );  // 5
        path.lineTo( WIDTH * 0.25, -HEIGHT * 0.5 );  // 6
        path.lineTo( -WIDTH * 0.2, HEIGHT * 0.0 );  // 7
        path.lineTo( -WIDTH * 0.4, -HEIGHT * 0.2 );  // 8
        path.lineTo( -WIDTH * 0.5, 0 );  // 9
        path.closePath();
        return path;
    }
}
