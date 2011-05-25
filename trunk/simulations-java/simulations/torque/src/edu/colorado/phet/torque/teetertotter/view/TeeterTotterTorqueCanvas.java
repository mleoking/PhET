// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
import edu.colorado.phet.torque.teetertotter.model.TeeterTotterTorqueModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author John Blanco
 */
public class TeeterTotterTorqueCanvas extends PhetPCanvas {

    private static Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );
    private final ModelViewTransform mvt;

    public TeeterTotterTorqueCanvas( TeeterTotterTorqueModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        // Set up the model-canvas transform.  The test chamber is centered
        // at (0, 0) in model space, and this transform is set up to place
        // the chamber where we want it on the canvas.
        //
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.295 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.82 ) ),
                100 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Set up a root node for our scene graph.
        PNode rootNode = new PNode();
        addWorldChild( rootNode );

        // Add the background that consists of the ground and sky.
        rootNode.addChild( new OutsideBackgroundNode( mvt, 3, 1 ) );
    }
}
