//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view;

import java.util.LinkedList;

import edu.colorado.phet.buildamolecule.control.AllFilledDialogNode;
import edu.colorado.phet.buildamolecule.control.CollectionPanel;
import edu.colorado.phet.buildamolecule.model.*;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

import static edu.colorado.phet.buildamolecule.BuildAMoleculeConstants.STAGE_SIZE;
import static edu.colorado.phet.buildamolecule.BuildAMoleculeConstants.VIEW_PADDING;

/**
 * Canvas (like its subclass) that shows kits, but also has a collection area to the right-hand side
 */
public class MoleculeCollectingCanvas extends BuildAMoleculeCanvas {

    private CollectionBoxHintNode collectionBoxHintNode = null; // arrow shown to prompt user to put a molecule in a collection box

    private VoidFunction0 regenerateCallback;

    // used for notifying others of the collection area attachment.
    protected java.util.List<SimpleObserver> collectionAttachmentListeners = new LinkedList<SimpleObserver>();

    public MoleculeCollectingCanvas( CollectionList collectionList, final boolean singleCollectionMode, final VoidFunction0 regenerateCallback ) {
        super( collectionList );

        this.regenerateCallback = regenerateCallback;

        getBaseLayer().addChild( new CollectionPanel( collectionList, singleCollectionMode, new VoidFunction1<SimpleObserver>() {
            /**
             In place so that we can notify this observer when the collection panel (and its associated collection boxes) are fully
             * attached to the canvas. This is necessary, because we need to turn view => model coordinates and update the box model
             * positions.
             * @param simpleObserver Observer to add
             */
            public void apply( SimpleObserver simpleObserver ) {
                collectionAttachmentListeners.add( simpleObserver );
            }
        }, toModelBounds ) {{
            setOffset( STAGE_SIZE.width - getFullBounds().getWidth() - VIEW_PADDING, VIEW_PADDING );
        }} );

        for ( SimpleObserver observer : collectionAttachmentListeners ) {
            observer.update();
        }

        /*---------------------------------------------------------------------------*
        * collection box hint arrow. add this only to the 1st collection
        *----------------------------------------------------------------------------*/

        final KitCollection firstCollection = collectionList.currentCollection.get();
        for ( final Kit kit : firstCollection.getKits() ) {
            kit.addMoleculeListener( new Kit.MoleculeAdapter() {
                @Override public void addedMolecule( Molecule molecule ) {
                    CollectionBox targetBox = firstCollection.getFirstTargetBox( molecule );

                    // if a hint doesn't exist AND we have a target box, add it
                    if ( collectionBoxHintNode == null && targetBox != null ) {
                        collectionBoxHintNode = new CollectionBoxHintNode( molecule, targetBox );
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

    @Override public KitCollectionNode addCollection( final KitCollection collection ) {
        final KitCollectionNode kitCollectionNode = super.addCollection( collection );

        // show dialog the 1st time all collection boxes are filled
        collection.allCollectionBoxesFilled.addObserver( new SimpleObserver() {
            private boolean hasShownOnce = false;
            private AllFilledDialogNode allFilledDialogNode;

            public void update() {
                if ( collection.allCollectionBoxesFilled.get() ) {
                    if ( !hasShownOnce ) {
                        allFilledDialogNode = new AllFilledDialogNode( collectionList.getAvailablePlayAreaBounds(), regenerateCallback );
                        hasShownOnce = true;
                    }
                    kitCollectionNode.addChild( allFilledDialogNode );
                }
                else {
                    if ( allFilledDialogNode != null ) {
                        kitCollectionNode.removeChild( allFilledDialogNode );
                    }
                }
            }
        } );

        return kitCollectionNode;
    }

}
