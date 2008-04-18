package edu.colorado.phet.fitness.control;

import javax.swing.*;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Created by: Sam
 * Apr 17, 2008 at 10:22:29 PM
 */
public class DietNode extends PNode {
    public DietNode() {
        PText text = new PText( "Balanced Diet" );
        PSwing pSwing = new PSwing( new JButton( "Edit Diet" ) );
        addChild( text );
        addChild( pSwing );
        pSwing.setOffset( 0, text.getFullBounds().getMaxY() );
    }
}
