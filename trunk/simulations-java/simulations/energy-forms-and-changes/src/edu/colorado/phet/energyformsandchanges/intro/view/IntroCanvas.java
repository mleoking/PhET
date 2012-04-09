// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.energyformsandchanges.intro.model.IntroModel;
import edu.colorado.phet.energyformsandchanges.intro.model.Shelf;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.piccolophet.PhetPCanvas.CenteredStage.DEFAULT_STAGE_SIZE;

/**
 * Piccolo canvas for the "Intro" tab of the Energy Forms and Changes
 * simulation.
 *
 * @author John Blanco
 */
public class IntroCanvas extends PhetPCanvas {
    public IntroCanvas( IntroModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this ) );

        // Set up the model-canvas transform.
        //
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( DEFAULT_STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( DEFAULT_STAGE_SIZE.getHeight() * 0.93 ) ),
                2500 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        setBackground( new Color( 245, 246, 247 ) );

        // Set up a root node for our scene graph.
        final PNode rootNode = new PNode();
        addWorldChild( rootNode );

        // Add the shelves.
        for ( Shelf shelf : model.getShelfList() ) {
            rootNode.addChild( new ShelfNode( shelf, mvt ) );
        }

        // Add the burners.
        rootNode.addChild( new BurnerNode( model.getLeftBurner(), mvt ) );
        rootNode.addChild( new BurnerNode( model.getRightBurner(), mvt ) );

        // Add the movable objects.
        rootNode.addChild( new CubeNode( model.getBrick(), mvt ) );
        rootNode.addChild( new CubeNode( model.getLeadBlock(), mvt ) );
    }
}
