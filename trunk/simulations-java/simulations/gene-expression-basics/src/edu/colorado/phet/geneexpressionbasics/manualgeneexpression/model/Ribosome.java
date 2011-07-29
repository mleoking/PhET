// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;

/**
 * Class that represents the a ribosome in the model.
 *
 * @author John Blanco
 */
public class Ribosome extends MobileBiomolecule {

    private static final double WIDTH = 290;                  // In nanometers.
    private static final double OVERALL_HEIGHT = 300;         // In nanometers.
    private static final double TOP_PORTION_HEIGHT_PROPORTION = 0.7;
    private static final double TOP_PORTION_HEIGHT = OVERALL_HEIGHT * TOP_PORTION_HEIGHT_PROPORTION;
    private static final double BOTTOM_PORTION_HEIGHT = OVERALL_HEIGHT * ( 1 - TOP_PORTION_HEIGHT_PROPORTION );

    public Ribosome() {
        this( new Point2D.Double( 0, 0 ) );

    }

    public Ribosome( Point2D position ) {
        super( createShape(), new Color( 205, 155, 29 ) );
        setPosition( position );
    }

    private static Shape createShape() {
        // Draw the top portion, which in this sim is the larger subunit.  The
        // shape is essentially a lumpy ellipse, and is based on some drawings
        // seen on the web.  Start at the top center.
//        DoubleGeneralPath topPortionPath = new DoubleGeneralPath();
//        topPortionPath.moveTo( 0, OVERALL_HEIGHT * 0.5 );
//        topPortionPath.quadTo( WIDTH * 0.5, BOTTOM_PORTION_HEIGHT * 0.5, WIDTH * 0.5, -BOTTOM_PORTION_HEIGHT * 0.2 );
//        topPortionPath.quadTo( WIDTH * 0.5, -BOTTOM_PORTION_HEIGHT * 0.5, 0, -BOTTOM_PORTION_HEIGHT * 0.4 );
//        topPortionPath.quadTo( -WIDTH * 0.5, -BOTTOM_PORTION_HEIGHT * 0.5, -WIDTH * 0.5, -BOTTOM_PORTION_HEIGHT * 0.2 );
//        topPortionPath.quadTo( -WIDTH * 0.5, BOTTOM_PORTION_HEIGHT * 0.5, 0, BOTTOM_PORTION_HEIGHT * 0.5 );
//        topPortionPath.closePath();
//        Shape topPortionShape = AffineTransform.getTranslateInstance( 0, OVERALL_HEIGHT / 2 ).createTransformedShape( new Ellipse2D.Double( -WIDTH / 2, -TOP_PORTION_HEIGHT / 2, WIDTH, TOP_PORTION_HEIGHT ) );
        Shape topPortionShape = AffineTransform.getTranslateInstance( 0, OVERALL_HEIGHT / 4 ).createTransformedShape( new Ellipse2D.Double( -WIDTH / 2, -TOP_PORTION_HEIGHT / 2, WIDTH, TOP_PORTION_HEIGHT ) );
        // Create the bottom portion, which is a more compact ellipse.
        Shape bottomPortionShape = AffineTransform.getTranslateInstance( 0, -OVERALL_HEIGHT / 4 ).createTransformedShape( new Ellipse2D.Double( -WIDTH / 2, -BOTTOM_PORTION_HEIGHT / 2, WIDTH, BOTTOM_PORTION_HEIGHT ) );
//        Shape bottomPortionShape = AffineTransform.getTranslateInstance( 0, -OVERALL_HEIGHT / 2 ).createTransformedShape( new Ellipse2D.Double( -WIDTH / 2, -BOTTOM_PORTION_HEIGHT / 2, WIDTH, BOTTOM_PORTION_HEIGHT ) );
        Area combinedShape = new Area( topPortionShape );
        combinedShape.add( new Area( bottomPortionShape ) );
        return combinedShape;
    }
}
