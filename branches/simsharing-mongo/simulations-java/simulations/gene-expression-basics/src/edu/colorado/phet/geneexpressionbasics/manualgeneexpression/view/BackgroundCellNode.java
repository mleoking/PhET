// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.common.model.BioShapeUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A Piccolo node that looks like a cell (as in the biology concept of a cell).
 * This is used when cells are needed in the background and have no
 * corresponding model component.
 *
 * @author John Blanco
 */
public class BackgroundCellNode extends PNode {

    private static final Color CELL_INTERIOR_COLOR = new Color( 190, 231, 251 );
    private static final Dimension2D DEFAULT_SIZE = new PDimension( 8000, 4000 ); // In screen coordinates, which are roughly the same as pixels.

    public BackgroundCellNode( Point2D centerLocation, int seed ) {
        addChild( new PhetPPath( createShape( centerLocation, DEFAULT_SIZE, seed ),
                                 CELL_INTERIOR_COLOR,
                                 new BasicStroke( 1f ),
                                 Color.BLACK ) );
    }

    private static Shape createShape( Point2D initialPosition, Dimension2D size, long seed ) {
        return BioShapeUtils.createCurvyEnclosedShape( new Rectangle2D.Double( initialPosition.getX() - size.getWidth() / 2,
                                                                               initialPosition.getY() - size.getHeight() / 2,
                                                                               size.getWidth(),
                                                                               size.getHeight() ),
                                                       0.4,
                                                       seed );
    }
}