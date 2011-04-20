// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.module;

import java.awt.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.KitCollectionModel;
import edu.colorado.phet.buildamolecule.model.LayoutBounds;
import edu.colorado.phet.buildamolecule.model.buckets.Bucket;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.buildamolecule.view.LargerMoleculesCanvas;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.buildamolecule.model.buckets.AtomModel.*;

/**
 * Module for the 3rd tab. Shows kits below as normal, but without collection boxes. Instead, the user is presented with an option of a "3d" view
 * TODO: put together common BuildAMoleculeModule behavior
 */
public class LargerMoleculesModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BuildAMoleculeCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public LargerMoleculesModule( Frame parentFrame ) {
        super( BuildAMoleculeStrings.TITLE_LARGER_MOLECULES, new ConstantDtClock( 30 ) );

        setClockControlPanel( null );

        /*---------------------------------------------------------------------------*
        * initial model
        *----------------------------------------------------------------------------*/

        final LayoutBounds bounds = new LayoutBounds( true );

        final KitCollectionModel initialModel = new KitCollectionModel( bounds ) {{
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 600, 200 ), getClock(), HYDROGEN_FACTORY, 13 ),
                             new Bucket( new PDimension( 450, 200 ), getClock(), OXYGEN_FACTORY, 3 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), CARBON_FACTORY, 3 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), NITROGEN_FACTORY, 3 ),
                             new Bucket( new PDimension( 600, 200 ), getClock(), CHLORINE_FACTORY, 2 )
            ) );
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 600, 200 ), getClock(), HYDROGEN_FACTORY, 13 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), FLUORINE_FACTORY, 2 ),
                             new Bucket( new PDimension( 600, 200 ), getClock(), CHLORINE_FACTORY, 2 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), CARBON_FACTORY, 3 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), OXYGEN_FACTORY, 3 )
            ) );
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 600, 200 ), getClock(), HYDROGEN_FACTORY, 13 ),
                             new Bucket( new PDimension( 350, 200 ), getClock(), BORON_FACTORY, 1 ),
                             new Bucket( new PDimension( 350, 200 ), getClock(), SULPHUR_FACTORY, 1 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), SILICON_FACTORY, 1 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), PHOSPHORUS_FACTORY, 1 )
            ) );
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 600, 200 ), getClock(), HYDROGEN_FACTORY, 13 ),
                             new Bucket( new PDimension( 700, 200 ), getClock(), BROMINE_FACTORY, 2 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), NITROGEN_FACTORY, 3 ),
                             new Bucket( new PDimension( 500, 200 ), getClock(), CARBON_FACTORY, 3 )
            ) );
        }};

        /*---------------------------------------------------------------------------*
        * canvas
        *----------------------------------------------------------------------------*/
        canvas = new LargerMoleculesCanvas( parentFrame, initialModel );
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
