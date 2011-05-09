// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.module;

import java.awt.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.*;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.buildamolecule.view.MoleculeCollectingCanvas;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.buildamolecule.model.AtomModel.*;

/**
 * Module for 2nd tab. Collection boxes take multiple molecules of the same type, and start off with a different kit collection each time
 */
public class CollectMultipleModule extends AbstractBuildAMoleculeModule {

    public CollectMultipleModule( Frame parentFrame ) {
        super( parentFrame, BuildAMoleculeStrings.TITLE_COLLECT_MULTIPLE, false );

        /*---------------------------------------------------------------------------*
        * initial model
        *----------------------------------------------------------------------------*/

        final KitCollectionModel initialModel = new KitCollectionModel( bounds ) {{
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 400, 200 ), getClock(), HYDROGEN_FACTORY, 2 ),
                             new Bucket( new PDimension( 450, 200 ), getClock(), OXYGEN_FACTORY, 2 )
            ) );

            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 500, 200 ), getClock(), CARBON_FACTORY, 2 ),
                             new Bucket( new PDimension( 600, 200 ), getClock(), OXYGEN_FACTORY, 4 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), NITROGEN_FACTORY, 2 )
            ) );
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 600, 200 ), getClock(), HYDROGEN_FACTORY, 12 ),
                             new Bucket( new PDimension( 600, 200 ), getClock(), OXYGEN_FACTORY, 4 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), NITROGEN_FACTORY, 2 )
            ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.CO2, 2 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.O2, 2 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.H2, 4 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.NH3, 2 ) );
        }};

        setModel( initialModel );
    }

    @Override protected BuildAMoleculeCanvas buildCanvas( KitCollectionModel model ) {
        return new MoleculeCollectingCanvas( parentFrame, model, false, new VoidFunction0() {
            public void apply() {
                setModel( generateModel() );
            }
        } );
    }

    @Override protected KitCollectionModel generateModel() {
        return generateModel( true, 4 );
    }
}
