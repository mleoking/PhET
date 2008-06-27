package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Jun 26, 2008 at 8:55:09 AM
 */
public class PNodeComponent extends PhetPCanvas {
    public PNodeComponent( PNode node ) {
        addScreenChild( node );
        node.setOffset( 2, 3 );
        setPreferredSize( new Dimension( (int) node.getFullBounds().getWidth() + 4, (int) node.getFullBounds().getHeight() + 4 ) );
    }
}
