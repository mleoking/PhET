// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.module;

import java.awt.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.Bucket;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.KitCollectionModel;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.buildamolecule.model.AtomModel.*;

/**
 * Module for the 3rd tab. Shows kits below as normal, but without collection boxes. Instead, the user is presented with an option of a "3d" view
 */
public class LargerMoleculesModule extends AbstractBuildAMoleculeModule {

    public LargerMoleculesModule( Frame parentFrame ) {
        super( parentFrame, BuildAMoleculeStrings.TITLE_LARGER_MOLECULES, true );

        /*---------------------------------------------------------------------------*
        * initial model
        *----------------------------------------------------------------------------*/

        final KitCollectionModel initialModel = new KitCollectionModel( bounds ) {{
            // general kit
            addKit( new Kit( bounds,
                             new Bucket( getClock(), HYDROGEN_FACTORY, 13 ),
                             new Bucket( getClock(), OXYGEN_FACTORY, 3 ),
                             new Bucket( getClock(), CARBON_FACTORY, 3 ),
                             new Bucket( getClock(), NITROGEN_FACTORY, 3 ),
                             new Bucket( getClock(), CHLORINE_FACTORY, 2 )
            ) );

            // organics kit
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 700, 200 ), getClock(), HYDROGEN_FACTORY, 21 ),
                             new Bucket( getClock(), OXYGEN_FACTORY, 4 ),
                             new Bucket( getClock(), CARBON_FACTORY, 4 ),
                             new Bucket( getClock(), NITROGEN_FACTORY, 4 )
            ) );

            // chlorine / fluorine
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 700, 200 ), getClock(), HYDROGEN_FACTORY, 21 ),
                             new Bucket( getClock(), CARBON_FACTORY, 4 ),
                             new Bucket( getClock(), CHLORINE_FACTORY, 4 ),
                             new Bucket( getClock(), FLUORINE_FACTORY, 4 )
            ) );

            // boron / silicon
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 700, 200 ), getClock(), HYDROGEN_FACTORY, 21 ),
                             new Bucket( getClock(), CARBON_FACTORY, 3 ),
                             new Bucket( getClock(), BORON_FACTORY, 2 ),
                             new Bucket( getClock(), SILICON_FACTORY, 2 )
            ) );

            // sulphur / oxygen
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 700, 200 ), getClock(), HYDROGEN_FACTORY, 21 ),
                             new Bucket( getClock(), BORON_FACTORY, 1 ),
                             new Bucket( getClock(), SULPHUR_FACTORY, 2 ),
                             new Bucket( getClock(), SILICON_FACTORY, 1 ),
                             new Bucket( getClock(), PHOSPHORUS_FACTORY, 1 )
            ) );

            // phosphorus
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 700, 200 ), getClock(), HYDROGEN_FACTORY, 21 ),
                             new Bucket( getClock(), CARBON_FACTORY, 4 ),
                             new Bucket( getClock(), OXYGEN_FACTORY, 2 ),
                             new Bucket( getClock(), PHOSPHORUS_FACTORY, 2 )
            ) );

            // iodine
            // TODO: make iodine look better. "I" can be taller than atom
//            addKit( new Kit( bounds,
//                             new Bucket( new PDimension( 700, 200 ), getClock(), HYDROGEN_FACTORY, 21 ),
//                             new Bucket( getClock(), CARBON_FACTORY, 4 ),
//                             new Bucket( getClock(), IODINE_FACTORY, 2 ),
//                             new Bucket( getClock(), OXYGEN_FACTORY, 2 )
//            ) );

            // bromine kit?
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 700, 200 ), getClock(), HYDROGEN_FACTORY, 21 ),
                             new Bucket( getClock(), BROMINE_FACTORY, 2 ),
                             new Bucket( getClock(), NITROGEN_FACTORY, 3 ),
                             new Bucket( getClock(), CARBON_FACTORY, 3 )
            ) );
        }};

        setModel( initialModel );
    }

    @Override
    protected BuildAMoleculeCanvas buildCanvas( KitCollectionModel model ) {
        return new BuildAMoleculeCanvas( parentFrame, model );
    }
}
