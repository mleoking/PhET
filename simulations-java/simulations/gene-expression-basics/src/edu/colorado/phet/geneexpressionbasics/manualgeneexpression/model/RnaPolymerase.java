// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;

/**
 * @author John Blanco
 */
public class RnaPolymerase extends MobileBiomolecule {

    private static final double WIDTH = 340;
    private static final double HEIGHT = 150;


    public RnaPolymerase() {
        this( new Point2D.Double( 0, 0 ) );

    }

    public RnaPolymerase( Point2D position ) {
        super( createShape(), new Color( 34, 139, 34 ) );
        setPosition( position );
    }

    public Paint getPaint() {
        return new Color( 0, 200, 40 );
    }

    private static Shape createShape() {
        GeneralPath path = new GeneralPath();
        // Start from left side, vertical center.
        path.moveTo( -WIDTH / 2, 0 );
        path.lineTo( -WIDTH * 0.2, HEIGHT * 0.5 );
        path.lineTo( WIDTH * 0.4, HEIGHT * 0.4 );
        path.lineTo( WIDTH / 2, 0 );
        path.lineTo( WIDTH * 0.4, -HEIGHT * 0.5 );
        path.lineTo( -WIDTH * 0.3, -HEIGHT * 0.5 );
        path.lineTo( -WIDTH * 0.4, -HEIGHT * 0.2 );
        path.closePath();
        return path;
    }
}
