// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.control.CollectionAreaNode;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.buckets.AtomModel;
import edu.colorado.phet.buildamolecule.model.buckets.Bucket;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

public class BuildAMoleculeCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    // TODO: model

    // View
    private final PNode _rootNode;

    // Model-View transform.
    private final ModelViewTransform mvt;

    private final PNode atomLayer = new PNode();

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BuildAMoleculeCanvas( Kit kit ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, BuildAMoleculeConstants.DEFAULT_STAGE_SIZE ) );

        // Set up the model-canvas transform.  IMPORTANT NOTES: The multiplier
        // factors for the point in the view can be adjusted to shift the
        // center right or left, and the scale factor can be adjusted to zoom
        // in or out (smaller numbers zoom out, larger ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAMoleculeConstants.DEFAULT_STAGE_SIZE.width * 0.5 ),
                           (int) Math.round( BuildAMoleculeConstants.DEFAULT_STAGE_SIZE.height * 0.5 ) ),
                0.6 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        setBackground( BuildAMoleculeConstants.CANVAS_BACKGROUND_COLOR );

//        // TODO: Temp - add an image that represents the tab.
//        PNode tempImage = new PImage( BuildAMoleculeResources.getImage( "tab-1-temp-sketch.png" ) );
//        addWorldChild( tempImage );

        CollectionAreaNode collectionAreaNode = new CollectionAreaNode() {{
            double collectionAreaPadding = 20;
            setOffset( BuildAMoleculeConstants.DEFAULT_STAGE_SIZE.width - getFullBounds().getWidth() - collectionAreaPadding, collectionAreaPadding );
        }};
        addWorldChild( collectionAreaNode );

        KitView kitView = new KitView( kit, mvt );
        for ( Bucket bucket : kit.getBuckets() ) {
            for ( AtomModel atom : bucket.getAtoms() ) {
                atomLayer.addChild( new AtomNode( mvt, atom ) );
            }
        }

        addWorldChild( kitView.getBottomLayer() );
        addWorldChild( atomLayer );
        addWorldChild( kitView.getTopLayer() );

//        PNode locationTestNode = new PhetPPath( new Rectangle2D.Double(-20, -20, 40, 40), Color.PINK );
//        locationTestNode.setOffset( mvt.modelToView( new Point2D.Double(0, 0) ) );
//        addWorldChild( locationTestNode );

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
