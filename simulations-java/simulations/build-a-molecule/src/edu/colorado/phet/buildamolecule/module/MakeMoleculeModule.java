// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.module;

import java.awt.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.*;
import edu.colorado.phet.buildamolecule.model.buckets.Bucket;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.buildamolecule.view.MoleculeCollectingCanvas;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.buildamolecule.model.buckets.AtomModel.*;

/**
 * Module for the 1st tab: collection boxes only take 1 molecule, and our 1st kit collection is always the same
 */
public class MakeMoleculeModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BuildAMoleculeCanvas canvas;
    private LayoutBounds bounds;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MakeMoleculeModule( Frame parentFrame ) {
        super( BuildAMoleculeStrings.TITLE_MAKE_MOLECULE, new ConstantDtClock( 30 ) );

        setClockControlPanel( null );

        // TODO: consolidate common module code into something like BuildAMoleculeModule

        /*---------------------------------------------------------------------------*
        * initial model
        *----------------------------------------------------------------------------*/

        bounds = new LayoutBounds( false );

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
            addCollectionBox( new CollectionBox( CompleteMolecule.H2O, 1 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.O2, 1 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.H2, 1 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.CO2, 1 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.N2, 1 ) );
        }};

        /*---------------------------------------------------------------------------*
        * canvas
        *----------------------------------------------------------------------------*/
        canvas = createCanvas( parentFrame, initialModel );
        setSimulationPanel( canvas );

        // Set initial state
        reset();
    }

    private KitCollectionModel generateModel() {
        // TODO
        return new KitCollectionModel( bounds ) {{
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 600, 200 ), getClock(), HYDROGEN_FACTORY, 3 ),
                             new Bucket( new PDimension( 600, 200 ), getClock(), NITROGEN_FACTORY, 3 )
            ) );
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 400, 200 ), getClock(), HYDROGEN_FACTORY, 4 ),
                             new Bucket( new PDimension( 450, 200 ), getClock(), OXYGEN_FACTORY, 2 )
            ) );
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 350, 200 ), getClock(), CARBON_FACTORY, 1 ),
                             new Bucket( new PDimension( 450, 200 ), getClock(), OXYGEN_FACTORY, 2 )
            ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.NH3, 1 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.CO2, 1 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.H2O, 1 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.H2, 1 ) );
            addCollectionBox( new CollectionBox( CompleteMolecule.N2, 1 ) );
        }};
    }

    private MoleculeCollectingCanvas createCanvas( final Frame parentFrame, KitCollectionModel initialModel ) {
        return new MoleculeCollectingCanvas( parentFrame, initialModel, true, new VoidFunction0() {
            public void apply() {
                canvas = createCanvas( parentFrame, generateModel() );
                setSimulationPanel( canvas );
            }
        } ); // single collection mode
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
