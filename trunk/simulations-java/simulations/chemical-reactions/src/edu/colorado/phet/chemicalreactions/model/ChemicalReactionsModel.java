// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.*;

public class ChemicalReactionsModel {
    public final KitCollection kitCollection;
    private final LayoutBounds layoutBounds;

    public ChemicalReactionsModel( IClock clock, final LayoutBounds layoutBounds ) {
        this.layoutBounds = layoutBounds;

        kitCollection = new KitCollection() {{
            addKit( new Kit( layoutBounds,
                             // reactants
                             Arrays.asList(
                                     new MoleculeBucket( O2, 5 ),
                                     new MoleculeBucket( H2, 5 )
                             ),

                             // products
                             Arrays.asList(
                                     new MoleculeBucket( H2O, 5 )
                             ),

                             Arrays.asList( ReactionShape.H2_O2_TO_H2O )
            ) );
            addKit( new Kit( layoutBounds,
                             // reactants
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( N2, 5 ) );
                                 add( new MoleculeBucket( H2, 5 ) );
                             }},

                             // products
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( NH3, 5 ) );
                             }},
                             new ArrayList<ReactionShape>()
            ) );
            addKit( new Kit( layoutBounds,
                             // reactants
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( Cl2, 5 ) );
                                 add( new MoleculeBucket( H2, 5 ) );
                             }},

                             // products
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( HCl, 5 ) );
                             }},
                             new ArrayList<ReactionShape>()
            ) );
            addKit( new Kit( layoutBounds,
                             // reactants
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( ClNO2, 5 ) );
                                 add( new MoleculeBucket( NO, 5 ) );
                             }},

                             // products
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( NO2, 5 ) );
                                 add( new MoleculeBucket( ClNO, 5 ) );
                             }},
                             new ArrayList<ReactionShape>()
            ) );
            addKit( new Kit( layoutBounds,
                             // reactants
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( CH4, 5 ) );
                                 add( new MoleculeBucket( O2, 5 ) );
                             }},

                             // products
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( CO2, 5 ) );
                                 add( new MoleculeBucket( H2O, 5 ) );
                             }},
                             new ArrayList<ReactionShape>()
            ) );
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
