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
 * Module for the 1st tab: collection boxes only take 1 molecule, and our 1st kit collection is always the same
 */
public class MakeMoleculeModule extends AbstractBuildAMoleculeModule {

    public MakeMoleculeModule( Frame parentFrame ) {
        super( parentFrame, BuildAMoleculeStrings.TITLE_MAKE_MOLECULE, false );

        /*---------------------------------------------------------------------------*
        * initial model
        *----------------------------------------------------------------------------*/

        final KitCollectionModel initialModel = new KitCollectionModel( bounds ) {{
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 400, 200 ), getClock(), HYDROGEN_FACTORY, 2 ),
                             new Bucket( new PDimension( 350, 200 ), getClock(), OXYGEN_FACTORY, 1 )
            ) );
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 400, 200 ), getClock(), HYDROGEN_FACTORY, 2 ),
                             new Bucket( new PDimension( 450, 200 ), getClock(), OXYGEN_FACTORY, 2 )
            ) );
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 350, 200 ), getClock(), CARBON_FACTORY, 1 ),
                             new Bucket( new PDimension( 450, 200 ), getClock(), OXYGEN_FACTORY, 2 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), NITROGEN_FACTORY, 2 )
            ) );
            addCollectionBox( new CollectionBox( MoleculeList.H2O, 1 ) );
            addCollectionBox( new CollectionBox( MoleculeList.O2, 1 ) );
            addCollectionBox( new CollectionBox( MoleculeList.H2, 1 ) );
            addCollectionBox( new CollectionBox( MoleculeList.CO2, 1 ) );
            addCollectionBox( new CollectionBox( MoleculeList.N2, 1 ) );
        }};

        setModel( initialModel );
    }

    @Override protected BuildAMoleculeCanvas buildCanvas( KitCollectionModel model ) {
        return new MoleculeCollectingCanvas( parentFrame, model, true, new VoidFunction0() {
            public void apply() {
                setModel( generateModel() );
            }
        } );
    }

    @Override protected KitCollectionModel generateModel() {
        return generateModel( false, 5 );
    }
}
