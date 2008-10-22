package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Jun 26, 2008 at 8:55:09 AM
 */
public class PNodeComponent extends BufferedPhetPCanvas {
    private PNode node;

    public PNodeComponent( PNode node ) {
        this.node = node;
        addScreenChild( node );
        node.setOffset( 2, 3 );
        updatePreferredSize();
    }

    public void updatePreferredSize() {
        setPreferredSize( new Dimension( (int) node.getFullBounds().getWidth() + 10, (int) node.getFullBounds().getHeight() + 4 ) );
    }
}
