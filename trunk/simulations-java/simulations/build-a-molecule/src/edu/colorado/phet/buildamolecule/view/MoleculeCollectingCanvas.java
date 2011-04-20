package edu.colorado.phet.buildamolecule.view;

import java.awt.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.control.AllFilledDialogNode;
import edu.colorado.phet.buildamolecule.control.CollectionAreaNode;
import edu.colorado.phet.buildamolecule.model.KitCollectionModel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * Canvas that shows multiple kits AND a collection area to the right
 */
public class MoleculeCollectingCanvas extends BuildAMoleculeCanvas {

    private CollectionAreaNode collectionAreaNode;

    public MoleculeCollectingCanvas( Frame parentFrame, final KitCollectionModel initialModel, final boolean singleCollectionMode, final VoidFunction0 regenerateCallback ) {
        super( parentFrame, initialModel, singleCollectionMode );

        // TODO: change if we support changing models
        initialModel.allCollectionBoxesFilled.addObserver( new SimpleObserver() {
            public void update() {
                if ( !initialModel.allCollectionBoxesFilled.getValue() ) {
                    // not filled
                    return;
                }
                addWorldChild( new AllFilledDialogNode( initialModel, getModelViewTransform(), regenerateCallback ) );
            }
        } );
    }

    @Override
    protected void addChildren() {
        collectionAreaNode = new CollectionAreaNode( this, getModel(), singleCollectionMode ) {{
            double collectionAreaPadding = 20;
            setOffset( BuildAMoleculeConstants.STAGE_SIZE.width - getFullBounds().getWidth() - collectionAreaPadding, collectionAreaPadding );
        }};
        addWorldChild( collectionAreaNode );
    }

}
