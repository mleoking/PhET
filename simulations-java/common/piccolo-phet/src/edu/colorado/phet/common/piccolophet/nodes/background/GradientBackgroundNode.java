// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.background;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for nodes that are used as the background on a tab and that have
 * some sort of gradient to it.  Example include ground and sky.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class GradientBackgroundNode extends PNode {
    public GradientBackgroundNode( ModelViewTransform mvt, Rectangle2D modelRect, Color color1, Color color2, double y1, double y2  ) {
        Shape viewShape = mvt.modelToView( modelRect );
        float centerX = (float) viewShape.getBounds2D().getCenterX();
        GradientPaint gradientPaint = new GradientPaint( centerX,
                                                         (float)mvt.modelToViewY( y1 ),
                                                         color1,
                                                         centerX,
                                                         (float)mvt.modelToViewY( y2 ),
                                                         color2 );
        PhetPPath path = new PhetPPath( viewShape, gradientPaint );
        addChild( path );
    }
}
