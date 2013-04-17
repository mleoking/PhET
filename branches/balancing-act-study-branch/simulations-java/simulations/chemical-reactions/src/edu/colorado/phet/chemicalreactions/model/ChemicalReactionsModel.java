// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.Cl2;
import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.H2;
import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.H2O;
import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.HCl;
import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.N2;
import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.NH3;
import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.O2;

public class ChemicalReactionsModel {
    public final KitCollection kitCollection;
    private final LayoutBounds layoutBounds;

    public ChemicalReactionsModel( IClock clock, final LayoutBounds layoutBounds ) {
        this.layoutBounds = layoutBounds;

        kitCollection = new KitCollection() {{
            addKit( new Kit( layoutBounds,
                             // reactants
                             Arrays.asList(
                                     new MoleculeBucket( O2, 2 ),
                                     new MoleculeBucket( H2, 4 )
                             ),

                             // products
                             Arrays.asList(
                                     new MoleculeBucket( H2O, 0, 4 )
                             ),

                             Arrays.asList( ReactionShape.H2_O2_TO_H2O )
            ) );
            addKit( new Kit( layoutBounds,
                             // reactants
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( N2, 3 ) );
                                 add( new MoleculeBucket( H2, 9 ) );
                             }},

                             // products
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( NH3, 0, 6 ) );
                             }},
                             Arrays.asList( ReactionShape.H2_N2_TO_NH3 )
            ) );
            addKit( new Kit( layoutBounds,
                             // reactants
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( Cl2, 3 ) );
                                 add( new MoleculeBucket( H2, 3 ) );
                             }},

                             // products
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( HCl, 0, 6 ) );
                             }},
                             Arrays.asList( ReactionShape.H2_Cl2_TO_HCl )
            ) );
//            addKit( new Kit( layoutBounds,
//                             // reactants
//                             new ArrayList<MoleculeBucket>() {{
//                                 add( new MoleculeBucket( ClNO2, 5 ) );
//                                 add( new MoleculeBucket( NO, 5 ) );
//                             }},
//
//                             // products
//                             new ArrayList<MoleculeBucket>() {{
//                                 add( new MoleculeBucket( NO2, 0 ) );
//                                 add( new MoleculeBucket( ClNO, 0 ) );
//                             }},
//                             new ArrayList<ReactionShape>()
//            ) );
//            addKit( new Kit( layoutBounds,
//                             // reactants
//                             new ArrayList<MoleculeBucket>() {{
//                                 add( new MoleculeBucket( CH4, 5 ) );
//                                 add( new MoleculeBucket( O2, 5 ) );
//                             }},
//
//                             // products
//                             new ArrayList<MoleculeBucket>() {{
//                                 add( new MoleculeBucket( CO2, 0 ) );
//                                 add( new MoleculeBucket( H2O, 0 ) );
//                             }},
//                             new ArrayList<ReactionShape>()
//            ) );
        }};

        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( final ClockEvent clockEvent ) {
                super.clockTicked( clockEvent );

                kitCollection.getCurrentKit().tick( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    public KitCollection getKitCollection() {
        return kitCollection;
    }
}
