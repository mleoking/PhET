//  Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule.module;

import java.awt.Frame;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.Bucket;
import edu.colorado.phet.buildamolecule.model.CollectionList;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.KitCollection;
import edu.colorado.phet.buildamolecule.model.LayoutBounds;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.buildamolecule.BuildAMoleculeSimSharing.UserComponent.largerMoleculesTab;
import static edu.colorado.phet.chemistry.model.Element.*;

/**
 * Module for the 3rd tab. Shows kits below as normal, but without collection boxes. Instead, the user is presented with an option of a "3d" view
 */
public class LargerMoleculesModule extends AbstractBuildAMoleculeModule {

    public LargerMoleculesModule( Frame parentFrame ) {
        super( largerMoleculesTab, parentFrame, BuildAMoleculeStrings.TITLE_LARGER_MOLECULES, new LayoutBounds( true, 0 ) );

        /*---------------------------------------------------------------------------*
        * initial model
        *----------------------------------------------------------------------------*/

        final KitCollection initialCollection = new KitCollection() {{
            // NOTE: if kits are modified here, examine MAX_NUM_HEAVY_ATOMS in MoleculeSDFCombinedParser, as it may need to be changed

            // general kit
            addKit( new Kit( bounds,
                             new Bucket( getClock(), H, 13 ),
                             new Bucket( getClock(), O, 3 ),
                             new Bucket( getClock(), C, 3 ),
                             new Bucket( getClock(), N, 3 ),
                             new Bucket( getClock(), Cl, 2 )
            ) );

            // organics kit
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 700, 200 ), getClock(), H, 21 ),
                             new Bucket( getClock(), O, 4 ),
                             new Bucket( getClock(), C, 4 ),
                             new Bucket( getClock(), N, 4 )
            ) );

            // chlorine / fluorine
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 700, 200 ), getClock(), H, 21 ),
                             new Bucket( getClock(), C, 4 ),
                             new Bucket( getClock(), Cl, 4 ),
                             new Bucket( getClock(), F, 4 )
            ) );

            // boron / silicon
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 700, 200 ), getClock(), H, 21 ),
                             new Bucket( getClock(), C, 3 ),
                             new Bucket( getClock(), B, 2 ),
                             new Bucket( getClock(), Si, 2 )
            ) );

            // sulphur / oxygen
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 700, 200 ), getClock(), H, 21 ),
                             new Bucket( getClock(), B, 1 ),
                             new Bucket( getClock(), S, 2 ),
                             new Bucket( getClock(), Si, 1 ),
                             new Bucket( getClock(), P, 1 )
            ) );

            // phosphorus
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 700, 200 ), getClock(), H, 21 ),
                             new Bucket( getClock(), C, 4 ),
                             new Bucket( getClock(), O, 2 ),
                             new Bucket( getClock(), P, 2 )
            ) );

            // bromine kit?
            addKit( new Kit( bounds,
                             new Bucket( new PDimension( 700, 200 ), getClock(), H, 21 ),
                             new Bucket( getClock(), Br, 2 ),
                             new Bucket( getClock(), N, 3 ),
                             new Bucket( getClock(), C, 3 )
            ) );
        }};

        setInitialCollection( initialCollection );
    }

    @Override
    protected BuildAMoleculeCanvas buildCanvas( CollectionList collectionList ) {
        return new BuildAMoleculeCanvas( collectionList );
    }
}
