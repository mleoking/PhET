// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.model;

import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.geneexpressionbasics.common.model.ShapeChangingModelElement;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author John Blanco
 */
public class Cell extends ShapeChangingModelElement {

    // Bounding size for cells.
    private static final Dimension2D CELL_BOUNDS = new PDimension( 2E-6, 0.5E-6 ); // In meters.

    public Cell( Point2D initialPosition ) {
        super( createShape( initialPosition ) );
    }

    private static Shape createShape( Point2D initialPosition ) {
        return new Rectangle2D.Double( initialPosition.getX() - CELL_BOUNDS.getWidth() / 2,
                                       initialPosition.getY() - CELL_BOUNDS.getHeight() / 2,
                                       CELL_BOUNDS.getWidth(),
                                       CELL_BOUNDS.getHeight() );
    }
}
