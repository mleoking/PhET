// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.watertower.model.Hose;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that shows the hose and allows the user to change the angle that water is sprayed out
 *
 * @author Sam Reid
 */
public class HoseNode extends PNode {
    public HoseNode( final ModelViewTransform transform, final Hose hose ) {
        addChild( new PhetPPath() );

        final double height = Math.abs( transform.modelToViewDeltaY( hose.attachmentWidth ) );
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, 10, height ), Color.green ) {{
            hose.attachmentPoint.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D point ) {
                    final Point2D.Double point1 = transform.modelToView( point ).toPoint2D();
                    setOffset( point1.getX(), point1.getY() - height );
                }
            } );
        }} );

        hose.enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );
    }
}