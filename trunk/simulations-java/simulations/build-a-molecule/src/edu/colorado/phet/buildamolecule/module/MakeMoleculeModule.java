//  Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.module;

import java.awt.Frame;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.control.CollectionPanel;
import edu.colorado.phet.buildamolecule.model.Bucket;
import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.buildamolecule.model.CollectionList;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.KitCollection;
import edu.colorado.phet.buildamolecule.model.LayoutBounds;
import edu.colorado.phet.buildamolecule.model.MoleculeList;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.buildamolecule.view.MoleculeCollectingCanvas;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.buildamolecule.BuildAMoleculeSimSharing.UserComponent.makeMoleculesTab;
import static edu.colorado.phet.chemistry.model.Element.*;

/**
 * Module for the 1st tab: collection boxes only take 1 molecule, and our 1st kit collection is always the same
 */
public class MakeMoleculeModule extends AbstractBuildAMoleculeModule {

    public MakeMoleculeModule( Frame parentFrame ) {
        super( makeMoleculesTab, parentFrame, BuildAMoleculeStrings.TITLE_MAKE_MOLECULE, new LayoutBounds( false, CollectionPanel.getCollectionPanelModelWidth( true ) ) );

        /*---------------------------------------------------------------------------*
        * initial model
        *----------------------------------------------------------------------------*/

        final KitCollection initialCollection = new KitCollection() {{
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 400, 200 ), getClock(), H, 2 ),
                             new Bucket( new PDimension( 350, 200 ), getClock(), O, 1 )
            ) );
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 400, 200 ), getClock(), H, 2 ),
                             new Bucket( new PDimension( 450, 200 ), getClock(), O, 2 )
            ) );
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 350, 200 ), getClock(), C, 1 ),
                             new Bucket( new PDimension( 450, 200 ), getClock(), O, 2 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), N, 2 )
            ) );
            addCollectionBox( new CollectionBox( MoleculeList.H2O, 1 ) );
            addCollectionBox( new CollectionBox( MoleculeList.O2, 1 ) );
            addCollectionBox( new CollectionBox( MoleculeList.H2, 1 ) );
            addCollectionBox( new CollectionBox( MoleculeList.CO2, 1 ) );
            addCollectionBox( new CollectionBox( MoleculeList.N2, 1 ) );
        }};

        setInitialCollection( initialCollection );
    }

    @Override protected BuildAMoleculeCanvas buildCanvas( CollectionList collectionList ) {
        return new MoleculeCollectingCanvas( collectionList, true, new VoidFunction0() {
            public void apply() {
                addGeneratedCollection();
            }
        } );
    }

    @Override protected KitCollection generateModel() {
        return generateModel( false, 5 );
    }
}
