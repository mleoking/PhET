// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.geom.GeneralPath;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Draws a 2-dimensional grid of cells.  Origin is at the upper-left corner.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GridLinesNode extends PComposite {

    public GridLinesNode( int rows, int columns, double width, double height, Stroke stroke, Paint strokePaint, Paint fillPaint ) {
        super();

        final PDimension cellSize = new PDimension( width / columns, height / rows );

        // outside edge
        PBounds edgeBounds = new PBounds( 0, 0, width, height );
        PPath edgeNode = new PPath( edgeBounds );
        edgeNode.setStroke( stroke );
        edgeNode.setStrokePaint( strokePaint );
        edgeNode.setPaint( fillPaint );
        addChild( edgeNode );

        // horizontal lines
        for ( int row = 0; row < rows; row++ ) {
            GeneralPath path = new GeneralPath();
            path.moveTo( (float) edgeBounds.getMinX(), (float) ( edgeBounds.getMinY() + ( row * cellSize.getHeight() ) ) );
            path.lineTo( (float) edgeBounds.getMaxX(), (float) ( edgeBounds.getMinY() + ( row * cellSize.getHeight() ) ) );
            PPath lineNode = new PPath( path );
            lineNode.setStroke( stroke );
            lineNode.setStrokePaint( strokePaint );
            addChild( lineNode );
        }

        // vertical lines
        for ( int column = 0; column < columns; column++ ) {
            GeneralPath path = new GeneralPath();
            path.moveTo( (float) ( edgeBounds.getMinX() + ( column * cellSize.getWidth() ) ), (float) edgeBounds.getMinY() );
            path.lineTo( (float) ( edgeBounds.getMinX() + ( column * cellSize.getWidth() ) ), (float) edgeBounds.getMaxY() );
            PPath lineNode = new PPath( path );
            lineNode.setStroke( stroke );
            lineNode.setStrokePaint( strokePaint );
            addChild( lineNode );
        }
    }
}