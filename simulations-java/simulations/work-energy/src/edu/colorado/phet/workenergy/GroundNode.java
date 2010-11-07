package edu.colorado.phet.workenergy;

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
        double dy = Math.abs( transform2D.modelToViewDifferentialYDouble( 1 ) );//fade color 1 meter down
        PhetPPath path = new PhetPPath( transform2D.createTransformedShape( new Rectangle2D.Double( -groundWidth, -groundHeight, groundWidth * 2, groundHeight ) ),
                                        new GradientPaint( 0, 0, topColor, 0, (float) dy, bottomColor ), new BasicStroke( 1.6f ), Color.gray );
        addChild( path );
    }
}
