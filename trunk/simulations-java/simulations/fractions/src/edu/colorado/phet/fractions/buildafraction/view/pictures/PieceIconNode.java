// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.pictures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractions.buildafraction.view.pictures.SimpleContainerNode.rectangleHeight;
import static edu.colorado.phet.fractions.buildafraction.view.pictures.SimpleContainerNode.rectangleWidth;

/**
 * Shows an icon on the bucket with a single slice in the context of its container.
 *
 * @author Sam Reid
 */
public class PieceIconNode extends PNode {

    public static final double TINY_SCALE = 0.43;

    public PieceIconNode( int divisions ) {
        for ( int i = 0; i < divisions; i++ ) {
            final double pieceWidth = rectangleWidth / divisions;
            addChild( new PhetPPath( new Rectangle2D.Double( pieceWidth * i, 0, pieceWidth, rectangleHeight ), Color.white, new BasicStroke( 1 ), Color.black ) );
        }
        //Thicker outer stroke
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, rectangleWidth, rectangleHeight ), new BasicStroke( 2 ), Color.black ) );

        scale( TINY_SCALE );
    }
}