// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.module;

import java.awt.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.*;
import edu.colorado.phet.buildamolecule.model.buckets.Bucket;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.buildamolecule.model.buckets.AtomModel.*;

/**
 * Module for 2nd tab. Collection boxes take multiple molecules of the same type, and start off with a different kit collection each time
 * TODO: randomize initial kit collection
 */
public class CollectMultipleModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BuildAMoleculeCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public CollectMultipleModule( Frame parentFrame ) {
        super( BuildAMoleculeStrings.TITLE_MAKE_MOLECULE, new ConstantDtClock( 30 ) );

        setClockControlPanel( null );

        /*---------------------------------------------------------------------------*
        * initial model
        *----------------------------------------------------------------------------*/

        final LayoutBounds bounds = new LayoutBounds();

        final KitCollectionModel initialModel = new KitCollectionModel( bounds ) {{
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 400, 200 ), getClock(), HYDROGEN_FACTORY, 2 ),
                             new Bucket( new PDimension( 450, 200 ), getClock(), OXYGEN_FACTORY, 2 )
            ) );

            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 350, 200 ), getClock(), CARBON_FACTORY, 1 ),
                             new Bucket( new PDimension( 450, 200 ), getClock(), OXYGEN_FACTORY, 2 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), NITROGEN_FACTORY, 2 )
            ) );

            /*---------------------------------------------------------------------------*
            * example kits
            *----------------------------------------------------------------------------*/
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 400, 200 ), getClock(), HYDROGEN_FACTORY, 7 ),
                             new Bucket( new PDimension( 450, 200 ), getClock(), OXYGEN_FACTORY, 3 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), CARBON_FACTORY, 2 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), NITROGEN_FACTORY, 2 )
            ) );
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 400, 200 ), getClock(), HYDROGEN_FACTORY, 7 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), FLUORINE_FACTORY, 2 ),
                             new Bucket( new PDimension( 600, 200 ), getClock(), CHLORINE_FACTORY, 2 ),
                             new Bucket( new PDimension( 350, 200 ), getClock(), CARBON_FACTORY, 1 )
            ) );
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 400, 200 ), getClock(), HYDROGEN_FACTORY, 7 ),
                             new Bucket( new PDimension( 350, 200 ), getClock(), BORON_FACTORY, 1 ),
                             new Bucket( new PDimension( 350, 200 ), getClock(), SULPHUR_FACTORY, 1 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), SILICON_FACTORY, 1 )
            ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.CO2, 2 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.O2, 2 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.H2, 4 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.NH3, 2 ) );
        }};

        /*---------------------------------------------------------------------------*
        * canvas
        *----------------------------------------------------------------------------*/
        canvas = new BuildAMoleculeCanvas( initialModel, false ); // single collection mode
        setSimulationPanel( canvas );

        // Set initial state
        reset();
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {
        // TODO: global reset entry point
    }
}
