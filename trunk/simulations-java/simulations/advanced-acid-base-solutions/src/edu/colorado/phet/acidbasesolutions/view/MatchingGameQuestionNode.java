package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;


public class MatchingGameQuestionNode extends PhetPNode {
    
    private static final Color COLOR = Color.RED;
    private static final Font FONT = new PhetFont( Font.BOLD, 32 );
    
    private final HTMLNode htmlNode;
    
    public MatchingGameQuestionNode( String text ) {
        super();
        htmlNode = new HTMLNode( text );
        addChild( htmlNode );
        htmlNode.setHTMLColor( COLOR );
        htmlNode.setFont( FONT );
    }
}
