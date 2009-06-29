package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;


public class MatchingGameAnswerNode extends PhetPNode {
    
    private static final Color COLOR = Color.RED;
    private static final Font FONT = new PhetFont( Font.BOLD, 32 );
    
    private final HTMLNode htmlNode;
    
    public MatchingGameAnswerNode( String text ) {
        super();
        htmlNode = new HTMLNode( text );
        addChild( htmlNode );
        htmlNode.setHTMLColor( COLOR );
        htmlNode.setFont( FONT );
    }
    
    public void setText( String text ) {
        htmlNode.setHTML( HTMLUtils.toHTMLString( text ) );
    }
}
