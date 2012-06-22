package edu.colorado.phet.buildafraction.view.pictures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows an icon on the bucket with a single slice in the context of its container.
 *
 * @author Sam Reid
 */
public class PieceIconNode extends PNode {

    public static final double TINY_SCALE = 0.43;

    public PieceIconNode( int divisions ) {
        for ( int i = 0; i < divisions; i++ ) {
            final double pieceWidth = SimpleContainerNode.width / divisions;
            addChild( new PhetPPath( new Rectangle2D.Double( pieceWidth * i, 0, pieceWidth, SimpleContainerNode.height ), i == 0 ? Color.red : Color.white, new BasicStroke( 1 ), Color.black ) );
        }
        //Thicker outer stroke
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, SimpleContainerNode.width, SimpleContainerNode.height ), new BasicStroke( 2 ), Color.black ) );

        scale( TINY_SCALE );
    }
}