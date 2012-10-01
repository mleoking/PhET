package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;

/**
 * Flag that appears at the top of the screen when one team has won the tug-of-war.  It waves in the wind and says "Red Wins" or "Blue Wins"
 *
 * @author Sam Reid
 */
class FlagNode extends PNode {

    private final Timer timer;

    public FlagNode( Color color, final String text ) {
        final PhetPPath p = new PhetPPath( createPath(), color, new BasicStroke( 2 ), Color.black );
        addChild( p );
        final PhetPText textNode = new PhetPText( text, new PhetFont( 32, true ) ) {{
            setTextPaint( Color.white );
        }};

        //Shrink to fit within the flag
        double width = textNode.getFullWidth();
        if ( width > 165 ) { textNode.scale( 165.0 / width ); }
        textNode.centerFullBoundsOnPoint( p.getFullBounds().getCenterX(), p.getFullBounds().getCenterY() );
        addChild( textNode );

        timer = new Timer( 20, new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                p.setPathTo( createPath() );
            }
        } );
        timer.start();
    }

    private GeneralPath createPath() {
        double time = System.currentTimeMillis() / 1000.0;
        GeneralPath path = new GeneralPath();
        final int maxX = 175;
        final float maxY = (float) ( 75 );
        final float dy = (float) ( 7 * Math.sin( time * 6 ) );
        final float dx = (float) ( 2 * Math.sin( time * 5 ) ) + 10;
        path.moveTo( 0, 0 );
        path.curveTo( maxX / 3 + dx, 25 + dy, 2 * maxX / 3 + dx, -25 - dy, maxX + dx, dy / 2 );
        path.lineTo( maxX + dx, maxY + dy / 2 );
        path.curveTo( 2 * maxX / 3 + dx, -25 + maxY - dy, maxX / 3 + dx, 25 + maxY + dy, 0, maxY );
        path.lineTo( 0, 0 );
        path.closePath();
        return path;
    }

    public void dispose() { timer.stop(); }
}