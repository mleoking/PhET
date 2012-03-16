//  Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.view;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.model.CollectionList;
import edu.colorado.phet.buildamolecule.model.CollectionList.Adapter;
import edu.colorado.phet.buildamolecule.model.KitCollection;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.buildamolecule.BuildAMoleculeConstants.MODEL_VIEW_TRANSFORM;

/**
 * Common canvas for Build a Molecule. It features kits shown at the bottom. Can be extended to add other parts
 */
public class BuildAMoleculeCanvas extends PhetPCanvas {

    public final CollectionList collectionList; // our (mutable) list of collections that we display

    /**
     * A node added on the canvas at a level below all of the atoms and other objects in play.
     * Used for adding the collection area.
     */
    private PNode baseLayer = new PNode();

    public BuildAMoleculeCanvas( CollectionList collectionList ) {
        this.collectionList = collectionList;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, BuildAMoleculeConstants.STAGE_SIZE ) );

        setBackground( BuildAMoleculeConstants.CANVAS_BACKGROUND_COLOR );

        addWorldChild( baseLayer );

        final KitCollection firstCollection = collectionList.currentCollection.get();
        addCollection( firstCollection );

        // handle newly added collections
        collectionList.addListener( new Adapter() {
            public void addedCollection( KitCollection collection ) {
                addCollection( collection );
            }
        } );
    }

    protected KitCollectionNode addCollection( KitCollection collection ) {
        KitCollectionNode result = new KitCollectionNode( collectionList, collection, this );
        addWorldChild( result );

        // return this so we can manipulate it in an override
        return result;
    }

    public KitCollection getCurrentCollection() {
        return collectionList.currentCollection.get();
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
    }

    public PNode getBaseLayer() {
        return baseLayer;
    }

    /**
     * Returns model bounds from a piccolo node, given local coordinates on a piccolo node.
     */
    public final Function1<PNode, Rectangle2D> toModelBounds = new Function1<PNode, Rectangle2D>() {
        public Rectangle2D apply( PNode node ) {
            // this requires getting local => global => view => model coordinates

            // our bounds relative to the root Piccolo canvas
            Rectangle2D globalBounds = node.getParent().localToGlobal( node.getFullBounds() );

            // pull out the upper-left corner and dimension so we can transform them
            Point2D upperLeftCorner = new Point2D.Double( globalBounds.getX(), globalBounds.getY() );
            PDimension dimensions = new PDimension( globalBounds.getWidth(), globalBounds.getHeight() );

            // transform the point and dimensions to world coordinates
            getPhetRootNode().globalToWorld( upperLeftCorner );
            getPhetRootNode().globalToWorld( dimensions );

            // our bounds relative to our simulation (BAM) canvas. Will be filled in
            Rectangle2D viewBounds = new Rectangle2D.Double( upperLeftCorner.getX(), upperLeftCorner.getY(), dimensions.getWidth(), dimensions.getHeight() );

            // return the model bounds
            return MODEL_VIEW_TRANSFORM.viewToModel( viewBounds ).getBounds2D();
        }
    };
}
