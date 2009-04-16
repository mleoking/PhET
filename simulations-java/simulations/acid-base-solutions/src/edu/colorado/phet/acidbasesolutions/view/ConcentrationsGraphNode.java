package edu.colorado.phet.acidbasesolutions.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;


public class ConcentrationsGraphNode extends PhetPNode {

    public ConcentrationsGraphNode() {
        super();
        
        //XXX placeholder
        PPath outlineNode = new PPath();
        outlineNode.setPathTo( new Rectangle2D.Double( 0, 0, 400, 600 ) );
        outlineNode.setStroke( new BasicStroke( 2f ) );
        outlineNode.setStrokePaint( Color.BLACK );
        addChild( outlineNode );
        
        //XXX placeholder
        PText textNode = new PText( "Concentration Graph lives here" );
        textNode.setFont( new PhetFont( 16 ) );
        addChild( textNode );
        textNode.setOffset( ( outlineNode.getWidth() - textNode.getWidth() ) / 2, 50 );
    }
}
