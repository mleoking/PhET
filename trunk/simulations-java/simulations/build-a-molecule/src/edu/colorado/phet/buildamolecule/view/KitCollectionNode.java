//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view;

import java.awt.*;

import edu.colorado.phet.buildamolecule.control.KitPanel;
import edu.colorado.phet.buildamolecule.model.CollectionList;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.KitCollection;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;

/**
 * Contains the kits and atoms in the play area.
 */
public class KitCollectionNode extends PNode {

    public KitCollectionNode( Frame parentFrame, final CollectionList collectionList, final KitCollection collection, ModelViewTransform mvt, BuildAMoleculeCanvas canvas ) {
        /*---------------------------------------------------------------------------*
        * layers
        *----------------------------------------------------------------------------*/
        final PNode bottomLayer = new PNode();
        final PNode metadataLayer = new PNode();
        final PNode atomLayer = new PNode();
        final PNode topLayer = new PNode();

        addChild( bottomLayer );
        addChild( atomLayer );
        addChild( metadataLayer );
        addChild( topLayer );

        bottomLayer.addChild( new KitPanel( collection, collectionList.getAvailableKitBounds(), mvt ) );

        for ( final Kit kit : collection.getKits() ) {
            KitView kitView = new KitView( parentFrame, kit, mvt, canvas ); // TODO: we need the ability to control scissors (screen) node from molecule bond code?
            bottomLayer.addChild( kitView.getBottomLayer() );
            atomLayer.addChild( kitView.getAtomLayer() );
            metadataLayer.addChild( kitView.getMetadataLayer() );
            topLayer.addChild( kitView.getTopLayer() );
        }

        // set visibility based on whether our collection is the current one
        collectionList.currentCollection.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( collectionList.currentCollection.get() == collection );
            }
        } );
    }
}
