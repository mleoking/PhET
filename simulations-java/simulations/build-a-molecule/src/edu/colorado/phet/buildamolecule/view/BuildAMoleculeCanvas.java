// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.model.*;
import edu.colorado.phet.buildamolecule.model.CollectionList.Adapter;
import edu.colorado.phet.buildamolecule.model.CollectionList.Listener;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * Common canvas for Build a Molecule. It features kits shown at the bottom. Can be extended to add other parts
 */
public class BuildAMoleculeCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // View
    private final PNode _rootNode;

    // Model-View transform.
    private final ModelViewTransform mvt;

    private CollectionBoxHintNode collectionBoxHintNode = null;

    // used for notifying others of the collection area attachment. TODO consider changing class initialization so this can be moved to MoleculeCollectingCanvas
    protected java.util.List<SimpleObserver> collectionAttachmentListeners = new LinkedList<SimpleObserver>();

    private Frame parentFrame;
    public final CollectionList collectionList;
    protected boolean singleCollectionMode; // TODO: find solution for LargerMoleculesCanvas so that we don't need this boolean and the separate constructor

    protected void addChildren( Frame parentFrame ) {

    }

    public BuildAMoleculeCanvas( Frame parentFrame, CollectionList collectionList ) {
        this( parentFrame, collectionList, true );
    }

    public BuildAMoleculeCanvas( Frame parentFrame, CollectionList collectionList, boolean singleCollectionMode ) {
        this.parentFrame = parentFrame;
        this.collectionList = collectionList;
        this.singleCollectionMode = singleCollectionMode;

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

        addChildren( parentFrame );

        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );

        final KitCollection firstCollection = collectionList.currentCollection.get();

        addCollection( firstCollection );

        collectionList.addListener( new Adapter() {
            public void addedCollection( KitCollection collection ) {
                addCollection( collection );
            }
        } );

        /*---------------------------------------------------------------------------*
        * collection box hint arrow. add this only to the 1st collection
        *----------------------------------------------------------------------------*/

        for ( final Kit kit : firstCollection.getKits() ) {
            kit.addMoleculeListener( new Kit.MoleculeAdapter() {
                @Override public void addedMolecule( Molecule molecule ) {
                    CollectionBox targetBox = firstCollection.getFirstTargetBox( molecule );

                    // if a hint doesn't exist AND we have a target box, add it
                    if ( collectionBoxHintNode == null && targetBox != null ) {
                        collectionBoxHintNode = new CollectionBoxHintNode( mvt, molecule, targetBox );
                        addWorldChild( collectionBoxHintNode );
                    }
                    else if ( collectionBoxHintNode != null ) {
                        // otherwise clear any other hint nodes
                        collectionBoxHintNode.disperse();
                    }
                }

                @Override public void removedMolecule( Molecule molecule ) {
                    // clear any existing hint node on molecule removal
                    if ( collectionBoxHintNode != null ) {
                        collectionBoxHintNode.disperse();
                    }
                }
            } );

            // whenever a kit switch happens, remove the arrow
            kit.visible.addObserver( new SimpleObserver() {
                public void update() {
                    // clear any existing hint node on molecule removal
                    if ( collectionBoxHintNode != null ) {
                        collectionBoxHintNode.disperse();
                    }
                }
            } );
        }
    }

    public KitCollectionNode addCollection( KitCollection collection ) {
        KitCollectionNode result = new KitCollectionNode( parentFrame, collectionList, collection, mvt, this );
        addWorldChild( result );

        // return this so we can manipulate it in an override
        return result;
    }

    public KitCollection getCurrentCollection() {
        return collectionList.currentCollection.get();
    }

    public ModelViewTransform getModelViewTransform() {
        return mvt;
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
