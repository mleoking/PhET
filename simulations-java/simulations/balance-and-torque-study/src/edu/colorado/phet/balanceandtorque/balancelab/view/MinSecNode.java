package edu.colorado.phet.balanceandtorque.balancelab.view;

import java.awt.Color;
import java.awt.Font;
import java.text.Format;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that displays a time in terms of minutes and seconds.
 *
 * @author John Blanco
 */
public class MinSecNode extends PNode {

    private Font FONT = new PhetFont( 70, true );
    private Format SECONDS_FORMATTER = new DefaultDecimalFormat( "00" );

    public MinSecNode( Property<Integer> seconds ) {
        seconds.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer secondsValue ) {
                int minutes = secondsValue / 60;
                int seconds = secondsValue - ( minutes * 60 );
                removeAllChildren();
                addChild( new PhetPText( minutes + ":" + SECONDS_FORMATTER.format( seconds ), FONT, Color.WHITE ) );
            }
        } );
    }
}
