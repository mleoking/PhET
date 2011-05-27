package edu.colorado.phet.buildamolecule.view;

import edu.colorado.phet.buildamolecule.control.AllFilledDialogNode;
import edu.colorado.phet.buildamolecule.control.CollectionPanel;
import edu.colorado.phet.buildamolecule.model.CollectionList;
import edu.colorado.phet.buildamolecule.model.KitCollection;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

import static edu.colorado.phet.buildamolecule.BuildAMoleculeConstants.STAGE_SIZE;
import static edu.colorado.phet.buildamolecule.BuildAMoleculeConstants.VIEW_PADDING;

/**
 * Canvas (like its subclass) that shows kits, but also has a collection area to the right-hand side
 */
public class MoleculeCollectingCanvas extends BuildAMoleculeCanvas {

    private VoidFunction0 regenerateCallback;

    public MoleculeCollectingCanvas( CollectionList collectionList, final boolean singleCollectionMode, final VoidFunction0 regenerateCallback ) {
        super( collectionList, singleCollectionMode );

        this.regenerateCallback = regenerateCallback;

        for ( SimpleObserver observer : collectionAttachmentListeners ) {
            observer.update();
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
                        allFilledDialogNode = new AllFilledDialogNode( collectionList.getAvailablePlayAreaBounds(), getModelViewTransform(), regenerateCallback );
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

    @Override protected void addChildren() {
        CollectionPanel collectionAreaNode = new CollectionPanel( collectionList, singleCollectionMode, new VoidFunction1<SimpleObserver>() {
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
        }};
        addWorldChild( collectionAreaNode );
    }

}
