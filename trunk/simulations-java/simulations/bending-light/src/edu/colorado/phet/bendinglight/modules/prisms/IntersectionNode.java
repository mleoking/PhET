// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.*;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * PNode that depicts a the normal vector at a light-ray / medium interface intersection.
 *
 * @author Sam Reid
 */
public class IntersectionNode extends PNode {
    public IntersectionNode( final ModelViewTransform transform, final Intersection intersection ) {
        ImmutableVector2D center = transform.modelToView( intersection.getPoint() );
        ImmutableVector2D norm = transform.modelToViewDelta( intersection.getUnitNormal() ).getNormalizedInstance();
        int normalLength = 100;
        addChild( new PhetPPath( new Line2D.Double( center.plus( norm.times( normalLength / 2 ) ).toPoint2D(),
                                                    center.plus( norm.times( -normalLength / 2 ) ).toPoint2D() ),
                                 new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[] { 10, 5 }, 0 ), Color.darkGray ) {{
        }} );
    }
}
