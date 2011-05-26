// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.module;

import java.awt.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.*;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.buildamolecule.view.MoleculeCollectingCanvas;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.chemistry.model.Element.*;

/**
 * Module for 2nd tab. Collection boxes take multiple molecules of the same type, and start off with a different kit collection each time
 */
public class CollectMultipleModule extends AbstractBuildAMoleculeModule {

    public CollectMultipleModule( Frame parentFrame ) {
        super( parentFrame, BuildAMoleculeStrings.TITLE_COLLECT_MULTIPLE, false );

        /*---------------------------------------------------------------------------*
        * initial model
        *----------------------------------------------------------------------------*/

        final KitCollection initialCollection = new KitCollection() {{
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 400, 200 ), getClock(), H, 2 ),
                             new Bucket( new PDimension( 450, 200 ), getClock(), O, 2 )
            ) );

            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 500, 200 ), getClock(), C, 2 ),
                             new Bucket( new PDimension( 600, 200 ), getClock(), O, 4 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), N, 2 )
            ) );
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 600, 200 ), getClock(), H, 12 ),
                             new Bucket( new PDimension( 600, 200 ), getClock(), O, 4 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), N, 2 )
            ) );
            addCollectionBox( new CollectionBox( MoleculeList.CO2, 2 ) );
            addCollectionBox( new CollectionBox( MoleculeList.O2, 2 ) );
            addCollectionBox( new CollectionBox( MoleculeList.H2, 4 ) );
            addCollectionBox( new CollectionBox( MoleculeList.NH3, 2 ) );
        }};

        setInitialCollection( initialCollection );
    }

    @Override protected BuildAMoleculeCanvas buildCanvas( CollectionList collectionList ) {
        return new MoleculeCollectingCanvas( collectionList, false, new VoidFunction0() {
            public void apply() {
                addGeneratedCollection();
            }
        } );
    }

    @Override protected KitCollection generateModel() {
        return generateModel( true, 4 );
    }
}
