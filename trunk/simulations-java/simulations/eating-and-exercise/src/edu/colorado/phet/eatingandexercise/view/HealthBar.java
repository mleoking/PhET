package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Jun 27, 2008 at 8:37:27 AM
 */
public class HealthBar extends PNode {
    public HealthBar( String name ) {
        PhetPPath boundary = new PhetPPath( new Rectangle2D.Double( 0, 0, 20, 100 ), new BasicStroke( 1 ), Color.black );
        addChild( boundary );

        HTMLNode label = new HTMLNode( name );
        addChild( label );
        label.rotate( -Math.PI / 2 );
        label.translate( -label.getFullBounds().getHeight() - boundary.getFullBounds().getHeight() - 4, 0 );
    }
}
