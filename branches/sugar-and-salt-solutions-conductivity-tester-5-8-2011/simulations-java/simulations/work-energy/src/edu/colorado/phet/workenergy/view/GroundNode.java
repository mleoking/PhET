// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class GroundNode extends PNode {
    public GroundNode( ModelViewTransform2D transform2D ) {
        int groundHeight = 1000;
        int groundWidth = 1000;
        Color topColor = new Color( 144, 199, 86 );
        Color bottomColor = new Color( 103, 162, 87 );
        double yBottom = transform2D.modelToViewYDouble( -2 );//fade color 1 meter down
        double yTop = transform2D.modelToViewYDouble( 0 );//fade color 1 meter down
        final Shape viewShape = transform2D.createTransformedShape( new Rectangle2D.Double( -groundWidth, -groundHeight, groundWidth * 2, groundHeight ) );
        PhetPPath path = new PhetPPath( viewShape, new GradientPaint( 0, (float) yTop, topColor, 0, (float) yBottom, bottomColor ), new BasicStroke( 1 ), Color.gray );
        addChild( path );
    }
}
