// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;

import edu.colorado.phet.balanceandtorque.teetertotter.model.Plank;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Graphic for the fulcrum, a triangle that the plank pivots about.
 *
 * @author Sam Reid
 */
public class PlankNode extends ModelObjectNode {
    public PlankNode( final ModelViewTransform mvt, final Plank plank ) {
        super( mvt, plank, new Color( 243, 203, 127 ) );
        final PNode tickMarkLayer = new PNode();
        addChild( tickMarkLayer );
        plank.getShapeProperty().addObserver( new VoidFunction1<Shape>() {
            public void apply( Shape rotatedPlankShape ) {
                // Remove existing tick marks.
                tickMarkLayer.removeAllChildren();
                tickMarkLayer.setRotation( 0 );
                // Add the tick marks.  The spacing should match that of the
                // plank's "snap to" locations.  The marks are created based
                // on the unrotated plank, and then rotated to match the
                // current orientation.
                double interMarkerDistance = mvt.modelToViewDeltaX( plank.LENGTH / (double) ( Plank.NUM_SNAP_TO_LOCATIONS + 1 ) );
                double markerXPos = mvt.modelToViewX( plank.getUnrotatedShape().getBounds2D().getMinX() ) + interMarkerDistance;
                double markerYPos = mvt.modelToViewY( plank.getUnrotatedShape().getBounds2D().getMinY() );
                double plankThickness = mvt.modelToViewDeltaY( plank.THICKNESS );
                DoubleGeneralPath tickMarkPath = new DoubleGeneralPath();
                for ( int i = 0; i < plank.NUM_SNAP_TO_LOCATIONS; i++ ) {
                    if ( !( i == plank.NUM_SNAP_TO_LOCATIONS / 2 ) ) {   // No marker in center of plank.
                        tickMarkPath.reset();
                        tickMarkPath.moveTo( markerXPos, markerYPos );
                        tickMarkPath.lineTo( markerXPos, markerYPos + plankThickness );
                        PNode tickMark = new PhetPPath( tickMarkPath.getGeneralPath(), new BasicStroke( 1 ), Color.BLACK );
                        tickMarkLayer.addChild( tickMark );
                    }
                    markerXPos += interMarkerDistance;
                }
                tickMarkLayer.rotateAboutPoint( -plank.getTiltAngle(), mvt.modelToView( plank.getPivotPoint() ) );
            }
        } );
    }
}
