package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;

import java.awt.*;

/**
 * Author: Sam Reid
 * Jul 19, 2007, 4:46:07 PM
 */
//todo: cache for performance reasons?
public class ShadowJLabel extends PhetPCanvas {
    public ShadowJLabel( String text, Color foreground, Font font ) {
        ShadowPText shadowPText = new ShadowPText( text );
        shadowPText.setTextPaint( foreground );
        shadowPText.setFont( font );
        getLayer().addChild( shadowPText );
        setPreferredSize( new Dimension( (int)shadowPText.getFullBounds().getWidth() + 1, (int)shadowPText.getFullBounds().getHeight() + 1 ) );
        setOpaque( false );
        setBackground( null );
        setBorder( null );
    }
}
