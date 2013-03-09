package edu.colorado.phet.forcesandmotionbasics.touch;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;

public class TextWithBackground extends PNode {

    private final PhetPPath backgroundNode;
    private final PhetPText textNode;

    public TextWithBackground( String text, PhetFont font, Color textColor ) {
        backgroundNode = new PhetPPath( new RoundRectangle2D.Double( 0, 0, 400, 100, 10, 10 ), Color.white );
        addChild( backgroundNode );
        textNode = new PhetPText( text, font, textColor );
        addChild( textNode );
//        textNode.centerFullBoundsOnPoint( backgroundNode.getFullBounds().getCenter2D() );
        setText( text );
    }

    public String getText() {
        return textNode.getText();
    }

    public void setText( String text ) {
        textNode.setText( text );
        double dw = 6;
        double dy = 2;
        backgroundNode.setPathTo( new RoundRectangle2D.Double( -dw, -dy, textNode.getFullWidth() + dw * 2, textNode.getFullHeight() + dy * 2, 10, 10 ) );
        textNode.centerFullBoundsOnPoint( backgroundNode.getFullBounds().getCenter2D() );
    }
}
