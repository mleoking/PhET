// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import java.awt.*;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * The normal line is a graphic that indicates the point of intersection of the light ray and the perpendicular angle at the interface.
 *
 * @author Sam Reid
 */
public class NormalLine extends PNode {
    public NormalLine( ModelViewTransform transform, double modelHeight ) {
        this( transform, modelHeight, 1, 10, 10 );
    }

    public NormalLine( ModelViewTransform transform, double modelHeight, float strokeWidth, float on, float off ) {
        //Normal Line
        double x = transform.modelToViewX( 0 );
        double y1 = transform.modelToViewY( 0 - modelHeight / 4 );
        double y2 = transform.modelToViewY( 0 + modelHeight / 4 );
        addChild( new PhetPPath( new Line2D.Double( x, y1, x, y2 ), new BasicStroke( strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { on, off }, 0 ), Color.black ) );
    }
}
