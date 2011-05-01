// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class SkyNode extends PNode {
    public SkyNode( ModelViewTransform transform2D ) {
        int skyHeight = 1000;
        int skyWidth = 1000;
        Color topColor = new Color( 1, 172, 228 );
        Color bottomColor = new Color( 208, 236, 251 );
        double yBottom = transform2D.modelToViewY( 0 );
        double yTop = transform2D.modelToViewY( 11 );
        final Shape viewShape = transform2D.modelToView( new Rectangle2D.Double( -skyWidth, 0, skyWidth * 2, skyHeight ) );
        PhetPPath path = new PhetPPath( viewShape, new GradientPaint( 0, (float) yTop, topColor, 0, (float) yBottom, bottomColor ) );
        addChild( path );
    }
}
