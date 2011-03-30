// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.view;

import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.BuildAMoleculeResources;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

public class LargerMoleculesCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    // TODO: model

    // View
    private final PNode _rootNode;

    // Model-View transform.
    private final ModelViewTransform mvt;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public LargerMoleculesCanvas() {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, BuildAMoleculeConstants.DEFAULT_STAGE_SIZE ) );

        // Set up the model-canvas transform.  IMPORTANT NOTES: The multiplier
        // factors for the point in the view can be adjusted to shift the
        // center right or left, and the scale factor can be adjusted to zoom
        // in or out (smaller numbers zoom out, larger ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAMoleculeConstants.DEFAULT_STAGE_SIZE.width * 0.32 ),
                        (int) Math.round( BuildAMoleculeConstants.DEFAULT_STAGE_SIZE.height * 0.49 ) ),
                2.0 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        setBackground( BuildAMoleculeConstants.CANVAS_BACKGROUND_COLOR );

        // TODO: Temp - add an image that represents the tab.
        PNode tempImage = new PImage( BuildAMoleculeResources.getImage( "tab-3-temp-sketch.png" ));
        addWorldChild( tempImage );

        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------

    /*
    * Updates the layout of stuff on the canvas.
    */
    @Override
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }

        //XXX lay out nodes
    }
}
