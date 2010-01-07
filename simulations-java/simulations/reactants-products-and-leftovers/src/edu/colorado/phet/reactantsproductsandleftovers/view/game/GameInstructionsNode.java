/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolox.nodes.PComposite;


public class GameInstructionsNode extends PComposite {
    
    private static final boolean SHOW_ARROW = false;
    
    private static final PhetFont FONT = new PhetFont( Font.BOLD, 36 );
    private static final Color COLOR = Color.YELLOW;
    
    private final HTMLNode htmlNode;
    private final DownArrowNode arrowNode;
    
    public GameInstructionsNode( String html ) {
        super();
        
        htmlNode = new HTMLNode( html, FONT, COLOR );
        addChild( htmlNode );
        
        arrowNode = new DownArrowNode( 80 );
        if ( SHOW_ARROW ) {
            addChild( arrowNode );
        }
        
        updateLayout();
    }
    
    public void setText( String html ) {
        htmlNode.setHTML( html );
        updateLayout();
    }
    
    private void updateLayout() {
        // layout, origin at upper left of text
        double x = 0;
        double y = 0;
        htmlNode.setOffset( 0, 0 );
        x = htmlNode.getFullBounds().getCenterX();
        y = htmlNode.getFullBounds().getMaxY() + 10;
        arrowNode.setOffset( x, y );
    }
}
