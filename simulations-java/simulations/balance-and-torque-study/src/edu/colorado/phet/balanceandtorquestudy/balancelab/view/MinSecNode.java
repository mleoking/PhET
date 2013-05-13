package edu.colorado.phet.balanceandtorquestudy.balancelab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.text.Format;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Node that displays a time in terms of minutes and seconds.
 *
 * @author John Blanco
 */
public class MinSecNode extends PNode {

    private static final Font FONT = new PhetFont( 50, true );
    private static final Format SECONDS_FORMATTER = new DefaultDecimalFormat( "00" );
    private static final Color COLOR = Color.YELLOW;

    public MinSecNode( Property<Integer> seconds ) {
        final PPath enclosingBox = new PhetPPath( new BasicStroke( 1 ), COLOR );
        addChild( enclosingBox );
        seconds.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer secondsValue ) {
                int minutes = secondsValue / 60;
                int seconds = secondsValue - ( minutes * 60 );
                PNode readoutText = new PhetPText( minutes + ":" + SECONDS_FORMATTER.format( seconds ), FONT, COLOR );
                enclosingBox.removeAllChildren();
                Dimension2D enclosingBoxSize = new PDimension( readoutText.getFullBoundsReference().width * 1.1, readoutText.getFullBoundsReference().height * 1.02 );
                enclosingBox.setPathTo( new Rectangle2D.Double( 0, 0, enclosingBoxSize.getWidth(), enclosingBoxSize.getHeight() ) );
                readoutText.centerFullBoundsOnPoint( enclosingBoxSize.getWidth() / 2, enclosingBoxSize.getHeight() / 2 );
                enclosingBox.addChild( readoutText );
            }
        } );
    }
}
