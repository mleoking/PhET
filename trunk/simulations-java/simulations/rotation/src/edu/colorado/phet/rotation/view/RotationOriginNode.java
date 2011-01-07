// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.model.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.util.UnicodeUtil;
import edu.umd.cs.piccolo.PNode;

/**
 * Author: Sam Reid
 * May 27, 2007, 12:56:07 AM
 */
public class RotationOriginNode extends PNode {

    public static final double AXIS_LENGTH = 30 * RotationPlayAreaNode.SCALE;
    private HTMLNode htmlNode;
    private AngleUnitModel angleUnitModel;

    public RotationOriginNode( RotationPlatform rotationPlatform, AngleUnitModel angleUnitModel ) {
        this.angleUnitModel = angleUnitModel;
        PhetPPath path = new PhetPPath( new Line2D.Double( 0, 0, AXIS_LENGTH, 0 ), new BasicStroke( (float) ( 2 * RotationPlayAreaNode.SCALE ) ), Color.black );
        double offsetX = 20 * RotationPlayAreaNode.SCALE;
        addChild( path );

        htmlNode = new HTMLNode( "<html>" + UnicodeUtil.THETA + "=0<sup>o</sup></html>" );
        htmlNode.setFont( new PhetFont( 16, true ) );
        htmlNode.setTransform( AffineTransform.getScaleInstance( RotationPlayAreaNode.SCALE, -RotationPlayAreaNode.SCALE ) );
        addChild( htmlNode );

        translate( rotationPlatform.getCenter().getX() + rotationPlatform.getRadius() + offsetX, rotationPlatform.getCenter().getY() );
        angleUnitModel.addListener( new AngleUnitModel.Listener() {
            public void changed() {
                update();
            }
        } );
        update();
    }

    private void update() {
        htmlNode.setHTML( angleUnitModel.isRadians() ? UnicodeUtil.THETA + "=0 radians" :
                          "<html>" + UnicodeUtil.THETA + "=0<sup>o</sup></html>" );
    }
}
