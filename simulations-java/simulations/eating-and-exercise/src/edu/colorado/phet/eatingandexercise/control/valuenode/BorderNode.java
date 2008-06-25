package edu.colorado.phet.eatingandexercise.control.valuenode;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Jun 24, 2008 at 8:46:37 PM
 */
public class BorderNode extends PNode {
    private PhetPPath borderPath;
    private PNode child;

    public BorderNode( PNode child ) {
        this.child = child;
        addChild( child );
        borderPath = new PhetPPath( new BasicStroke( 1 ), Color.black );
        addChild( borderPath );
        relayout();
    }

    private void relayout() {
        borderPath.setPathTo( child.getFullBounds() );
    }

}
