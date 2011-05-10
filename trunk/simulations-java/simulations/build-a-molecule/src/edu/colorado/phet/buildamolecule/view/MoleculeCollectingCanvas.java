package edu.colorado.phet.buildamolecule.view;

import java.awt.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.control.AllFilledDialogNode;
import edu.colorado.phet.buildamolecule.control.CollectionAreaNode;
import edu.colorado.phet.buildamolecule.model.KitCollectionModel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * Canvas (like its subclass) that shows kits, but also has a collection area to the right-hand side
 */
public class MoleculeCollectingCanvas extends BuildAMoleculeCanvas {

    private CollectionAreaNode collectionAreaNode;

    public MoleculeCollectingCanvas( Frame parentFrame, final KitCollectionModel model, final boolean singleCollectionMode, final VoidFunction0 regenerateCallback ) {
        super( parentFrame, model, singleCollectionMode );

        final AllFilledDialogNode allFilledDialogNode = new AllFilledDialogNode( model, getModelViewTransform(), regenerateCallback );

        model.allCollectionBoxesFilled.addObserver( new SimpleObserver() {
            public void update() {
                if ( model.allCollectionBoxesFilled.get() ) {
                    addWorldChild( allFilledDialogNode );
                }
                else {
                    removeWorldChild( allFilledDialogNode );
                }
            }
        } );
    }

    @Override protected void addChildren() {
        collectionAreaNode = new CollectionAreaNode( this, getModel(), singleCollectionMode ) {{
            double collectionAreaPadding = 20;
            setOffset( BuildAMoleculeConstants.STAGE_SIZE.width - getFullBounds().getWidth() - collectionAreaPadding, collectionAreaPadding );
        }};
        addWorldChild( collectionAreaNode );
    }

}
