// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.common.model.BioShapeUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A Piccolo node that looks like a cell (as in the biology concept of a cell).
 * This is used when cells are needed in the background and have no
 * corresponding model component.
 *
 * @author John Blanco
 */
class BackgroundCellNode extends PNode {

    // Default size in screen coordinates, which are pretty close to pixels
    // when there is no zoom in effect.  Size was empirically determined to
    // hold the DNA strand.
    public static final Dimension2D DEFAULT_SIZE = new PDimension( 65000, 20000 );

    private static final Color CELL_INTERIOR_COLOR = new Color( 190, 231, 251 );

    public BackgroundCellNode( Point2D centerLocation, int seed ) {
        this( centerLocation, DEFAULT_SIZE, 0, seed );
    }

    public BackgroundCellNode( Point2D centerLocation, Dimension2D size, double rotationAngle, int seed ) {
        PPath cellBody = new PhetPPath( BioShapeUtils.createEColiLikeShape( centerLocation, size.getWidth(), DEFAULT_SIZE.getHeight(), 0, seed ),
                                        new BasicStroke( 500f ), // This is big because the cell is only ever shown when zoomed way out.
                                        Color.WHITE );
        cellBody.rotateAboutPoint( rotationAngle, centerLocation );
        Paint cellInteriorPaint = new GradientPaint( (float) ( centerLocation.getX() - size.getWidth() * 0.1 ),
                                                     (float) ( centerLocation.getY() - size.getHeight() * 0.5 ),
                                                     ColorUtils.darkerColor( CELL_INTERIOR_COLOR, 0.25 ),
                                                     (float) ( centerLocation.getX() + size.getWidth() * 0.1 ),
                                                     (float) ( centerLocation.getY() + size.getHeight() * 0.5 ),
                                                     ColorUtils.brighterColor( CELL_INTERIOR_COLOR, 0.25 ) );
        cellBody.setPaint( cellInteriorPaint );
        addChild( cellBody );
    }
}
