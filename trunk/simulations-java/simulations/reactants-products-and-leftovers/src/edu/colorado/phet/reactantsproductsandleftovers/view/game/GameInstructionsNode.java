package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolox.nodes.PComposite;


public class GameInstructionsNode extends PComposite {
    
    private static final PhetFont FONT = new PhetFont( 32 );
    private static final Color COLOR = Color.YELLOW;
    
    public GameInstructionsNode( String html ) {
        super();
        
        HTMLNode htmlNode = new HTMLNode( html, FONT, COLOR );
        addChild( htmlNode );
        
        DownArrowNode arrowNode = new DownArrowNode( 80 );
        addChild( arrowNode );
        
        // layout, origin at upper left of text
        double x = 0;
        double y = 0;
        htmlNode.setOffset( 0, 0 );
        x = htmlNode.getFullBounds().getCenterX() - ( arrowNode.getFullBoundsReference().getWidth() / 2 );
        y = htmlNode.getFullBounds().getMaxY() + 10;
        arrowNode.setOffset( x, y );
    }
}
