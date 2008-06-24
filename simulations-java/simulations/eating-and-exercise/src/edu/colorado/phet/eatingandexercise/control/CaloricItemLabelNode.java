package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

public class CaloricItemLabelNode extends PNode {
    public CaloricItemLabelNode( String s ) {
        HTMLNode htmlNode = new HTMLNode( s );
        htmlNode.setFont( new PhetFont( 16, true ) );

        Rectangle2D rectangle2D = RectangleUtils.expandRectangle2D( htmlNode.getFullBounds(), 5, 5 );
        PhetPPath background = new PhetPPath( new RoundRectangle2D.Double( rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight(), 10, 10 ), new Color( 246, 239, 169 ) );

        addChild( background );
        addChild( htmlNode );
    }
}
