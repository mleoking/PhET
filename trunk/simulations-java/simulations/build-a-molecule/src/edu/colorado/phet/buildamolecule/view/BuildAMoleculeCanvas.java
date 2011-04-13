// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.control.CollectionAreaNode;
import edu.colorado.phet.buildamolecule.control.KitPanel;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.KitCollectionModel;
import edu.colorado.phet.buildamolecule.model.buckets.AtomModel;
import edu.colorado.phet.buildamolecule.model.buckets.Bucket;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

public class BuildAMoleculeCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private final Property<KitCollectionModel> modelProperty;
    private Map<Kit, KitView> kitMap = new HashMap<Kit, KitView>(); // so we can pull our kit view for a particular kit

    // TODO: (possible) separate canvases for different kits?

    // View
    private final PNode _rootNode;

    // Model-View transform.
    private final ModelViewTransform mvt;

    private final PNode bottomLayer = new PNode();
    private final PNode atomLayer = new PNode();
    private final PNode topLayer = new PNode();


    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BuildAMoleculeCanvas( KitCollectionModel initialModel, final boolean singleCollectionMode ) {

        modelProperty = new Property<KitCollectionModel>( initialModel );

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, BuildAMoleculeConstants.STAGE_SIZE ) );

        // Set up the model-canvas transform.  IMPORTANT NOTES: The multiplier
        // factors for the point in the view can be adjusted to shift the
        // center right or left, and the scale factor can be adjusted to zoom
        // in or out (smaller numbers zoom out, larger ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAMoleculeConstants.STAGE_SIZE.width * 0.5 ),
                           (int) Math.round( BuildAMoleculeConstants.STAGE_SIZE.height * 0.5 ) ),
                0.3 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        setBackground( BuildAMoleculeConstants.CANVAS_BACKGROUND_COLOR );

//        // TODO: Temp - add an image that represents the tab.
//        PNode tempImage = new PImage( BuildAMoleculeResources.getImage( "tab-1-temp-sketch.png" ) );
//        addWorldChild( tempImage );

        // TODO: make this so we can construct/destruct
        CollectionAreaNode collectionAreaNode = new CollectionAreaNode( getModel(), singleCollectionMode ) {{
            double collectionAreaPadding = 20;
            setOffset( BuildAMoleculeConstants.STAGE_SIZE.width - getFullBounds().getWidth() - collectionAreaPadding, collectionAreaPadding );
        }};
        addWorldChild( collectionAreaNode );

        addWorldChild( bottomLayer );
        addWorldChild( atomLayer );
        addWorldChild( topLayer );

//        PNode locationTestNode = new PhetPPath( new Rectangle2D.Double(-20, -20, 40, 40), Color.PINK );
//        locationTestNode.setOffset( mvt.modelToView( new Point2D.Double(0, 0) ) );
//        addWorldChild( locationTestNode );

        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );

        buildFromModel();
    }

    private void buildFromModel() {
        // bottom-most layer is our kit panel
        bottomLayer.addChild( new KitPanel( getModel(), mvt ) );

        for ( final Kit kit : getModel().getKits() ) {
            KitView kitView = new KitView( kit, mvt );
            kitMap.put( kit, kitView );
            for ( Bucket bucket : kit.getBuckets() ) {
                for ( final AtomModel atom : bucket.getAtoms() ) {
                    final AtomNode atomNode = new AtomNode( mvt, atom );
                    atomLayer.addChild( atomNode );

                    // Add a drag listener that will move the model element when the user
                    // drags this atom.
                    atomNode.addInputEventListener( new PDragEventHandler() {
                        @Override
                        protected void startDrag( PInputEvent event ) {
                            super.startDrag( event );
                            atom.setUserControlled( true );
                        }

                        @Override
                        public void mouseDragged( PInputEvent event ) {
                            PDimension delta = event.getDeltaRelativeTo( atomNode.getParent() );
                            ImmutableVector2D modelDelta = mvt.viewToModelDelta( new ImmutableVector2D( delta.width, delta.height ) );
                            kit.atomDragged( atom, modelDelta );
                        }

                        @Override
                        protected void endDrag( PInputEvent event ) {
                            super.endDrag( event );
                            atom.setUserControlled( false );
                        }
                    } );
                }
            }
            bottomLayer.addChild( kitView.getBottomLayer() );
            topLayer.addChild( kitView.getTopLayer() );
        }
    }

    // TODO: add destruct so we can switch models

    public KitCollectionModel getModel() {
        return modelProperty.getValue();
    }

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
