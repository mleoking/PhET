package edu.colorado.phet.buildamolecule.view;

import java.awt.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.control.CollectionAreaNode;
import edu.colorado.phet.buildamolecule.model.KitCollectionModel;

/**
 * Canvas that shows multiple kits AND a collection area to the right
 */
public class MoleculeCollectingCanvas extends BuildAMoleculeCanvas {

    private CollectionAreaNode collectionAreaNode;

    public MoleculeCollectingCanvas( Frame parentFrame, KitCollectionModel initialModel, final boolean singleCollectionMode ) {
        super( parentFrame, initialModel, singleCollectionMode );
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
