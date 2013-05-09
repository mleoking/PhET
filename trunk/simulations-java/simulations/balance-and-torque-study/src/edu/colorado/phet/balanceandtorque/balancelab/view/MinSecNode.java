package edu.colorado.phet.balanceandtorque.balancelab.view;

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
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Node that displays a time in terms of minutes and seconds.
 *
 * @author John Blanco
 */
public class MinSecNode extends PNode {

    private static final Font FONT = new PhetFont( 60, true );
    private static final Format SECONDS_FORMATTER = new DefaultDecimalFormat( "00" );
    private static final Color COLOR = Color.YELLOW;
    private static final Dimension2D SIZE = new PDimension( 150, 80 );

    public MinSecNode( Property<Integer> seconds ) {
        final PNode enclosingBox = new PhetPPath( new Rectangle2D.Double( 0, 0, SIZE.getWidth(), SIZE.getHeight() ),
                                            new BasicStroke(1),
                                            COLOR );
        addChild( enclosingBox );

        seconds.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer secondsValue ) {
                int minutes = secondsValue / 60;
                int seconds = secondsValue - ( minutes * 60 );
                enclosingBox.removeAllChildren();
                PNode readoutText = new PhetPText( minutes + ":" + SECONDS_FORMATTER.format( seconds ), FONT, COLOR );

                readoutText.centerFullBoundsOnPoint( SIZE.getWidth() / 2, SIZE.getHeight() / 2 );
                enclosingBox.addChild( readoutText );
            }
        } );
    }
}
