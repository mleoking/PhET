/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;

/**
 * Displays instructions on what to do with the game.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameInstructionsNode extends HTMLNode {
    
    private static final PhetFont FONT = new PhetFont( Font.BOLD, 36 );
    private static final Color COLOR = Color.YELLOW;
    
    public GameInstructionsNode( String html ) {
        super( html );
        setHTMLColor( COLOR );
        setFont( FONT );
    }
    
    public void setText( String html ) {
        setHTML( html );
    }
}
