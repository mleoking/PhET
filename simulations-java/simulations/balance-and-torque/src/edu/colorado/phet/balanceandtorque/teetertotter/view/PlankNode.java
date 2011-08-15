// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;

import edu.colorado.phet.balanceandtorque.teetertotter.model.Plank;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Graphic for the fulcrum, a triangle that the plank pivots about.
 *
 * @author Sam Reid
 */
public class PlankNode extends ModelObjectNode {
    private static final Stroke NORMAL_TICK_MARK_STROKE = new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
    private static final Stroke BOLD_TICK_MARK_STROKE = new BasicStroke( 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );

    public PlankNode( final ModelViewTransform mvt, final Plank plank ) {
        super( mvt, plank, new Color( 243, 203, 127 ) );
        final PNode tickMarkLayer = new PNode();
        addChild( tickMarkLayer );
        plank.addShapeObserver( new VoidFunction1<Shape>() {
            public void apply( Shape rotatedPlankShape ) {
                // Remove existing tick marks.
                tickMarkLayer.removeAllChildren();
                // Add the tick marks.  The spacing should match that of the
                // plank's "snap to" locations.  The marks are created based
                // on the unrotated plank, and then rotated to match the
                // current orientation.
                for ( int i = 0; i < plank.getTickMarks().size(); i++ ) {
                    if ( i % 4 == 0 ) {
                        // Make some marks bold for easier placement of masses.
                        // The 'if' clause can be tweaked to put marks in
                        // different places.
                        tickMarkLayer.addChild( new PhetPPath( mvt.modelToView( plank.getTickMarks().get( i ) ), BOLD_TICK_MARK_STROKE, Color.BLACK ) );
                    }
                    else {
                        // Use the normal stroke.
                        tickMarkLayer.addChild( new PhetPPath( mvt.modelToView( plank.getTickMarks().get( i ) ), NORMAL_TICK_MARK_STROKE, Color.BLACK ) );
                    }
                }
            }
        } );
    }
}
