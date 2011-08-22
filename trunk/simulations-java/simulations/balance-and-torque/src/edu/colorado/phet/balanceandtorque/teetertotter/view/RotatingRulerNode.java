// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.teetertotter.model.Plank;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.umd.cs.piccolo.PNode;

/**
 * This class represents a ruler that sits on the bottom of the plank and
 * rotates as the plank rotates.
 *
 * @author John Blanco
 */
public class RotatingRulerNode extends PNode {

    public RotatingRulerNode( final Plank plank, final ModelViewTransform mvt, BooleanProperty visibleProperty ) {
        final RulerNode rulerNode = new RulerNode( mvt.modelToViewDeltaX( Plank.LENGTH - 0.5 ),
                                                   50,
                                                   new String[] { "2", "1.75", "1.5", "1.25", "1", "0.75", "0.5", "0.25", "0", "0.25", "0.5", "0.75", "1", "1.25", "1.5", "1.75", "2" },
                                                   "m",
                                                   0,
                                                   12 );
        rulerNode.setBackgroundPaint( new Color( 236, 225, 113, 100 ) );
        addChild( rulerNode );
        // Observe visibility property.
        visibleProperty.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );
        // Position the ruler so that it appears to be stuck to the bottom of
        // the plank, and rotates as the plank rotates.
        plank.bottomCenterPoint.addObserver( new VoidFunction1<Point2D>() {
            public void apply( Point2D centerBottomOfPlank ) {
                rulerNode.setRotation( 0 );
                centerFullBoundsOnPoint( mvt.modelToViewX( centerBottomOfPlank.getX() ),
                                         mvt.modelToViewY( centerBottomOfPlank.getY() ) + getFullBoundsReference().height / 2 );
                rulerNode.rotateAboutPoint( -plank.getTiltAngle(), rulerNode.getFullBoundsReference().getWidth() / 2, 0 );
            }
        } );
    }
}
