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
public class MessengerRnaDestroyer extends MobileBiomolecule {

    private static final double WIDTH = 250;   // In nanometers.
    private static final double HEIGHT = 190;  // In nanometers.

    public MessengerRnaDestroyer() {
        this( new Point2D.Double( 0, 0 ) );
    }

    public MessengerRnaDestroyer( Point2D position ) {
        super( createShape(), new Color( 255, 150, 66 ) );
        setPosition( position );
    }

    private static Shape createShape() {
        GeneralPath path = new GeneralPath();
        // Start from top center.  This is a triangle.
        path.moveTo( 0, HEIGHT * 0.5 );
        path.lineTo( WIDTH * 0.5, -HEIGHT * 0.5 );
        path.lineTo( -WIDTH * 0.5, -HEIGHT * 0.5 );
        path.closePath();
        return path;
    }
}
