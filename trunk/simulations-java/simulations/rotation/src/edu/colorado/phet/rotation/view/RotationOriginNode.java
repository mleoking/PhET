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

    public static final double AXIS_LENGTH = 30 * RotationPlayAreaNode.SCALE;

    public RotationOriginNode( RotationPlatform rotationPlatform ) {
        PhetPPath path = new PhetPPath( new Line2D.Double( 0, 0, AXIS_LENGTH, 0 ), new BasicStroke( (float)( 2 * RotationPlayAreaNode.SCALE ) ), Color.black );
        double offsetX = 20 * RotationPlayAreaNode.SCALE;
        addChild( path );

        HTMLNode htmlNode = new HTMLNode( "<html>" + UnicodeUtil.THETA + "=0<sup>o</sup></html>" );
        htmlNode.setFont( new PhetDefaultFont( 16, true ) );
        htmlNode.scale( RotationPlayAreaNode.SCALE );
        addChild( htmlNode );

        translate( rotationPlatform.getCenter().getX() + rotationPlatform.getRadius() + offsetX, rotationPlatform.getCenter().getY() );
    }
}
