package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class FlagNode extends PNode {
    public FlagNode( Color color, final String text ) {
        GeneralPath path = new GeneralPath();
        final int maxX = 175;
        final int maxY = 75;
        path.moveTo( 0, 0 );
        path.curveTo( maxX / 3, 25, 2 * maxX / 3, -25, maxX, 0 );
        path.lineTo( maxX, maxY );
        path.curveTo( 2 * maxX / 3, -25 + maxY, maxX / 3, 25 + maxY, 0, maxY );
        path.lineTo( 0, 0 );
        path.closePath();
        final PhetPPath p = new PhetPPath( path, color, new BasicStroke( 2 ), Color.black );
        addChild( p );
        addChild( new PhetPText( text, new PhetFont( 32, true ) ) {{
            setTextPaint( Color.white );
            centerFullBoundsOnPoint( p.getFullBounds().getCenterX(), p.getFullBounds().getCenterY() );
        }} );
    }
}