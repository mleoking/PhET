// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.model;

import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.geneexpressionbasics.common.model.BioShapeUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.ShapeChangingModelElement;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author John Blanco
 */
public class Cell extends ShapeChangingModelElement {

    // Bounding size for cells.
    public static final Dimension2D CELL_SIZE = new PDimension( 2E-6, 1E-6 ); // In meters.

    public Cell( long seed ) {
        this( new Point2D.Double( 0, 0 ), seed );
    }

    public Cell( Point2D initialPosition, long seed ) {
        super( createShape( initialPosition, seed ) );
    }

    private static Shape createShape( Point2D initialPosition, long seed ) {
        return BioShapeUtils.createCurvyEnclosedShape( new Rectangle2D.Double( initialPosition.getX() - CELL_SIZE.getWidth() / 2,
                                                                               initialPosition.getY() - CELL_SIZE.getHeight() / 2,
                                                                               CELL_SIZE.getWidth(),
                                                                               CELL_SIZE.getHeight() ),
                                                       0.4,
                                                       seed );
    }
}
