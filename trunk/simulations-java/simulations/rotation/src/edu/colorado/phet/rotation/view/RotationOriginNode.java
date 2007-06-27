package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.util.UnicodeUtil;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Author: Sam Reid
 * May 27, 2007, 12:56:07 AM
 */
public class RotationOriginNode extends PNode {

    public RotationOriginNode( RotationPlatform rotationPlatform ) {
        PhetPPath path = new PhetPPath( new Line2D.Double( 0, 0, 100, 0 ), new BasicStroke( 2.0f ), Color.black );
        double offsetX = 20;
        addChild( path );

        HTMLNode htmlNode = new HTMLNode( "<html>" + UnicodeUtil.THETA + "=0<sup>o</sup></html>" );
        htmlNode.setFont( new PhetDefaultFont( 18, true ) );
        addChild( htmlNode );

        translate( rotationPlatform.getCenter().getX() + rotationPlatform.getRadius() + offsetX, rotationPlatform.getCenter().getY() );
    }
}
