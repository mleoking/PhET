// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;

/**
 * Node for displaying messages in the game.
 * Message can be in HTML or plain text format.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameMessageNode extends PhetPNode {
    
    private final HTMLNode htmlNode; // use composition instead of inheritance to keep the interface sparse
    
    public GameMessageNode( String message, Color color, int pointSize ) {
        super();
        htmlNode = new HTMLNode( HTMLUtils.toHTMLString( message ) );
        htmlNode.setHTMLColor( color );
        htmlNode.setFont( new PhetFont( pointSize ) );
        addChild( htmlNode );
    }
    
    public void setText( String message ) {
        htmlNode.setHTML( HTMLUtils.toHTMLString( message ) );
    }

}
