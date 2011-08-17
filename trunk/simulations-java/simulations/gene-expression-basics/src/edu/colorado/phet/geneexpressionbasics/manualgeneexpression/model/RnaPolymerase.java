// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.geneexpressionbasics.common.common.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.common.ShapeCreationUtils;

/**
 * Class that represents RNA polymerase in the model.
 *
 * @author John Blanco
 */
public class RnaPolymerase extends MobileBiomolecule {

    private static final double WIDTH = 340;
    private static final double HEIGHT = 480;

    public RnaPolymerase() {
        this( new Point2D.Double( 0, 0 ) );

    }

    public RnaPolymerase( Point2D position ) {
        super( createShape(), new Color( 34, 240, 34 ) );
        setPosition( position );
    }

    private static Shape createShape() {
        // Shape is meant to look like illustrations in "The Machinery of
        // Life" by David Goodsell.
        List<Point2D> points = new ArrayList<Point2D>();
        points.add( new Point2D.Double( 0, HEIGHT / 2 ) ); // Middle top.
        points.add( new Point2D.Double( WIDTH / 2, HEIGHT * 0.25 ) );
        points.add( new Point2D.Double( WIDTH * 0.30, -HEIGHT * 0.25 ) );
        points.add( new Point2D.Double( 0, -HEIGHT / 2 ) ); // Middle bottom.
        points.add( new Point2D.Double( -WIDTH * 0.30, -HEIGHT * 0.25 ) );
        points.add( new Point2D.Double( -WIDTH / 2, HEIGHT * 0.25 ) );
        return ShapeCreationUtils.createRoundedShapeFromPoints( points );
    }
}
